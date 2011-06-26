/**
 * This file is part of the Nilestore project.
 * 
 * Copyright (C) (2011) Nile University (NU)
 *
 * Nilestore is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eg.nileu.cis.nilestore.availablepeers.decentralized;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.p2p.bootstrap.BootstrapCompleted;
import se.sics.kompics.p2p.bootstrap.BootstrapRequest;
import se.sics.kompics.p2p.bootstrap.BootstrapResponse;
import se.sics.kompics.p2p.bootstrap.P2pBootstrap;
import se.sics.kompics.p2p.bootstrap.PeerEntry;
import se.sics.kompics.p2p.bootstrap.client.BootstrapClient;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.p2p.overlay.chord.Chord;
import se.sics.kompics.p2p.overlay.chord.ChordAddress;
import se.sics.kompics.p2p.overlay.chord.ChordConfiguration;
import se.sics.kompics.p2p.overlay.chord.ChordInit;
import se.sics.kompics.p2p.overlay.chord.ChordLookupRequest;
import se.sics.kompics.p2p.overlay.chord.ChordLookupResponse;
import se.sics.kompics.p2p.overlay.chord.ChordStructuredOverlay;
import se.sics.kompics.p2p.overlay.chord.CreateRing;
import se.sics.kompics.p2p.overlay.chord.JoinRing;
import se.sics.kompics.p2p.overlay.chord.JoinRingCompleted;
import se.sics.kompics.p2p.overlay.key.NumericRingKey;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.availablepeers.decentralized.port.DGetPeers;
import eg.nileu.cis.nilestore.availablepeers.decentralized.port.DGetPeersResponse;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.availablepeers.port.GetPeers;
import eg.nileu.cis.nilestore.common.NilestoreAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class ChordPeer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ChordPeer extends ComponentDefinition {

	/** The A aport. */
	Negative<AvailablePeers> AAport = provides(AvailablePeers.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The fd. */
	Positive<FailureDetector> fd = requires(FailureDetector.class);

	/** The bootstrap. */
	private Component chord, bootstrap;

	/** The self. */
	private NilestoreAddress self;

	/** The chord self. */
	private ChordAddress chordSelf;

	/** The bootstrap request peer count. */
	private int bootstrapRequestPeerCount;

	/** The bootstraped. */
	private boolean bootstraped;

	/** The active requests. */
	private final HashMap<NumericRingKey, GetPeers> activeRequests;

	/** The logger. */
	Logger logger = LoggerFactory.getLogger(ChordPeer.class);

	/**
	 * Instantiates a new chord peer.
	 */
	public ChordPeer() {

		// TODO: add an option for a node to contribute to the chord ring or not
		bootstraped = false;
		activeRequests = new HashMap<NumericRingKey, GetPeers>();

		chord = create(Chord.class);
		bootstrap = create(BootstrapClient.class);

		connect(network, chord.required(Network.class));
		connect(timer, chord.required(Timer.class));
		connect(fd, chord.required(FailureDetector.class));

		connect(bootstrap.required(Network.class), network);
		connect(bootstrap.required(Timer.class), timer);

		subscribe(handleInit, control);
		subscribe(handleBootstrapResponse,
				bootstrap.provided(P2pBootstrap.class));
		subscribe(handleJoinRingCompleted,
				chord.provided(ChordStructuredOverlay.class));

		subscribe(handleGetPeers, AAport);
		subscribe(handleChordLookupResponse,
				chord.provided(ChordStructuredOverlay.class));

	}

	/** The handle init. */
	Handler<ChordPeerInit> handleInit = new Handler<ChordPeerInit>() {

		@Override
		public void handle(ChordPeerInit init) {

			logger.info("init");

			ChordConfiguration config = init.getChordConfiguration();
			self = init.getSelf();
			bootstrapRequestPeerCount = config.getBootstrapRequestPeerCount();
			trigger(new ChordInit(config.getLog2RingSize(),
					config.getSuccessorListLength(),
					config.getSuccessorStabilizationPeriod(),
					config.getFingerStabilizationPeriod(),
					config.getRpcTimeout()), chord.getControl());

			NumericRingKey nodekey = new NumericRingKey(new BigInteger(
					self.getPeerIdBytes()));
			chordSelf = new ChordAddress(self.getPeerAddress(), nodekey);

			BootstrapRequest request = new BootstrapRequest("Chord",
					bootstrapRequestPeerCount);
			trigger(request, bootstrap.provided(P2pBootstrap.class));
		}
	};

	/** The handle bootstrap response. */
	Handler<BootstrapResponse> handleBootstrapResponse = new Handler<BootstrapResponse>() {
		@Override
		public void handle(BootstrapResponse event) {

			if (!bootstraped) {
				Set<PeerEntry> peers = event.getPeers();
				logger.info("bootstrap response {}", peers.size());

				if (peers.size() > 0) {
					LinkedList<ChordAddress> chordInsiders = new LinkedList<ChordAddress>();
					for (PeerEntry peerEntry : peers) {
						chordInsiders.add((ChordAddress) peerEntry
								.getOverlayAddress());
					}

					trigger(new JoinRing(chordSelf, chordInsiders),
							chord.provided(ChordStructuredOverlay.class));
				} else {
					trigger(new CreateRing(chordSelf),
							chord.provided(ChordStructuredOverlay.class));
				}

				bootstraped = true;
			}

		}
	};

	/** The handle join ring completed. */
	Handler<JoinRingCompleted> handleJoinRingCompleted = new Handler<JoinRingCompleted>() {
		public void handle(JoinRingCompleted event) {

			logger.info("joinring completed");
			trigger(new BootstrapCompleted("Chord", chordSelf),
					bootstrap.provided(P2pBootstrap.class));
		}
	};

	/** The handle get peers. */
	Handler<DGetPeers> handleGetPeers = new Handler<DGetPeers>() {

		@Override
		public void handle(DGetPeers event) {

			logger.info("getPeers");
			NumericRingKey ringKey = new NumericRingKey(new BigInteger(
					event.getPeerSelectionIndex()));
			activeRequests.put(ringKey, event);

			trigger(new ChordLookupRequest(ringKey, null),
					chord.provided(ChordStructuredOverlay.class));
		}
	};

	/** The handle chord lookup response. */
	Handler<ChordLookupResponse> handleChordLookupResponse = new Handler<ChordLookupResponse>() {

		@Override
		public void handle(ChordLookupResponse event) {

			logger.info("got chordlookup response");
			ChordAddress responsible = event.getResponsible();
			String peerId = Base32.encode(responsible.getKey().getId()
					.toByteArray());

			// FIXME: add an attached object "holding Nilestore Address" to the
			// chord address
			trigger(new DGetPeersResponse(
					(DGetPeers) activeRequests.remove(event.getKey()),
					new NilestoreAddress(responsible.getPeerAddress(), 8080,
							"", peerId, 0)), AAport);

		}

	};
}
