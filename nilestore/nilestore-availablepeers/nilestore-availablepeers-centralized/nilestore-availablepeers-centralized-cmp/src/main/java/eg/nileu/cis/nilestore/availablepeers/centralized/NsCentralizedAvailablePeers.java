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
package eg.nileu.cis.nilestore.availablepeers.centralized;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.p2p.overlay.OverlayAddress;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeers;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeersResponse;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.availablepeers.port.RemovePeer;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.introducer.Service;
import eg.nileu.cis.nilestore.introducer.StorageService;
import eg.nileu.cis.nilestore.introducer.port.Announce;
import eg.nileu.cis.nilestore.introducer.port.Publish;
import eg.nileu.cis.nilestore.introducer.port.Subscribe;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsCentralizedAvailablePeers.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsCentralizedAvailablePeers extends ComponentDefinition {

	// TODO:status port
	/** The available peers. */
	Negative<AvailablePeers> availablePeers = provides(AvailablePeers.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The fd. */
	Positive<FailureDetector> fd = requires(FailureDetector.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The logger. */
	private Logger logger;// =LoggerFactory.getLogger(NsCentralizedAvailablePeers.class);

	/** The servers. */
	private Set<NilestoreAddress> servers;

	/** The address2 ns address. */
	private HashMap<Address, NilestoreAddress> address2NsAddress;

	/** The self. */
	private NilestoreAddress self;

	/** The is storage server. */
	private boolean isStorageServer;

	/** The introducer. */
	private Address introducer;

	/**
	 * Instantiates a new ns centralized available peers.
	 */
	public NsCentralizedAvailablePeers() {
		servers = new HashSet<NilestoreAddress>();
		address2NsAddress = new HashMap<Address, NilestoreAddress>();
		subscribe(handleInit, control);
		subscribe(handleAnnounce, network);
		subscribe(handleGetPeers, availablePeers);
		subscribe(handleRemovePeer, availablePeers);

	}

	/** The handle init. */
	Handler<NsCentralizedAvailablePeersInit> handleInit = new Handler<NsCentralizedAvailablePeersInit>() {

		@Override
		public void handle(NsCentralizedAvailablePeersInit init) {
			self = init.getSelf();
			logger = Slf4jInstantiator.getLogger(
					NsCentralizedAvailablePeers.class, self.getNickname());

			isStorageServer = init.isStorageServer();
			introducer = init.getIntroducerConfiguration()
					.getIntroducerAddress();

			logger.info("started with storage sever {} ",
					isStorageServer ? "enabled" : "disabled");

			Service service = new StorageService();
			if (isStorageServer) {
				trigger(new Publish(self.getPeerAddress(), introducer, service,
						self), network);
			}
			trigger(new Subscribe(self.getPeerAddress(), introducer, service),
					network);

		}

	};

	/** The handle announce. */
	Handler<Announce> handleAnnounce = new Handler<Announce>() {
		@Override
		public void handle(Announce event) {

			Set<OverlayAddress> peers = event.getPeers();
			logger.info("got announcement from introducer with peers {}",
					DumpUtils.dumptolog(peers));

			synchronized (servers) {
				for (OverlayAddress peer : peers) {
					NilestoreAddress p = (NilestoreAddress) peer;
					servers.add(p);
					address2NsAddress.put(peer.getPeerAddress(), p);
				}
			}
			dumptolog();
		}
	};

	/** The handle get peers. */
	Handler<CGetPeers> handleGetPeers = new Handler<CGetPeers>() {

		@Override
		public void handle(CGetPeers event) {

			logger.info("got GetPeers request from {}", event.getRequestAgent());

			List<NilestoreAddress> peers;
			synchronized (servers) {
				peers = new ArrayList<NilestoreAddress>(servers);
			}

			int len = event.getLength();
			byte[] peerSelectionIndex = event.getPeerSelectionIndex();

			if (peerSelectionIndex != null)
				logger.debug(
						"got GetPeers request with peerSelectionIndex = {}, response is {}",
						Base32.encode(peerSelectionIndex),
						event.isLimited() ? "limited to " + event.getLength()
								: "unlimited");
			Collections.sort(peers, new ServersComparator(peerSelectionIndex));

			if (event.isLimited())
				peers = peers.size() > len ? peers.subList(0, len) : peers;

			trigger(new CGetPeersResponse(peers, event), availablePeers);
		}
	};

	/** The handle remove peer. */
	Handler<RemovePeer> handleRemovePeer = new Handler<RemovePeer>() {
		@Override
		public void handle(RemovePeer event) {
			NilestoreAddress peer = null;
			if (event.getNilestoreAddress() == null) {
				Address addr = event.getAddress();
				synchronized (address2NsAddress) {
					peer = address2NsAddress.remove(addr);
				}
			}
			if (peer == null) {
				logger.debug("peer {} doesn't exists in the available peers",
						event.getAddress());
				return;
			}

			logger.info("remove peer {} because it has failed", peer);
			synchronized (servers) {
				servers.remove(peer);
			}
			dumptolog();
		}
	};

	/**
	 * The Class ServersComparator.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	class ServersComparator implements Comparator<NilestoreAddress> {

		/** The peer selection index. */
		private final byte[] peerSelectionIndex;

		/**
		 * Instantiates a new servers comparator.
		 * 
		 * @param peerSelectionIndex
		 *            the peer selection index
		 */
		public ServersComparator(byte[] peerSelectionIndex) {
			this.peerSelectionIndex = peerSelectionIndex;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(NilestoreAddress arg0, NilestoreAddress arg1) {

			BigInteger b1 = new BigInteger(Hash.sha1_hash(
					arg0.getPeerIdBytes(), peerSelectionIndex));
			BigInteger b2 = new BigInteger(Hash.sha1_hash(
					arg1.getPeerIdBytes(), peerSelectionIndex));
			return b1.compareTo(b2);
		}
	}

	/**
	 * Dumptolog.
	 */
	private void dumptolog() {
		Set<NilestoreAddress> peers;
		synchronized (servers) {
			peers = new HashSet<NilestoreAddress>(servers);
		}

		logger.info("Current List of Servers ");
		logger.info("=====================================================");
		for (NilestoreAddress add : peers) {
			logger.info("\t" + add);
		}
		logger.info("=====================================================");
	}

}
