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
package eg.nileu.cis.nilestore.storage.server;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.channelfilters.ExtMessageDestinationFilter;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.storage.AbstractStorageServer;
import eg.nileu.cis.nilestore.storage.LeaseInfo;
import eg.nileu.cis.nilestore.storage.common;
import eg.nileu.cis.nilestore.storage.immutable.reader.NsBucketReader;
import eg.nileu.cis.nilestore.storage.immutable.reader.NsBucketReaderInit;
import eg.nileu.cis.nilestore.storage.immutable.writer.NsBucketWriter;
import eg.nileu.cis.nilestore.storage.immutable.writer.NsBucketWriterInit;
import eg.nileu.cis.nilestore.storage.port.network.AllocateBucket;
import eg.nileu.cis.nilestore.storage.port.network.AllocateBucketResponse;
import eg.nileu.cis.nilestore.storage.port.network.GetBuckets;
import eg.nileu.cis.nilestore.storage.port.network.GetBucketsResponse;
import eg.nileu.cis.nilestore.storage.port.network.HaveBuckets;
import eg.nileu.cis.nilestore.storage.port.network.HaveBucketsResponse;
import eg.nileu.cis.nilestore.storage.port.status.SIStatusItem;
import eg.nileu.cis.nilestore.storage.port.status.StorageServerStatus;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusRequest;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusResponse;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusView;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.Tuple;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsStorageServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsStorageServer extends AbstractStorageServer {

	/** The ss status. */
	Negative<StorageServerStatus> ssStatus = provides(StorageServerStatus.class);

	/** The net. */
	Positive<Network> net = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The home dir. */
	private String homeDir;

	/** The shares dir. */
	private String sharesDir;

	/** The incoming dir. */
	private String incomingDir;

	/** The self. */
	private NilestoreAddress self;

	/** The activebuckets. */
	private HashMap<String, Component> activebuckets;

	/** The bucktes last access time. */
	private HashMap<String, Tuple<Long, Long>> bucktesLastAccessTime;

	/** The concurrent readers. */
	private HashMap<String, Integer> concurrentReaders;

	/** The logger. */
	private static Logger logger; // =
									// LoggerFactory.getLogger(NsStorageServer.class);

	// TODO: add stats and latency computation
	// TODO: add leases management (Add, Renew, Cancel)

	/**
	 * Instantiates a new ns storage server.
	 */
	public NsStorageServer() {
		super();
		activebuckets = new HashMap<String, Component>();
		bucktesLastAccessTime = new HashMap<String, Tuple<Long, Long>>();
		concurrentReaders = new HashMap<String, Integer>();

		subscribe(handleInit, control);
		subscribe(handleAllocateBucket, net);
		subscribe(handleGetBuckets, net);
		subscribe(handleHaveRequest, net);

		subscribe(handleGetStatus, ssStatus);
	}

	/** The handle init. */
	Handler<NsStorageServerInit> handleInit = new Handler<NsStorageServerInit>() {

		@Override
		public void handle(NsStorageServerInit init) {

			homeDir = init.getStorageserverConfiguration().getHomeDir();
			self = init.getStorageserverConfiguration().getSelf();

			logger = Slf4jInstantiator.getLogger(NsStorageServer.class,
					self.getNickname());
			logger.info("initiated with homeDir " + homeDir);

			sharesDir = FileUtils.JoinPath(homeDir, "storage", "shares");
			incomingDir = FileUtils.JoinPath(sharesDir, "incoming");
			FileUtils.mkdirs(sharesDir);

			clean_incomplete();

			FileUtils.mkdirs(incomingDir);

		}

	};

	/** The handle allocate bucket. */
	Handler<AllocateBucket> handleAllocateBucket = new Handler<AllocateBucket>() {

		@Override
		public void handle(AllocateBucket event) {

			String storageIndex = event.getStorageIndex();
			String storageIndexDir = common.storage_index_to_dir(storageIndex);

			logger.info(
					"got allocate bucket for storageIndex ({}) from {}@{}",
					new Object[] { storageIndex, event.getSrcId(),
							event.getSource() });

			int expire_time = (int) (System.currentTimeMillis() / 1000 + 31 * 24 * 60 * 60);
			LeaseInfo lease_info = new LeaseInfo(0, event.getRenewSecret(),
					event.getCancelSecret(), expire_time, self.getPeerId());
			long max_space_per_bucket = event.getAllocatedSize();

			// TODO: add storage quota, that defines the amount of shared
			// storage
			long remaingingSpace = FileUtils.getFreeSpace(sharesDir);
			logger.debug("current availablespace={} bytes", remaingingSpace);

			Set<Integer> alreadygot = new HashSet<Integer>();
			Map<Integer, String> prexisingShares = getSharesforSI(storageIndex);
			for (int shnum : prexisingShares.keySet()) {
				alreadygot.add(shnum);
				// TODO: add or renew lease
			}

			logger.debug("i already have {} for stroageIndex ({})",
					DumpUtils.dumptolog(alreadygot), storageIndex);

			Map<Integer, String> sharemap = new HashMap<Integer, String>();

			for (int sharenum : event.getShareNums()) {

				String incominghome = FileUtils.JoinPath(incomingDir,
						storageIndexDir);
				String finalhome = FileUtils.JoinPath(sharesDir,
						storageIndexDir);

				String incomingfile = FileUtils.JoinPath(incominghome,
						String.valueOf(sharenum));
				String finalfile = FileUtils.JoinPath(finalhome,
						String.valueOf(sharenum));

				if (FileUtils.exists(incomingfile)) {
					continue;
				}

				if (FileUtils.exists(finalfile)) {
					continue;
				}

				if (remaingingSpace >= max_space_per_bucket) {
					String wbpID = common.getWriteBucketProxyID(storageIndex,
							sharenum);
					String bwId = common.getWriteBucketID(storageIndex,
							sharenum);

					createBucketWriter(new NsBucketWriterInit(thisSS,
							new ComponentAddress(event.getDestination(), bwId),
							new ComponentAddress(event.getSource(), wbpID),
							incominghome, finalhome, String.valueOf(sharenum),
							max_space_per_bucket, lease_info));

					sharemap.put(sharenum, bwId);
					logger.debug("create BucketWriter ({}) for ShareNum ({})",
							bwId, sharenum);
					remaingingSpace -= max_space_per_bucket;
				}

			}

			logger.debug(
					"send sharemap {} to  {}:{}",
					new Object[] { DumpUtils.dumptolog(sharemap),
							event.getSrcId(), event.getSource() });

			trigger(new AllocateBucketResponse(new ComponentAddress(
					event.getDestination(), event.getDestId()),
					new ComponentAddress(event.getSource(), event.getSrcId()),
					sharemap, alreadygot), net);
		}
	};

	/** The handle get buckets. */
	Handler<GetBuckets> handleGetBuckets = new Handler<GetBuckets>() {

		@Override
		public void handle(GetBuckets event) {

			String storageIndex = event.getStorageIndex();

			logger.info(
					"got getBuckets request for storageIndex ({}) from {}@{}",
					new Object[] { storageIndex, event.getSrcId(),
							event.getSource() });

			Map<Integer, String> shares = getSharesforSI(storageIndex);
			logger.debug("current available shares for {} are {}",
					storageIndex, DumpUtils.dumptolog(shares));

			Map<Integer, String> sharemap = new HashMap<Integer, String>();

			for (Map.Entry<Integer, String> e : shares.entrySet()) {
				int shnum = e.getKey();
				String homedir = e.getValue();

				String rbId = checkBRExistance(storageIndex, e.getKey());
				String destid = common.getReadBucketProxyID(storageIndex,
						e.getKey());

				NsBucketReaderInit init = new NsBucketReaderInit(thisSS,
						new ComponentAddress(event.getDestination(), rbId),
						new ComponentAddress(event.getSource(), destid),
						homedir, String.valueOf(shnum));

				createBucketReader(init);
				sharemap.put(shnum, rbId);
				logger.debug("create BucketReader ({}) for ShareNum ({})",
						rbId, shnum);
			}

			trigger(new GetBucketsResponse(new ComponentAddress(
					event.getDestination(), event.getDestId()),
					new ComponentAddress(event.getSource(), event.getSrcId()),
					sharemap), net);

		}

	};

	/** The handle have request. */
	Handler<HaveBuckets> handleHaveRequest = new Handler<HaveBuckets>() {
		@Override
		public void handle(HaveBuckets event) {
			String storageIndex = event.getStorageIndex();
			logger.info(
					"got haveBuckets request for ({}) from {}@{}",
					new Object[] { storageIndex, event.getSrcId(),
							event.getSource() });

			Map<Integer, String> shares = getSharesforSI(event
					.getStorageIndex());
			logger.debug("current available shares for {} are {}",
					storageIndex, DumpUtils.dumptolog(shares));

			trigger(new HaveBucketsResponse(new ComponentAddress(
					event.getDestination(), event.getDestId()),
					new ComponentAddress(event.getSource(), event.getSrcId()),
					new HashSet<Integer>(shares.keySet())), net);
		}
	};

	// FIXME: need a better implementation
	/** The handle get status. */
	Handler<StorageStatusRequest> handleGetStatus = new Handler<StorageStatusRequest>() {

		@Override
		public void handle(StorageStatusRequest event) {

			Map<String, SIStatusItem> statusperSI = new HashMap<String, SIStatusItem>();
			long total_size = 0;
			long total_count = 0;

			File shares = new File(sharesDir);
			File[] files = shares.listFiles();
			for (File f : files) {

				if (f.getName().equals("incoming"))
					continue;

				File[] ff = f.listFiles();
				for (File si : ff) {
					File[] shs = si.listFiles();
					int c = shs.length;
					long size = 0;
					Set<Integer> sharenums = new HashSet<Integer>();
					for (File sh : shs) {
						size += sh.length();
						sharenums.add(Integer.valueOf(sh.getName()));
					}
					size = size / 1024;
					total_count += c;
					total_size += size;

					statusperSI.put(si.getName(), new SIStatusItem(c, size,
							sharenums));
				}
			}

			trigger(new StorageStatusResponse(event, new StorageStatusView(
					statusperSI, total_size, total_count)), ssStatus);
		}

	};

	/**
	 * Gets the sharesfor si.
	 * 
	 * @param storageIndex
	 *            the storage index
	 * @return the sharesfor si
	 */
	private Map<Integer, String> getSharesforSI(String storageIndex) {

		String dir = common.storage_index_to_dir(storageIndex);
		String homedir = FileUtils.JoinPath(sharesDir, dir);
		Map<Integer, String> shares = new HashMap<Integer, String>();
		if (FileUtils.exists(homedir)) {
			File dir_f = new File(homedir);
			File[] files = dir_f.listFiles();
			for (File file : files) {
				shares.put(Integer.valueOf(file.getName()),
						file.getAbsolutePath());
			}
		}
		return shares;

	}

	/**
	 * Check br existance.
	 * 
	 * @param SI
	 *            the sI
	 * @param sharenum
	 *            the sharenum
	 * @return the string
	 */
	private String checkBRExistance(String SI, int sharenum) {
		String brId = common.getReadBucketID(SI, sharenum);

		boolean exists = false;
		synchronized (activebuckets) {
			if (activebuckets.containsKey(brId)) {
				exists = true;
			}
		}

		if (!exists) {
			return brId;
		}

		// there is an existing reader

		Tuple<Long, Long> lasttimes;
		synchronized (bucktesLastAccessTime) {
			lasttimes = bucktesLastAccessTime.get(brId);
		}

		if (lasttimes != null) {
			long now = System.currentTimeMillis();
			// time since last access is 5 times larger than the max recorded
			// RTT
			if ((now - lasttimes.getLeft()) > 5 * lasttimes.getRight()) {
				return brId;
			}
		}

		// either this bucket reader is still working
		// or lasttimes = null which probably means that the reader is recently
		// created
		// so we have to create another reader

		Integer cReaders;
		synchronized (concurrentReaders) {
			cReaders = concurrentReaders.get(brId);
		}

		// the first reader besides the existing one
		if (cReaders == null) {
			cReaders = 0;
		}

		cReaders++;
		String newId = common.getReadBucketID(SI, sharenum, cReaders);

		synchronized (concurrentReaders) {
			concurrentReaders.put(brId, cReaders);
		}

		return newId;
	}

	/**
	 * Creates the bucket reader.
	 * 
	 * @param init
	 *            the init
	 */
	private void createBucketReader(NsBucketReaderInit init) {
		String brid = init.getSelf().getId();
		synchronized (activebuckets) {
			if (activebuckets.containsKey(brid)) {
				return;
			}
		}

		Component br = create(NsBucketReader.class);

		connect(br.required(Network.class), net,
				new ExtMessageDestinationFilter(brid));
		connect(br.required(CFailureDetector.class), cfd);

		synchronized (activebuckets) {
			activebuckets.put(brid, br);
		}

		trigger(init, br.getControl());
	}

	/**
	 * Creates the bucket writer.
	 * 
	 * @param init
	 *            the init
	 */
	private void createBucketWriter(NsBucketWriterInit init) {
		Component bw = create(NsBucketWriter.class);
		String bwId = init.getSelf().getId();
		connect(bw.required(Network.class), net,
				new ExtMessageDestinationFilter(bwId));
		connect(bw.required(CFailureDetector.class), cfd);

		synchronized (activebuckets) {
			activebuckets.put(bwId, bw);
		}

		trigger(init, bw.getControl());

	}

	/**
	 * Clean_incomplete.
	 */
	private void clean_incomplete() {
		FileUtils.rmdir(incomingDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.storage.AbstractStorageServer#bucketClosed(java
	 * .lang.String)
	 */
	public synchronized void bucketClosed(String id) {
		Component cmp = activebuckets.remove(id);
		if (cmp == null) {
			logger.debug(
					"got bucket close for {} but there isn't any component with that id",
					id);
			return;
		}
		disconnect(cmp.required(Network.class), net);
		disconnect(cmp.required(CFailureDetector.class), cfd);
		destroy(cmp);
		logger.info("{} destroyed", id);
		logger.debug("activeBuckets={}, count={}",
				DumpUtils.dumptolog(activebuckets.keySet()),
				activebuckets.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.storage.AbstractStorageServer#updateAccessTime
	 * (java.lang.String, java.lang.Long, java.lang.Long)
	 */
	public void updateAccessTime(String id, Long time, Long rtt) {
		synchronized (bucktesLastAccessTime) {
			bucktesLastAccessTime.put(id, new Tuple<Long, Long>(time, rtt));
		}
	}
}
