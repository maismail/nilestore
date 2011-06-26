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
package eg.nileu.cis.nilestore.immutable.downloader.sharefinder.tahoe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeers;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeersResponse;
import eg.nileu.cis.nilestore.availablepeers.port.RemovePeer;
import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.Share;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.FindShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.GotShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.NoMoreShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.ShareFinder;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.WantMoreShares;
import eg.nileu.cis.nilestore.immutable.peertracker.NsPeerTrackersHolder;
import eg.nileu.cis.nilestore.immutable.peertracker.port.GetShares;
import eg.nileu.cis.nilestore.immutable.peertracker.port.GetSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.PeerTracker;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsShareFinder.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsShareFinder extends NsPeerTrackersHolder {

	/** The sharefinder. */
	Negative<ShareFinder> sharefinder = provides(ShareFinder.class);

	/** The logger. */
	private Logger logger; // = LoggerFactory.getLogger(NsShareFinder.class);

	/** The running. */
	private boolean running;

	/** The hungry. */
	private boolean hungry;

	/** The self. */
	private NilestoreAddress self;

	/** The serverslist. */
	private List<NilestoreAddress> serverslist;

	/** The servers. */
	private Iterator<NilestoreAddress> servers;

	/** The storage index. */
	private String storageIndex;

	/** The max outstanding requests. */
	private int maxOutstandingRequests;

	/** The pending requests. */
	private Map<String, Long> pendingRequests;

	/**
	 * Instantiates a new ns share finder.
	 */
	public NsShareFinder() {
		serverslist = new ArrayList<NilestoreAddress>();
		pendingRequests = new HashMap<String, Long>();

		subscribe(handleInit, control);
		subscribe(handleStart, control);
		subscribe(handleStop, control);
		subscribe(handleGetShareReaders, sharefinder);
		subscribe(handleGetPeersResponse, availablePeers);
		subscribe(handleWantMoreShares, sharefinder);
	}

	/** The handle init. */
	Handler<NsShareFinderInit> handleInit = new Handler<NsShareFinderInit>() {
		@Override
		public void handle(NsShareFinderInit init) {
			self = init.getSelf();
			maxOutstandingRequests = init.getMaxOutstandingRequests();
			storageIndex = init.getStorageIndex();
			hungry = false;
			running = true;
			logger = Slf4jInstantiator.getLogger(NsShareFinder.class,
					self.getNickname());
			logger.info("(SI={}): initiated", storageIndex);
		}
	};

	/** The handle get share readers. */
	Handler<FindShares> handleGetShareReaders = new Handler<FindShares>() {

		@Override
		public void handle(FindShares event) {
			hungry = true;
			trigger(new CGetPeers(new Agent("ShareFinder", 0), storageIndex),
					availablePeers);
			logger.debug("(SI={}): Asking for AvailablePeers permuted by ({})",
					storageIndex, storageIndex);
		}
	};

	/** The handle get peers response. */
	Handler<CGetPeersResponse> handleGetPeersResponse = new Handler<CGetPeersResponse>() {

		@Override
		public void handle(CGetPeersResponse event) {

			int NumofPeers = event.getPeers().size();
			logger.debug(
					"(SI={}): GotPeers from AvailablePeers {}, (size={})",
					new Object[] { storageIndex,
							DumpUtils.dumptolog(event.getPeers()), NumofPeers });
			if (NumofPeers == 0) {
				logger.warn("there are no storage servers to download from");
				trigger(new GotShares(
						new StatusMsg(Status.Failed,
								"Downloading Failed: there are no storage servers to download from"),
						null), sharefinder);
			} else {
				serverslist = event.getPeers();
				servers = serverslist.iterator();
				loop();
			}
		}
	};

	/**
	 * Loop.
	 */
	private void loop() {

		logger.debug(
				"(SI={}): running={}, hungry={}, pendingRequests.size={}",
				new Object[] { storageIndex, String.valueOf(running),
						String.valueOf(hungry), pendingRequests.size() });
		if (!running)
			return;
		if (!hungry)
			return;

		if (pendingRequests.size() >= maxOutstandingRequests)
			return;

		NilestoreAddress server = null;
		if (servers.hasNext()) {
			server = servers.next();
		}

		if (server != null) {
			sendRequest(server);
			return;
		}

		if (!pendingRequests.isEmpty())
			return;

		trigger(new NoMoreShares(), sharefinder);
	}

	/**
	 * Send request.
	 * 
	 * @param server
	 *            the server
	 */
	private void sendRequest(NilestoreAddress server) {

		PeerTrackerEntry pt = createAndStartPeerTracker(null, null,
				handleGetSharesResponse, storageIndex, self, server);
		long time = System.nanoTime();
		synchronized (pendingRequests) {
			pendingRequests.put(server.getPeerId(), time);
		}
		trigger(new GetShares(storageIndex),
				pt.getPeerTracker().provided(PeerTracker.class));
		logger.debug("(SI={}): send GetShares request to {}", storageIndex,
				server);
		loop();
	}

	/** The handle get shares response. */
	Handler<GetSharesResponse> handleGetSharesResponse = new Handler<GetSharesResponse>() {
		@Override
		public void handle(GetSharesResponse event) {

			NilestoreAddress serverAddress = event.getServerAddress();
			Long t1;
			synchronized (pendingRequests) {
				t1 = pendingRequests.remove(serverAddress.getPeerId());
			}

			if (t1 == null) {
				logger.debug(
						"UNUSUAL:(SI={}): we got request from {} although it is not in our pendingRequests",
						storageIndex, serverAddress);
				return;
			}

			if (event.isSucceeded()) {
				long rtt = System.nanoTime() - t1;
				Map<Integer, ComponentAddress> buckets = event
						.getSharesReader();

				logger.debug(
						"(SI={}): got response for GetShares request from {}, RTT={}msec, shares.size={}",
						new Object[] { storageIndex, serverAddress, rtt * 1e-6,
								buckets.size() });

				List<Share> shares = new ArrayList<Share>();
				for (Map.Entry<Integer, ComponentAddress> b : buckets
						.entrySet()) {
					Share share = new Share(b.getKey(), rtt, b.getValue());
					shares.add(share);
				}

				if (shares.isEmpty()) {
					hungry = true;
					loop();
				} else {
					hungry = false;
					trigger(new GotShares(new StatusMsg(Status.Succeeded, ""),
							shares), sharefinder);
				}
			} else {
				logger.debug(
						"(SI={}): {} doesn't respond to my GetShares request it may be failed",
						storageIndex, event.getServerAddress());
				trigger(new RemovePeer(event.getServerAddress()),
						availablePeers);
			}
		}
	};

	/** The handle want more shares. */
	Handler<WantMoreShares> handleWantMoreShares = new Handler<WantMoreShares>() {
		@Override
		public void handle(WantMoreShares event) {
			logger.info("(SI={}): my parent want more shares", storageIndex);
			if (!hungry) {
				hungry = true;
			}
			loop();
		}
	};

	/** The handle stop. */
	Handler<Stop> handleStop = new Handler<Stop>() {
		@Override
		public void handle(Stop event) {
			running = false;
		}
	};

	/** The handle start. */
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {
			running = true;
		}
	};
}
