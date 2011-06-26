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
package eg.nileu.cis.nilestore.immutable.uploader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.SecretHolder;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.NsBucketProxiesHolder;
import eg.nileu.cis.nilestore.immutable.file.EncryptFileHandle;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.immutable.peertracker.NsPeerTrackersHolder;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.NsEncoder;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.NsEncoderInit;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.Encoder;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.EncoderDone;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.GetEncoderParams;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.GetEncoderParamsResponse;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.SetShareHolders;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeers;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeersResponse;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PeerSelector;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.tahoe2.NsTahoe2PeerSelector;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.tahoe2.NsTahoe2PeerSelectorInit;
import eg.nileu.cis.nilestore.immutable.uploader.port.Uploader;
import eg.nileu.cis.nilestore.immutable.uploader.port.UploadingDone;
import eg.nileu.cis.nilestore.immutable.uploader.writer.NsWriteBucketProxy;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.WBProxy;
import eg.nileu.cis.nilestore.interfaces.file.IUploadable;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.uri.CHKFileVerifierURI;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.hashtree.IncompleteHashTree;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsUploader.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsUploader extends NsBucketProxiesHolder {

	/** The uploader. */
	Negative<Uploader> uploader = provides(Uploader.class);

	/** The peers. */
	Positive<AvailablePeers> peers = requires(AvailablePeers.class);

	/** The redundancy. */
	Positive<Redundancy> redundancy = requires(Redundancy.class);

	/** The logger. */
	private Logger logger;

	/** The peerselector. */
	private Component encoder, peerselector;

	/** The self. */
	private NilestoreAddress self;

	/** The storage index. */
	private String storageIndex;

	/** The fileinfo. */
	private FileInfo fileinfo;

	/** The secret holder. */
	private SecretHolder secretHolder;

	/** The uploadable. */
	private IUploadable uploadable;

	/** The encryption key. */
	private byte[] encryptionKey;

	/** The start peer selection. */
	private long startPeerSelection;

	/** The started. */
	private long started;

	// TODO: enable the event Stop
	/**
	 * Instantiates a new ns uploader.
	 */
	public NsUploader() {
		encoder = create(NsEncoder.class);
		Class<? extends NsPeerTrackersHolder> peerSelectorClass = NsTahoe2PeerSelector.class;

		peerselector = create(peerSelectorClass);

		connect(encoder.required(Redundancy.class), redundancy);
		connect(peerselector.required(Network.class), network);
		connect(peerselector.required(CFailureDetector.class), cfd);
		connect(peerselector.required(AvailablePeers.class), peers);

		subscribe(handleInit, control);

		subscribe(handleGotEncoderParams, encoder.provided(Encoder.class));
		subscribe(handleGotPeers, peerselector.provided(PeerSelector.class));
		subscribe(handleUploadingCompleted, encoder.provided(Encoder.class));

	}

	/** The handle init. */
	Handler<NsUploaderInit> handleInit = new Handler<NsUploaderInit>() {

		@Override
		public void handle(NsUploaderInit init) {

			self = init.getSelf();
			uploadable = init.getUploadable();
			secretHolder = init.getSecretHolder();
			logger = Slf4jInstantiator.getLogger(NsUploader.class,
					self.getNickname());

			started = System.currentTimeMillis();
			try {
				EncryptFileHandle encryptedUploadable = new EncryptFileHandle(
						uploadable);
				storageIndex = Base32.encode(encryptedUploadable
						.getStorageIndex());
				encryptionKey = uploadable.getEncryptionkey();

				trigger(new NsEncoderInit(encryptedUploadable, self),
						encoder.getControl());
				trigger(new NsTahoe2PeerSelectorInit(self),
						peerselector.getControl());
				logger.info("(SI={}): Uploader started", storageIndex);
				trigger(new GetEncoderParams(), encoder.provided(Encoder.class));
			} catch (IOException e) {
				logger.error("Exception while initializing the uploader: ", e);
			}

		}

	};

	/** The handle got encoder params. */
	Handler<GetEncoderParamsResponse> handleGotEncoderParams = new Handler<GetEncoderParamsResponse>() {

		@Override
		public void handle(GetEncoderParamsResponse event) {

			logger.debug("(SI={}): got encoder parameters", storageIndex);
			fileinfo = fillFileInfo(event.getShareSize(), event.getBlockSize(),
					event.getNumSegments(), event.getMaxExtSize(),
					uploadable.getEncodingParams());

			startPeerSelection = System.currentTimeMillis();
			logger.debug("(SI={}): start peer selection", storageIndex);
			trigger(new PSGetPeers(fileinfo),
					peerselector.provided(PeerSelector.class));
		}
	};

	/** The handle got peers. */
	Handler<PSGetPeersResponse> handleGotPeers = new Handler<PSGetPeersResponse>() {

		@Override
		public void handle(PSGetPeersResponse event) {

			long elapsed = System.currentTimeMillis() - startPeerSelection;
			uploadable.getUploadResults().setTimeforKey("peer_selection",
					elapsed);

			if (!event.getStatus().isSucceeded()) {
				logger.debug("(SI={}): peer selection failed reason={}",
						storageIndex, event.getStatus().getMessage());
				trigger(new UploadingDone(storageIndex, event.getStatus(),
						uploadable.getUploadResults()), uploader);
				return;
			}

			Map<Integer, ComponentAddress> uploadServers = event
					.getUploadServers();
			Map<Address, Set<Integer>> alreadygot = event.getAlreadyGot();

			logger.debug("(SI={}): got uploadservers elapsed time={}msec",
					storageIndex, elapsed);
			logger.debug("(SI={}): uploadservers = {} , servermap = {} ",
					DumpUtils.dumptolog(uploadServers),
					DumpUtils.dumptolog(alreadygot));

			Map<Integer, ComponentAddress> sharemap = new HashMap<Integer, ComponentAddress>();
			Map<Address, Set<Integer>> servermap = new HashMap<Address, Set<Integer>>(
					alreadygot);

			for (int sharenum : uploadServers.keySet()) {

				String wbpid = createWriteBucketProxy(self,
						uploadServers.get(sharenum), fileinfo, sharenum,
						encoder.required(WBProxy.class));

				Address serverAdd = uploadServers.get(sharenum).getAddress();

				sharemap.put(sharenum, new ComponentAddress(serverAdd, wbpid));

				if (!servermap.containsKey(serverAdd))
					servermap.put(serverAdd, new HashSet<Integer>());

				servermap.get(serverAdd).add(sharenum);
			}
			if (sharemap.isEmpty()) {
				logger.info("(SI={}): file already exists in the network",
						storageIndex);
			}
			// TODO: add servermap to the upload results
			trigger(new SetShareHolders(sharemap, servermap),
					encoder.provided(Encoder.class));
		}
	};

	/** The handle uploading completed. */
	Handler<EncoderDone> handleUploadingCompleted = new Handler<EncoderDone>() {

		@Override
		public void handle(EncoderDone event) {
			// TODO: add encryption on/off
			long elapsed = System.currentTimeMillis() - started;
			uploadable.getUploadResults().setTimeforKey("total", elapsed);

			logger.debug("(SI={}): encoder done, {}", storageIndex,
					event.getStatus());
			if (event.getStatus().isSucceeded()) {

				logger.debug("(SI={}): encoding done, time taken={}msec",
						storageIndex, elapsed);
				CHKFileVerifierURI veriferURI = event.getVerifyURI();
				CHKFileURI uri = new CHKFileURI(encryptionKey,
						veriferURI.getUEBHash(), veriferURI.getNeededShares(),
						veriferURI.getTotalShares(), veriferURI.getSize());
				uploadable.getUploadResults().setUri(uri);

				if (System.getProperty("debug.writetimespath") != null) {
					debugWriteTimeStats(uploadable.getUploadResults()
							.getTimes(), Base32.encode(encryptionKey));
				}
			}

			trigger(new UploadingDone(storageIndex, event.getStatus(),
					uploadable.getUploadResults()), uploader);
			destroyAll();
		}

	};

	/**
	 * Debug write time stats.
	 * 
	 * @param times
	 *            the times
	 * @param key
	 *            the key
	 */
	private void debugWriteTimeStats(Map<String, Double> times, String key) {
		String f = getNextTrialPath(key);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			for (Map.Entry<String, Double> t : times.entrySet()) {
				String line = t.getKey() + "=" + t.getValue();
				writer.write(line);
				writer.newLine();
			}
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
	 * Fill file info.
	 * 
	 * @param shareSize
	 *            the share size
	 * @param blockSize
	 *            the block size
	 * @param numSegments
	 *            the num segments
	 * @param MaxExt
	 *            the max ext
	 * @param encodingParam
	 *            the encoding param
	 * @return the file info
	 */
	private FileInfo fillFileInfo(long shareSize, int blockSize,
			int numSegments, int MaxExt, EncodingParam encodingParam) {
		byte[] client_renewal_secret = secretHolder.get_renewal_secret();
		byte[] client_cancel_secret = secretHolder.get_cancel_secret();
		byte[] file_renewal_secret = Hash.file_renewal_secret(
				client_renewal_secret, Base32.decode(storageIndex));
		byte[] file_cancel_secret = Hash.file_cancel_secret(
				client_cancel_secret, Base32.decode(storageIndex));

		IncompleteHashTree tmp = new IncompleteHashTree(encodingParam.getN());
		int num_share_hashes = tmp.neededHashes(0, true).size();

		int allocatedSize = NsWriteBucketProxy.getAllocatedSize(shareSize,
				blockSize, numSegments, num_share_hashes, MaxExt);

		FileInfo info = new FileInfo(encodingParam, storageIndex,
				file_renewal_secret, file_cancel_secret, allocatedSize,
				num_share_hashes, shareSize, blockSize, numSegments);

		return info;
	}

	/**
	 * Destroy all.
	 */
	private void destroyAll() {
		destroyAllBucketProxies(encoder.required(WBProxy.class));
		disconnect(encoder.required(Redundancy.class), redundancy);
		destroy(encoder);

		disconnect(peerselector.required(Network.class), network);
		disconnect(peerselector.required(CFailureDetector.class), cfd);
		disconnect(peerselector.required(AvailablePeers.class), peers);

		destroy(peerselector);
	}
}
