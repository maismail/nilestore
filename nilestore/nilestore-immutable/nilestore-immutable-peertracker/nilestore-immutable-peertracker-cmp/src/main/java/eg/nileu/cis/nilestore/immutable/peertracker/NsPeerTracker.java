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
package eg.nileu.cis.nilestore.immutable.peertracker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.connectionfd.port.CancelNotifyonFailure;
import eg.nileu.cis.nilestore.connectionfd.port.ConnectionFailure;
import eg.nileu.cis.nilestore.connectionfd.port.NotifyonFailure;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Abort;
import eg.nileu.cis.nilestore.immutable.peertracker.port.GetShares;
import eg.nileu.cis.nilestore.immutable.peertracker.port.GetSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.HaveShares;
import eg.nileu.cis.nilestore.immutable.peertracker.port.HaveSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.PeerTracker;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Query;
import eg.nileu.cis.nilestore.immutable.peertracker.port.QueryResponse;
import eg.nileu.cis.nilestore.storage.AbortBucket;
import eg.nileu.cis.nilestore.storage.port.network.AllocateBucket;
import eg.nileu.cis.nilestore.storage.port.network.AllocateBucketResponse;
import eg.nileu.cis.nilestore.storage.port.network.GetBuckets;
import eg.nileu.cis.nilestore.storage.port.network.GetBucketsResponse;
import eg.nileu.cis.nilestore.storage.port.network.HaveBuckets;
import eg.nileu.cis.nilestore.storage.port.network.HaveBucketsResponse;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerTracker.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeerTracker extends ComponentDefinition {

	/** The peer tracker. */
	Negative<PeerTracker> peerTracker = provides(PeerTracker.class);

	/** The net. */
	Positive<Network> net = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/**
	 * The Enum Operation.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	enum Operation {

		/** The Query. */
		Query,

		/** The Get shares. */
		GetShares,

		/** The Have shares. */
		HaveShares,

		/** The Abort. */
		Abort
	}

	/** The logger. */
	private Logger logger;// =LoggerFactory.getLogger(NsPeerTracker.class);

	/** The server address. */
	private NilestoreAddress serverAddress;

	/** The self. */
	private ComponentAddress self;

	/** The dest. */
	private ComponentAddress dest;

	/** The buckets. */
	private Map<Integer, ComponentAddress> buckets;

	/** The notify on failure event. */
	private NotifyonFailure notifyOnFailureEvent;

	/** The current operation. */
	private Operation currentOperation;

	/**
	 * Instantiates a new ns peer tracker.
	 */
	public NsPeerTracker() {
		subscribe(handleInit, control);

		subscribe(handleQuery, peerTracker);
		subscribe(handleGetShares, peerTracker);
		subscribe(handleHaveShares, peerTracker);
		subscribe(handleAbort, peerTracker);

		subscribe(handleAllocateBucketResponse, net);
		subscribe(handleGetBucketsResponse, net);
		subscribe(handleHaveBucketResponse, net);

		subscribe(handleFailure, cfd);

	}

	/** The handle init. */
	Handler<NsPeerTrackerInit> handleInit = new Handler<NsPeerTrackerInit>() {

		@Override
		public void handle(NsPeerTrackerInit init) {

			self = init.getSelf();
			dest = init.getDest();
			serverAddress = init.getServerAddress();
			logger = Slf4jInstantiator.getLogger(NsPeerTracker.class, init
					.getSelfAddress().getNickname());
			logger.info("({}) started for ({})", self, dest);
		}
	};

	/** The handle query. */
	Handler<Query> handleQuery = new Handler<Query>() {

		@Override
		public void handle(Query event) {
			FileInfo fileinfo = event.getFileInfo();
			logger.debug(
					"({}) query about storageIndex ({}) for sharenums {} on server ({})",
					new Object[] { self, fileinfo.getStorageIndex(),
							DumpUtils.dumptolog(event.getShareNums()), dest });

			notifyOnFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyOnFailureEvent, cfd);

			currentOperation = Operation.Query;

			trigger(new AllocateBucket(self, dest, fileinfo.getStorageIndex(),
					fileinfo.getBucketRenewSecret(serverAddress.getPeerId()),
					fileinfo.getBucketCancelSecret(serverAddress.getPeerId()),
					fileinfo.getAllocatedSize(), event.getShareNums()), net);
		}
	};

	/** The handle allocate bucket response. */
	Handler<AllocateBucketResponse> handleAllocateBucketResponse = new Handler<AllocateBucketResponse>() {

		@Override
		public void handle(AllocateBucketResponse event) {

			trigger(new CancelNotifyonFailure(notifyOnFailureEvent), cfd);

			logger.debug(
					"({}) got AllocateBucketResponse with buckets {} from {}",
					new Object[] { self,
							DumpUtils.dumptolog(event.getShareMap()), dest });

			buckets = new HashMap<Integer, ComponentAddress>();
			for (int sharenum : event.getShareMap().keySet()) {
				buckets.put(sharenum, new ComponentAddress(dest.getAddress(),
						event.getShareMap().get(sharenum)));
			}
			trigger(new QueryResponse(Status.Succeeded, self.getId(),
					serverAddress, buckets, event.getAlreadygot()), peerTracker);
		}

	};

	/** The handle get shares. */
	Handler<GetShares> handleGetShares = new Handler<GetShares>() {

		@Override
		public void handle(GetShares event) {

			logger.debug(
					"({}) get share readers for storageIndex ({}) from server ({})",
					new Object[] { self, event.getStorageIndex(), dest });

			notifyOnFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyOnFailureEvent, cfd);

			currentOperation = Operation.GetShares;

			trigger(new GetBuckets(self, dest, event.getStorageIndex()), net);
		}
	};

	/** The handle get buckets response. */
	Handler<GetBucketsResponse> handleGetBucketsResponse = new Handler<GetBucketsResponse>() {

		@Override
		public void handle(GetBucketsResponse event) {

			trigger(new CancelNotifyonFailure(notifyOnFailureEvent), cfd);

			logger.debug("({}) got BucketsResponse with buckets {} from ({})",
					new Object[] { self,
							DumpUtils.dumptolog(event.getShares()), dest });

			HashMap<Integer, ComponentAddress> rbps = new HashMap<Integer, ComponentAddress>();
			for (int sharenum : event.getShares().keySet()) {
				rbps.put(sharenum, new ComponentAddress(dest.getAddress(),
						event.getShares().get(sharenum)));
			}

			trigger(new GetSharesResponse(Status.Succeeded, self.getId(),
					serverAddress, rbps), peerTracker);
		}

	};

	/** The handle have shares. */
	Handler<HaveShares> handleHaveShares = new Handler<HaveShares>() {
		@Override
		public void handle(HaveShares event) {

			logger.debug(
					"({}) send have request for storageIndex ({}) to server ({})",
					new Object[] { self, event.getStorageIndex(), dest });

			notifyOnFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyOnFailureEvent, cfd);

			currentOperation = Operation.HaveShares;

			trigger(new HaveBuckets(self, dest, event.getStorageIndex()), net);
		}
	};

	/** The handle have bucket response. */
	Handler<HaveBucketsResponse> handleHaveBucketResponse = new Handler<HaveBucketsResponse>() {
		@Override
		public void handle(HaveBucketsResponse event) {

			trigger(new CancelNotifyonFailure(notifyOnFailureEvent), cfd);
			logger.debug("({}) got have response with shares {} from ({})",
					new Object[] { self,
							DumpUtils.dumptolog(event.getShares()), dest });

			trigger(new HaveSharesResponse(Status.Succeeded, self.getId(),
					serverAddress, event.getShares()), peerTracker);
		}
	};

	/** The handle failure. */
	Handler<ConnectionFailure> handleFailure = new Handler<ConnectionFailure>() {
		@Override
		public void handle(ConnectionFailure event) {
			logger.warn(
					"({}) failed to get response from ({}) at {} operation",
					new Object[] { self, dest, currentOperation });

			switch (currentOperation) {
			case Query:
				trigger(new QueryResponse(Status.Failed, self.getId(),
						serverAddress,
						new HashMap<Integer, ComponentAddress>(),
						new HashSet<Integer>()), peerTracker);
				break;
			case GetShares:
				trigger(new GetSharesResponse(Status.Failed, self.getId(),
						serverAddress, new HashMap<Integer, ComponentAddress>()),
						peerTracker);
				break;
			case HaveShares:
				trigger(new HaveSharesResponse(Status.Failed, self.getId(),
						serverAddress, new HashSet<Integer>()), peerTracker);
				break;
			default:
				break;
			}
		}
	};

	/** The handle abort. */
	Handler<Abort> handleAbort = new Handler<Abort>() {
		@Override
		public void handle(Abort event) {

			Set<Integer> bucketsToAbort = event.isAbortForAll() ? buckets
					.keySet() : event.getBuckets();
			logger.debug(
					"({}) send abort for shares writers {} on server ({})",
					new Object[] { self, DumpUtils.dumptolog(bucketsToAbort),
							dest });
			for (int bucket : bucketsToAbort) {
				trigger(new AbortBucket(self, buckets.get(bucket)), net);
			}
		}
	};
}
