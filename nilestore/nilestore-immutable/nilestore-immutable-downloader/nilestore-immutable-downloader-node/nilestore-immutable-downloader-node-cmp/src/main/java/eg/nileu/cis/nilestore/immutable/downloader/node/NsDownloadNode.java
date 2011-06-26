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
package eg.nileu.cis.nilestore.immutable.downloader.node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.cryptography.SHA256d;
import eg.nileu.cis.nilestore.immutable.NsBucketProxiesHolder;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.DownloadNode;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.DownloadingDone;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.GotValidatedSegment;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetUEB;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetUEBResponse;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GotCiphertextHashes;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GotSharesHashes;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.RBProxy;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.SetCommonParameters;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.NsSegmentFetcher;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.NsSegmentFetcherInit;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.port.AddShares;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.port.GetSegment;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.port.GetSegmentResponse;
import eg.nileu.cis.nilestore.immutable.downloader.segfetcher.port.SegmentFetcher;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.Share;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.FindShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.GotShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.NoMoreShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.ShareFinder;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.port.WantMoreShares;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.tahoe.NsShareFinder;
import eg.nileu.cis.nilestore.immutable.downloader.sharefinder.tahoe.NsShareFinderInit;
import eg.nileu.cis.nilestore.immutable.file.UEB;
import eg.nileu.cis.nilestore.redundancy.port.Decode;
import eg.nileu.cis.nilestore.redundancy.port.DecodeResponse;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.uri.CHKFileVerifierURI;
import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.DataUtils;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.hashtree.BadHashError;
import eg.nileu.cis.nilestore.utils.hashtree.NotEnoughHashesError;
import eg.nileu.cis.nilestore.utils.hashutils.Hasher;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsDownloadNode.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsDownloadNode extends NsBucketProxiesHolder {

	/** The downloadnode. */
	Negative<DownloadNode> downloadnode = provides(DownloadNode.class);

	/** The redundancy. */
	Positive<Redundancy> redundancy = requires(Redundancy.class);

	/** The peers. */
	Positive<AvailablePeers> peers = requires(AvailablePeers.class);

	/** The segmentfetcher. */
	private Component sharefinder, segmentfetcher;

	/** The logger. */
	private Logger logger; // = LoggerFactory.getLogger(NsDownloadNode.class);

	/** The self. */
	private NilestoreAddress self;

	/** The storage index. */
	private String storageIndex;

	/** The uri. */
	private CHKFileVerifierURI uri;

	/** The shares. */
	private List<Share> shares;

	/** The common. */
	private DownloadCommon common;

	/** The once. */
	private boolean once;

	/** The end. */
	private long start, end;

	/**
	 * Instantiates a new ns download node.
	 */
	public NsDownloadNode() {
		once = true;
		shares = new ArrayList<Share>();

		sharefinder = create(NsShareFinder.class);
		segmentfetcher = create(NsSegmentFetcher.class);

		connect(sharefinder.required(Network.class), network);
		connect(sharefinder.required(CFailureDetector.class), cfd);
		connect(sharefinder.required(AvailablePeers.class), peers);

		subscribe(handleInit, control);
		subscribe(handleGotShareReaders,
				sharefinder.provided(ShareFinder.class));
		subscribe(handleNoMoreShares, sharefinder.provided(ShareFinder.class));
		subscribe(handleGetSegmentResponse,
				segmentfetcher.provided(SegmentFetcher.class));
		subscribe(handleWantMoreShares,
				segmentfetcher.provided(SegmentFetcher.class));
		subscribe(handleDecodeResponse, redundancy);
	}

	/** The handle init. */
	Handler<NsDownloadNodeInit> handleInit = new Handler<NsDownloadNodeInit>() {
		@Override
		public void handle(NsDownloadNodeInit init) {

			start = System.currentTimeMillis();
			self = init.getSelf();
			uri = init.getVerifyCap();
			storageIndex = Base32.encode(uri.getStorageIndex());
			common = new DownloadCommon(uri.getTotalShares(), uri.getSize());

			logger = Slf4jInstantiator.getLogger(NsDownloadNode.class,
					self.getNickname());
			logger.info("(SI={}): initiated", storageIndex);

			trigger(new NsShareFinderInit(self, storageIndex,
					uri.getNeededShares()), sharefinder.getControl());
			trigger(new NsSegmentFetcherInit(self, storageIndex,
					uri.getNeededShares()), segmentfetcher.getControl());
			trigger(new FindShares(), sharefinder.provided(ShareFinder.class));
		}
	};

	/** The handle got share readers. */
	Handler<GotShares> handleGotShareReaders = new Handler<GotShares>() {

		@Override
		public void handle(GotShares event) {

			if (event.getStatus().isSucceeded()) {
				logger.info("(SI={}): GotShares from my share finder",
						storageIndex);
				List<Share> gotShares = event.getShares();
				List<Share> _shares = new ArrayList<Share>();

				logger.debug("(SI={}): GotShares=({})", storageIndex,
						DumpUtils.dumptolog(gotShares));
				for (Share share : gotShares) {
					String rbpid = createReadBucketProxy(self, share.getSelf(),
							storageIndex, share.getShareNum(),
							segmentfetcher.required(RBProxy.class));
					_shares.add(new Share(share.getShareNum(), share.getRTT(),
							new ComponentAddress(share.getSelf().getAddress(),
									rbpid)));
					if (!common.haveUEB()) {
						if (once) {
							logger.debug("(SI={}): send GetUEB request to {}",
									storageIndex, rbpid);
							triggerOnRBP(new GetUEB(rbpid));
							subscribeRBPTo(rbpid, handleGetUEBResponse);
							subscribeRBPTo(rbpid, handleGotCiphertextHashes);
							once = false;
						}
					}

					subscribeRBPTo(rbpid, handleGotShareHashes);
				}

				if (common.haveUEB()) {
					updateSharesCommonParams(_shares);
				}

				shares.addAll(_shares);
				trigger(new AddShares(_shares),
						segmentfetcher.provided(SegmentFetcher.class));

			} else {
				// there is no servers to download from
				logger.warn("(SI={}): share finder failed reason=({})",
						storageIndex, event.getStatus());
				trigger(new DownloadingDone(event.getStatus(), storageIndex),
						downloadnode);
			}
		}
	};

	/** The handle no more shares. */
	Handler<NoMoreShares> handleNoMoreShares = new Handler<NoMoreShares>() {
		@Override
		public void handle(NoMoreShares event) {
			logger.info("(SI={}): share finder cannot find any more shares",
					storageIndex);
			trigger(event, segmentfetcher.provided(SegmentFetcher.class));
		}
	};
	//TODO: handle cases where BadHashError or NotEnoughHashesError encountered
	
	/** The handle get ueb response. */
	Handler<GetUEBResponse> handleGetUEBResponse = new Handler<GetUEBResponse>() {
		@Override
		public void handle(GetUEBResponse event) {

			byte[] uebdata = event.getUEBData();
			try {
				logger.info("(SI={}): GotUEB from share {}", storageIndex,
						event.getSharenum());
				UEB ueb = new UEB(uebdata, uri.getUEBHash());
				logger.debug(ueb.toString());

				common.setUEB(ueb);
				updateSharesCommonParams(shares);
				//TODO: implement a segmentation algrithm to enable random acess pattern
				trigger(new GetSegment(common.getCurrentSegmentNum()),
						segmentfetcher.provided(SegmentFetcher.class));

			} catch (BadHashError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnoughHashesError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	/** The handle got share hashes. */
	Handler<GotSharesHashes> handleGotShareHashes = new Handler<GotSharesHashes>() {
		@Override
		public void handle(GotSharesHashes event) {

			logger.info("(SI={}): got share hashes from share {}",
					storageIndex, event.getSharenum());
			try {
				Map<Integer, ByteArray> sharehashes = event.getShareHashes();
				common.getShareHashTree().setHashes(sharehashes);
				logger.debug("(SI={}): ShareHT={}", storageIndex,
						common.getShareHashTree());
			} catch (BadHashError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnoughHashesError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/** The handle got ciphertext hashes. */
	Handler<GotCiphertextHashes> handleGotCiphertextHashes = new Handler<GotCiphertextHashes>() {
		@Override
		public void handle(GotCiphertextHashes event) {

			logger.info("(SI={}): got ciphertext hashes from share {}",
					storageIndex, event.getSharenum());
			Map<Integer, ByteArray> hashes = event.getHashes();
			try {
				common.getCiphertextHashTree().setHashes(hashes);
				// logger.debug("(SI={}), CiphertextHT={}", storageIndex,
				// common.getCiphertextHashTree());
			} catch (BadHashError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnoughHashesError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/** The handle want more shares. */
	Handler<WantMoreShares> handleWantMoreShares = new Handler<WantMoreShares>() {
		@Override
		public void handle(WantMoreShares event) {
			logger.info("(SI={}): our segment fetcher need more shares",
					storageIndex);
			trigger(event, sharefinder.provided(ShareFinder.class));
		}
	};

	/** The handle get segment response. */
	Handler<GetSegmentResponse> handleGetSegmentResponse = new Handler<GetSegmentResponse>() {
		@Override
		public void handle(GetSegmentResponse event) {
			logger.info("(SI={}): {}", storageIndex, event.getStatus());
			if (!event.getStatus().isSucceeded()) {
				trigger(new DownloadingDone(event.getStatus(), storageIndex),
						downloadnode);
				return;
			}

			//logger.info("(SI={}): got segment#{} from segmenFetcher",storageIndex, common.getCurrentSegmentNum());

			Map<Integer, byte[]> blocks = event.getBlocks();
			// int blockSize = common.getBlockSize();
			ByteArray[] chunks = new ByteArray[uri.getNeededShares()];
			int[] indeces = new int[uri.getNeededShares()];

			int i = 0;
			for (Map.Entry<Integer, byte[]> e : blocks.entrySet()) {
				chunks[i] = new ByteArray(e.getValue());// Arrays.copyOf(e.getValue(),blockSize);
				indeces[i] = e.getKey();
				i++;
			}

			trigger(new Decode(uri.getNeededShares(), uri.getTotalShares(),
					chunks, indeces), redundancy);
		}
	};

	/** The handle decode response. */
	Handler<DecodeResponse> handleDecodeResponse = new Handler<DecodeResponse>() {
		@Override
		public void handle(DecodeResponse event) {
			logger.debug("(SI={}): got decoded segment#{} from the decoder",
					storageIndex, common.getCurrentSegmentNum());
			int k = event.getK();
			int pad = common.getPaddingValue();

			SHA256d crypttextSegmentHasher = Hasher.getCrypttextSegmenthasher();
			List<byte[]> segment = new ArrayList<byte[]>();

			for (int i = 0; i < k; i++) {

				byte[] chunk = event.getBuffer(i);

				if (common.isTailSegmentReached() && i == k - 1 && pad > 0) {
					int newlen = chunk.length - pad;
					chunk = Arrays.copyOf(chunk, newlen);
				}

				crypttextSegmentHasher.update(chunk);

				segment.add(chunk);
			}

			byte[] segmentHash = crypttextSegmentHasher.digest();
			try {
				common.getCiphertextHashTree().setLeafHash(
						common.getCurrentSegmentNum(), segmentHash);
				// logger.debug("(SI={}), CiphertextHT={}", storageIndex,
				// common.getCiphertextHashTree());
				int index = common.getCurrentSegmentNum() + 1;
				// trigger(new DownloadResponse(storageIndex, index,
				// common.getNumSegments(),datautils.join(segment)),
				// downloaderport);
				logger.debug(
						"(SI={}): send validated segment to our parent downloader",
						storageIndex);
				trigger(new GotValidatedSegment(DataUtils.join(segment), index,
						common.getNumSegments()), downloadnode);

				if (common.nextSegment()) {
					logger.debug(
							"(SI={}): we still have segments to ask for, send getSegment {}",
							storageIndex, common.getCurrentSegmentNum());
					trigger(new GetSegment(common.getCurrentSegmentNum()),
							segmentfetcher.provided(SegmentFetcher.class));
				} else {
					end = System.currentTimeMillis();
					long elapsed = end - start;
					logger.debug("(SI={}): we finished downloading in {} msec",
							storageIndex, elapsed);
					if (System.getProperty("debug.writetimespath") != null) {
						debugWriteTimeStats(elapsed, storageIndex);
					}
					destroyAll();
					trigger(new DownloadingDone(new StatusMsg(Status.Succeeded,
							"downloading completed"), storageIndex),
							downloadnode);
				}

			} catch (BadHashError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotEnoughHashesError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	/**
	 * Debug write time stats.
	 * 
	 * @param elapsed
	 *            the elapsed
	 * @param key
	 *            the key
	 */
	private void debugWriteTimeStats(long elapsed, String key) {
		String f = getNextTrialPath(key);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			String line = String.valueOf(elapsed);
			writer.write(line);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Gets the next trial path.
	 * 
	 * @param key
	 *            the key
	 * @return the next trial path
	 */
	private String getNextTrialPath(String key) {
		int trial = 1;
		String f = getFileName(key, trial);
		while (FileUtils.exists(f)) {
			trial++;
			f = getFileName(key, trial);
		}
		return f;
	}

	/**
	 * Gets the file name.
	 * 
	 * @param key
	 *            the key
	 * @param trial
	 *            the trial
	 * @return the file name
	 */
	private String getFileName(String key, int trial) {
		String path = System.getProperty("debug.writetimespath");
		return String.format("%s/nilestore-%s.%d", path, key, trial);
	}

	/**
	 * Update shares common params.
	 * 
	 * @param shs
	 *            the shs
	 */
	private void updateSharesCommonParams(List<Share> shs) {
		for (Share sh : shs) {
			triggerOnRBP(new SetCommonParameters(sh.getSelf().getId(),
					common.getTailBlockSize(), common.getNumSegments(), common
							.getShareHashTree().getLeafIndex(sh.getShareNum())));
		}
	}

	/**
	 * Destroy all.
	 */
	private void destroyAll() {
		triggerCloseOnAllRBPs();
		// FIXME:
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		destroyAllBucketProxies(segmentfetcher.required(RBProxy.class));
		destroy(segmentfetcher);
		disconnect(sharefinder.required(Network.class), network);
		disconnect(sharefinder.required(CFailureDetector.class), cfd);
		disconnect(sharefinder.required(AvailablePeers.class), peers);

		destroy(sharefinder);
	}

}
