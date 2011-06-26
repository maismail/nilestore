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
package eg.nileu.cis.nilestore.immutable.uploader.peerselector.tahoe2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeers;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeersResponse;
import eg.nileu.cis.nilestore.availablepeers.port.RemovePeer;
import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.immutable.peertracker.NsPeerTrackersHolder;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Abort;
import eg.nileu.cis.nilestore.immutable.peertracker.port.HaveShares;
import eg.nileu.cis.nilestore.immutable.peertracker.port.HaveSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.PeerTracker;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Query;
import eg.nileu.cis.nilestore.immutable.peertracker.port.QueryResponse;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeers;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeersResponse;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PeerSelector;
import eg.nileu.cis.nilestore.utils.Barrier;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsTahoe2PeerSelector.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsTahoe2PeerSelector extends NsPeerTrackersHolder {

	/** The peer selectorport. */
	Negative<PeerSelector> peerSelectorport = provides(PeerSelector.class);

	/** The logger. */
	private Logger logger;

	/** The self. */
	NilestoreAddress self;

	/** The request event. */
	PSGetPeers requestEvent;

	/** The SI. */
	String SI;

	/** The self id. */
	String selfId;

	/** The homeless shares. */
	LinkedList<Integer> homelessShares;

	/** The uncontacted peers. */
	LinkedList<NilestoreAddress> uncontactedPeers;

	/** The contacted peers. */
	LinkedList<PeerTrackerAddress> contactedPeers;

	/** The contacted peers2. */
	LinkedList<PeerTrackerAddress> contactedPeers2;

	/** The peers with shares. */
	HashSet<NilestoreAddress> peersWithShares;

	/** The pre existing shares. */
	HashMap<Address, Set<Integer>> preExistingShares;

	/** The already got. */
	HashSet<Integer> alreadyGot;

	/** The upload servers. */
	HashMap<Integer, ComponentAddress> uploadServers;

	/** The allocated shareto peer tracker id. */
	HashMap<Integer, String> allocatedSharetoPeerTrackerId;

	/** The shares_to_ask. */
	HashSet<Integer> shares_to_ask;

	/** The num contacted peers. */
	int numContactedPeers;

	/** The queries count. */
	int queriesCount;

	/** The full count. */
	int fullCount;

	/** The bad query count. */
	int badQueryCount;

	/** The good query count. */
	int goodQueryCount;

	/** The error count. */
	int errorCount;

	/** The barrier. */
	Barrier barrier;

	/** The start second pass. */
	boolean startSecondPass;

	/**
	 * Instantiates a new ns tahoe2 peer selector.
	 */
	public NsTahoe2PeerSelector() {
		homelessShares = new LinkedList<Integer>();
		uncontactedPeers = new LinkedList<NilestoreAddress>();
		contactedPeers = new LinkedList<PeerTrackerAddress>();
		contactedPeers2 = new LinkedList<PeerTrackerAddress>();
		peersWithShares = new HashSet<NilestoreAddress>();
		preExistingShares = new HashMap<Address, Set<Integer>>();
		alreadyGot = new HashSet<Integer>();
		uploadServers = new HashMap<Integer, ComponentAddress>();
		allocatedSharetoPeerTrackerId = new HashMap<Integer, String>();
		shares_to_ask = new HashSet<Integer>();

		numContactedPeers = 0;
		queriesCount = 0;
		fullCount = 0;
		badQueryCount = 0;
		goodQueryCount = 0;
		errorCount = 0;

		startSecondPass = false;

		selfId = "Tahoe2PeerSelector";

		subscribe(handleInit, control);
		subscribe(handleGetPeers, peerSelectorport);
		subscribe(handleGetPeersResponse, availablePeers);

	}

	/** The handle init. */
	Handler<NsTahoe2PeerSelectorInit> handleInit = new Handler<NsTahoe2PeerSelectorInit>() {

		@Override
		public void handle(NsTahoe2PeerSelectorInit init) {

			self = init.getSelf();
			logger = Slf4jInstantiator.getLogger(NsTahoe2PeerSelector.class,
					self.getNickname());
		}
	};

	/** The handle get peers. */
	Handler<PSGetPeers> handleGetPeers = new Handler<PSGetPeers>() {

		@Override
		public void handle(PSGetPeers event) {

			requestEvent = event;
			SI = requestEvent.getFileInfo().getStorageIndex();

			byte[] peerSelectionIndex = Base32.decode(requestEvent
					.getFileInfo().getStorageIndex());
			trigger(new CGetPeers(new Agent(selfId, 0), peerSelectionIndex),
					availablePeers);

			logger.info("(SI={}): PeerSelection started", SI);
			logger.debug("(SI={}): Asking for AvailablePeers permuted by ({})",
					SI, SI);
		}

	};

	/** The handle get peers response. */
	Handler<CGetPeersResponse> handleGetPeersResponse = new Handler<CGetPeersResponse>() {

		@Override
		public void handle(CGetPeersResponse event) {

			logger.debug(
					"(SI={}): got GetPeers response from NsAvailablePeers", SI);
			List<NilestoreAddress> servers = event.getPeers();
			if (servers.size() == 0) {
				trigger(new PSGetPeersResponse(new StatusMsg(Status.Failed,
						"no servers error"), uploadServers, preExistingShares),
						peerSelectorport);
				return;
			}
			logger.debug("(SI={}): got servers {} from NsAvailablePeers", SI,
					DumpUtils.dumptolog(servers));

			FileInfo fileInfo = requestEvent.getFileInfo();

			long allocatedSize = fileInfo.getAllocatedSize();
			int totalShares = fileInfo.getEncodingParam().getN();

			for (int i = 0; i < totalShares; i++) {
				homelessShares.push(i);
			}

			int num_readonly_peers = 0;
			for (NilestoreAddress peer : servers) {
				if (peer.getAvailableSpace() < allocatedSize) {
					if (num_readonly_peers >= (totalShares * 2))
						continue;
					PeerTrackerEntry pte = createAndStartPeerTracker(null,
							handleHaveSharesResponse, null, SI, self, peer);
					trigger(new HaveShares(SI),
							pte.getPeerTracker().provided(PeerTracker.class));
					num_readonly_peers++;
					numContactedPeers++;
					queriesCount++;

				} else {
					uncontactedPeers.push(peer);
				}
			}

			barrier = new Barrier(num_readonly_peers);
			if (barrier.isFilled()) {
				loop();
			}
		}
	};

	/*
	 * Ask about Existing Shares
	 */
	/** The handle have shares response. */
	Handler<HaveSharesResponse> handleHaveSharesResponse = new Handler<HaveSharesResponse>() {

		@Override
		public void handle(HaveSharesResponse event) {

			if (event.getStatus().equals(Status.Succeeded)) {

				Set<Integer> alreadygot = event.getShares();
				logger.debug(
						"(SI={}): response to haveBuckets from peer {}: alreadygot={}",
						new Object[] { SI, event.getServerAddress(),
								DumpUtils.dumptolog(alreadygot) });

				if (event.hasShares()) {
					/*
					 * for (int share : alreadygot) {
					 * if(!preExistingShares.containsKey(share))
					 * preExistingShares.put(share, new
					 * HashSet<NilestoreAddress>());
					 * preExistingShares.get(share)
					 * .add(event.getServerAddress());
					 * 
					 * homelessShares.remove(share); }
					 */

					Address address = event.getServerAddress().getPeerAddress();
					if (!preExistingShares.containsKey(address))
						preExistingShares.put(address, new HashSet<Integer>());

					preExistingShares.get(address).addAll(alreadygot);
					homelessShares.removeAll(alreadygot);
				}

				fullCount++;
				badQueryCount++;

			} else {

				logger.warn("(SI={}): peer {} failed ", SI,
						event.getServerAddress());
				trigger(new RemovePeer(event.getServerAddress()),
						availablePeers);
				errorCount++;
			}

			destroyPeerTracker(event.getPeerTrackerId());
			barrier.setTrue();
			if (barrier.isFilled()) {
				// barrier.reset(uncontactedPeers.size());
				loop();
			}
		}
	};

	/**
	 * Loop.
	 */
	private void loop() {
		if (homelessShares.isEmpty()) {
			// TODO: server of happiness computation
			// trigger GetPeersResponseforSI
			checkAfterAllocation();
			trigger(new PSGetPeersResponse(new StatusMsg(Status.Succeeded,
					"all homeless shares are placed"), uploadServers,
					preExistingShares), peerSelectorport);
			destroyAllPeerTrackers();
			return;
		}

		if (!uncontactedPeers.isEmpty()) {
			NilestoreAddress peer = uncontactedPeers.removeLast();

			shares_to_ask.clear();
			shares_to_ask.add(homelessShares.pop());

			queriesCount++;
			numContactedPeers++;

			PeerTrackerEntry pte = createAndStartPeerTracker(
					handleQueryResponse, null, null, SI, self, peer);
			trigger(new Query(requestEvent.getFileInfo(), shares_to_ask), pte
					.getPeerTracker().provided(PeerTracker.class));
		} else if (!contactedPeers.isEmpty()) {
			if (!startSecondPass) {
				logger.debug("starting second pass");
				startSecondPass = true;
			}

			int numShares = (int) MathUtils.div_ceil(homelessShares.size(),
					contactedPeers.size());
			shares_to_ask.clear();
			for (int i = 0; i < numShares; i++) {
				shares_to_ask.add(homelessShares.pop());
			}

			queriesCount++;

			PeerTrackerAddress peer = contactedPeers.removeLast();
			Component pt = getPeerTracker(peer.getPeerTrackerId());
			if (pt != null) {
				trigger(new Query(requestEvent.getFileInfo(), shares_to_ask),
						pt.provided(PeerTracker.class));
			} else {
				// somehow peerTracker component is destroyed so create a new
				// one
				PeerTrackerEntry pte = createAndStartPeerTracker(
						handleQueryResponse, null, null, SI, self,
						peer.getPeerAddress());
				trigger(new Query(requestEvent.getFileInfo(), shares_to_ask),
						pte.getPeerTracker().provided(PeerTracker.class));
			}
		} else if (!contactedPeers2.isEmpty()) {
			contactedPeers.addAll(contactedPeers2);
			contactedPeers2.clear();
		} else {
			// no more peers are available
			// TODO: server of happiness should be here
			checkAfterAllocation();
			trigger(new PSGetPeersResponse(new StatusMsg(Status.Succeeded,
					"no more peers are available"), uploadServers,
					preExistingShares), peerSelectorport);
			destroyAllPeerTrackers();
			return;
		}

	}

	/** The handle query response. */
	Handler<QueryResponse> handleQueryResponse = new Handler<QueryResponse>() {

		@Override
		public void handle(QueryResponse event) {

			boolean destroy = false;
			if (event.getStatus().equals(Status.Succeeded)) {

				Map<Integer, ComponentAddress> allocated = event.getSharemap();
				Set<Integer> alreadygot = event.getAlreadygot();

				logger.debug(
						"(SI={}): response for allocateBuckets from peer {}: allocated = {}, alreadygot ={}",
						new Object[] { SI, event.getServerAddress(),
								DumpUtils.dumptolog(allocated.keySet()),
								DumpUtils.dumptolog(alreadygot) });

				boolean progress = false;

				if (!alreadygot.isEmpty()) {
					Address address = event.getServerAddress().getPeerAddress();
					if (!preExistingShares.containsKey(address))
						preExistingShares.put(address, new HashSet<Integer>());

					preExistingShares.get(address).addAll(alreadygot);
					alreadyGot.addAll(alreadygot);
				}

				for (int share : alreadygot) {
					/*
					 * if(!preExistingShares.containsKey(share))
					 * preExistingShares.put(share, new
					 * HashSet<NilestoreAddress>());
					 * preExistingShares.get(share)
					 * .add(event.getServerAddress());
					 */

					if (homelessShares.contains(share)) {
						homelessShares.remove((Object) share);
						progress = true;
					} else if (shares_to_ask.contains(share)) {
						progress = true;
					}
				}

				for (int share : allocated.keySet()) {
					allocatedSharetoPeerTrackerId.put(share,
							event.getPeerTrackerId());
				}

				if (!allocated.isEmpty()) {
					uploadServers.putAll(allocated);
					progress = true;
				}

				if (!allocated.isEmpty() || !alreadygot.isEmpty()) {
					peersWithShares.add(event.getServerAddress());
				}

				/*
				 * not_yet_present = set(shares_to_ask) - set(alreadygot)
				 * still_homeless = not_yet_present - set(allocated)
				 */
				Set<Integer> stillHomeless = new HashSet<Integer>(shares_to_ask);
				stillHomeless.removeAll(alreadygot);
				stillHomeless.removeAll(allocated.keySet());

				if (progress) {
					goodQueryCount++;
				} else {
					badQueryCount++;
					fullCount++;
				}

				if (!stillHomeless.isEmpty()) {
					for (int share : stillHomeless) {
						homelessShares.push(share);
					}
				} else {
					/*
					 * if they accept everything then
					 */
					PeerTrackerAddress peer = new PeerTrackerAddress(
							event.getPeerTrackerId(), event.getServerAddress());
					if (startSecondPass) {
						contactedPeers2.addLast(peer);
					} else {
						contactedPeers.addLast(peer);
					}
				}

			} else {

				logger.warn(
						"(SI={}): error during peer selection {} could be failed",
						SI, event.getServerAddress());
				trigger(new RemovePeer(event.getServerAddress()),
						availablePeers);

				destroy = true;
				errorCount++;
				badQueryCount++;
				homelessShares.addAll(shares_to_ask);
			}

			if (destroy) {
				destroyPeerTracker(event.getPeerTrackerId());
			}

			loop();
		}
	};

	/**
	 * Check after allocation.
	 */
	private void checkAfterAllocation() {
		// After allocation check if this share is already existed in another
		// node
		// if it's then abort that share and remove it from the allocated
		if (!alreadyGot.isEmpty()) {
			Set<Integer> shares = new HashSet<Integer>(uploadServers.keySet());
			for (int share : shares) {
				if (alreadyGot.contains(share)) {
					uploadServers.remove(share);
					Component pt = getPeerTracker(allocatedSharetoPeerTrackerId
							.get(share));
					if (pt == null) {
						// in that case the peer is already dead so we don't
						// need to send abort
					} else {
						trigger(new Abort(share),
								pt.provided(PeerTracker.class));
					}
				}
			}
		}
	}
}
