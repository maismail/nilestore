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
package eg.nileu.cis.nilestore.immutable.uploader.encoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.cryptography.SHA256d;
import eg.nileu.cis.nilestore.immutable.common.Close;
import eg.nileu.cis.nilestore.immutable.file.EncryptFileHandle;
import eg.nileu.cis.nilestore.immutable.file.UEB;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.Encoder;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.EncoderDone;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.GetEncoderParams;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.GetEncoderParamsResponse;
import eg.nileu.cis.nilestore.immutable.uploader.encoder.port.SetShareHolders;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.CloseCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutBlock;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutBlockCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutBlockHashData;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutBlockHashDataCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutCryptHashData;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutCryptHashDataCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutHeader;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutHeaderCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutShareHashData;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutShareHashDataCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutUEB;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.PutUEBCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.WBProxy;
import eg.nileu.cis.nilestore.redundancy.port.Encode;
import eg.nileu.cis.nilestore.redundancy.port.EncodeResponse;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.uri.CHKFileVerifierURI;
import eg.nileu.cis.nilestore.utils.Barrier;
import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashtree.HashTree;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;
import eg.nileu.cis.nilestore.utils.hashutils.Hasher;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsEncoder.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsEncoder extends ComponentDefinition {

	/** The encoder. */
	Negative<Encoder> encoder = provides(Encoder.class);

	/** The wbproxy. */
	Positive<WBProxy> wbproxy = requires(WBProxy.class);

	/** The redundancy. */
	Positive<Redundancy> redundancy = requires(Redundancy.class);

	/** The logger. */
	private Logger logger;

	/** The uri extension data. */
	private UEB uriExtensionData;

	/** The uploadable. */
	private EncryptFileHandle uploadable;

	/** The storage index. */
	private String storageIndex;

	/** The encoding param. */
	private EncodingParam encodingParam;

	/** The num_segments. */
	private int num_segments;

	/** The sharesize. */
	private long sharesize;

	/** The block_size. */
	private int block_size;

	/** The last_block_size. */
	private int last_block_size;

	/** The segment_num. */
	private int segment_num;

	/** The crypttext_hasher. */
	private SHA256d crypttext_hasher;

	/** The cryptext_hashes. */
	private List<ByteArray> cryptext_hashes;

	/** The block_hashes. */
	private Map<Integer, List<ByteArray>> block_hashes;

	/** The share_root_hashes. */
	private List<ByteArray> share_root_hashes;

	/** The landlords. */
	private Map<Integer, ComponentAddress> landlords;

	/** The servermap. */
	private Map<Address, Set<Integer>> servermap;

	/** The uri_extension_hash. */
	private byte[] uri_extension_hash;

	/** The file already exists. */
	private boolean fileAlreadyExists;

	/** The barrier. */
	private Barrier barrier;

	// Times
	/** The start_timestamp. */
	private long start_timestamp;

	/** The start_encoding. */
	private long start_encoding;

	/** The start_sending. */
	private long start_sending;

	/** The start_hashing. */
	private long start_hashing;

	/**
	 * Instantiates a new ns encoder.
	 */
	public NsEncoder() {
		uriExtensionData = new UEB();
		fileAlreadyExists = false;

		subscribe(handleInit, control);

		subscribe(handleGetEncoderParams, encoder);
		subscribe(handleSetShareholders, encoder);

		subscribe(handlePutHeaderCompleted, wbproxy);
		subscribe(handlePutBlockCompleted, wbproxy);
		subscribe(handlePutCryptHashDataCompleted, wbproxy);
		subscribe(handlePutBlockHashDataCompleted, wbproxy);
		subscribe(handlePutShareHashDataCompleted, wbproxy);
		subscribe(handlePutUEBCompleted, wbproxy);
		subscribe(handleCloseCompleted, wbproxy);

		subscribe(handleEncodeResponse, redundancy);
	}

	/** The handle init. */
	Handler<NsEncoderInit> handleInit = new Handler<NsEncoderInit>() {

		@Override
		public void handle(NsEncoderInit init) {

			logger = Slf4jInstantiator.getLogger(NsEncoder.class, init
					.getSelf().getNickname());
			setEncryptableUploadable(init.getEncryptedFileHandle());
			logger.info("(SI={}): encoder initiated ", storageIndex);
		}

	};

	/** The handle get encoder params. */
	Handler<GetEncoderParams> handleGetEncoderParams = new Handler<GetEncoderParams>() {

		@Override
		public void handle(GetEncoderParams event) {

			logger.debug("(SI={}): get encoder parameters", storageIndex);
			trigger(new GetEncoderParamsResponse(sharesize, block_size,
					num_segments), encoder);
		}
	};

	/** The handle set shareholders. */
	Handler<SetShareHolders> handleSetShareholders = new Handler<SetShareHolders>() {

		@Override
		public void handle(SetShareHolders event) {

			logger.debug("(SI={}): got shareholders", storageIndex);

			landlords = event.getSharemap();
			servermap = event.getServermap();

			logger.debug(
					"(SI={}): servermap={}, sharemap={}",
					new Object[] { storageIndex,
							DumpUtils.dumptolog(servermap),
							DumpUtils.dumptolog(landlords) });

			fileAlreadyExists = landlords.isEmpty();
			start_timestamp = System.nanoTime();
			barrier = new Barrier(encodingParam.getN());

			segment_num = 0;
			crypttext_hasher = Hasher.getCrypttexthasher();
			cryptext_hashes = new ArrayList<ByteArray>();
			block_hashes = new HashMap<Integer, List<ByteArray>>();
			share_root_hashes = new ArrayList<ByteArray>();

			start_all_shareholders();

			logger.debug("(SI={}): started", storageIndex);
		}

	};

	/** The handle put header completed. */
	Handler<PutHeaderCompleted> handlePutHeaderCompleted = new Handler<PutHeaderCompleted>() {

		@Override
		public void handle(PutHeaderCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}
			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				barrier.reset();
				logger.debug("(SI={}): writeHeader completed", storageIndex);
				if (segment_num < num_segments - 1) {
					encode_and_send_segments();
				} else {
					encode_and_send_tailsegment();
				}
			}

		}

	};

	/** The handle put block completed. */
	Handler<PutBlockCompleted> handlePutBlockCompleted = new Handler<PutBlockCompleted>() {

		@Override
		public void handle(PutBlockCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}
			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				double elapsed = (System.nanoTime() - start_sending) * 1e-6;
				uploadable.getUploadResults().addTimetoKey(
						"cumulative_sending", elapsed);
				barrier.reset();
				putBlockCompleted();
			}
		}

	};

	/** The handle put crypt hash data completed. */
	Handler<PutCryptHashDataCompleted> handlePutCryptHashDataCompleted = new Handler<PutCryptHashDataCompleted>() {

		@Override
		public void handle(PutCryptHashDataCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}
			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				barrier.reset();
				send_all_block_hash_trees();

			}
		}

	};

	/** The handle put block hash data completed. */
	Handler<PutBlockHashDataCompleted> handlePutBlockHashDataCompleted = new Handler<PutBlockHashDataCompleted>() {

		@Override
		public void handle(PutBlockHashDataCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}

			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				barrier.reset();
				send_all_share_hash_trees();

			}
		}

	};

	/** The handle put share hash data completed. */
	Handler<PutShareHashDataCompleted> handlePutShareHashDataCompleted = new Handler<PutShareHashDataCompleted>() {

		@Override
		public void handle(PutShareHashDataCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}
			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				barrier.reset();
				send_uri_extension_to_all_shareholders();

			}
		}

	};

	/** The handle put ueb completed. */
	Handler<PutUEBCompleted> handlePutUEBCompleted = new Handler<PutUEBCompleted>() {

		@Override
		public void handle(PutUEBCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}

			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				barrier.reset();
				send_close();

			}

		}

	};

	/** The handle close completed. */
	Handler<CloseCompleted> handleCloseCompleted = new Handler<CloseCompleted>() {

		@Override
		public void handle(CloseCompleted event) {

			boolean nofailure = true;
			if (event.isSucceeded()) {
				barrier.setTrue(event.getSharenum());
			} else {
				nofailure = removeShareholder(event.getSharenum());
			}

			logger.debug("(SI={}): barrierStatus = {}, noFailure={}",
					new Object[] { storageIndex, barrier, nofailure });
			if (barrier.isFilled() && nofailure) {
				closeCompleted();
			}
		}

	};

	/** The handle encode response. */
	Handler<EncodeResponse> handleEncodeResponse = new Handler<EncodeResponse>() {
		@Override
		public void handle(EncodeResponse event) {

			double elapsed = (System.nanoTime() - start_encoding) * 1e-6;
			uploadable.getUploadResults().addTimetoKey("cumulative_encoding",
					elapsed);

			start_sending = System.nanoTime();
			logger.info("(SI={}): send segment {}/{}", new Object[] {
					storageIndex, segment_num + 1, num_segments });
			for (int i = 0; i < event.getN(); i++) {
				byte[] block = event.getBuffer(i);
				if (landlords.containsKey(i)) {
					logger.debug("(SI={}): putblock {} on share {}",
							new Object[] { storageIndex, segment_num, i });
					trigger(new PutBlock(segment_num, block, landlords.get(i)
							.getId()), wbproxy);
				}

				byte[] block_hash = Hash.block_hash(block);
				logger.debug("(SI={}): block={}, blockHash={}", new Object[] {
						storageIndex, i, Base32.encode(block_hash) });
				if (!block_hashes.containsKey(i)) {
					block_hashes.put(i, new ArrayList<ByteArray>());
				}
				block_hashes.get(i).add(new ByteArray(block_hash));
			}

			segment_num++;

			if (fileAlreadyExists) {
				putBlockCompleted();
				if (segment_num == num_segments) {
					send_all_block_hash_trees();
					send_all_share_hash_trees();
					send_uri_extension_to_all_shareholders();
					send_close();
					closeCompleted();
				}
			}
		}
	};

	/**
	 * _gather_data.
	 * 
	 * @param num_chunks
	 *            the num_chunks
	 * @param input_chunk_size
	 *            the input_chunk_size
	 * @param crypttext_segment_hasher
	 *            the crypttext_segment_hasher
	 * @param allowshort
	 *            the allowshort
	 * @return the byte array[]
	 */
	private ByteArray[] _gather_data(int num_chunks, int input_chunk_size,
			SHA256d crypttext_segment_hasher, boolean allowshort) {
		// byte[][] chunks = new byte[num_chunks][input_chunk_size];
		ByteArray[] chunks = new ByteArray[num_chunks];
		try {
			for (int i = 0; i < num_chunks; i++) {
				byte[] data = uploadable.readEncrypted(input_chunk_size);
				assert data.length == input_chunk_size;

				crypttext_segment_hasher.update(data);
				crypttext_hasher.update(data);
				if (allowshort) {
					if (data.length < input_chunk_size) {
						// Padding
						data = Arrays.copyOf(data, input_chunk_size);
					}
				}
				chunks[i] = new ByteArray(data);
			}
		} catch (IOException e) {
			logger.error("Exception while reading from the file: ", e);
		}
		return chunks;
	}

	/**
	 * Close completed.
	 */
	private void closeCompleted() {

		logger.debug("(SI={}): close completed", storageIndex);
		long t = System.nanoTime();
		double elapsed = (t - start_hashing) * 1e-6;
		uploadable.getUploadResults().addTimetoKey("hashes_and_close", elapsed);
		elapsed = (t - start_timestamp) * 1e-6;
		uploadable.getUploadResults().addTimetoKey("total_encode_and_push",
				elapsed);

		barrier.reset();

		CHKFileVerifierURI verifyCap = new CHKFileVerifierURI(storageIndex,
				uri_extension_hash, encodingParam.getK(), encodingParam.getN(),
				uploadable.getSize());
		logger.debug("(SI={}): verifycap={}", storageIndex,
				verifyCap.toString());
		trigger(new EncoderDone(new StatusMsg(Status.Succeeded,
				"encoding and pushing completed successfully "), verifyCap),
				encoder);
	}

	/**
	 * Encode_and_send_segments.
	 */
	private void encode_and_send_segments() {
		logger.info("(SI={}): encode segment {}/{}", new Object[] {
				storageIndex, segment_num + 1, num_segments });

		start_encoding = System.nanoTime();
		SHA256d crypttext_segment_hasher = Hasher.getCrypttextSegmenthasher();
		ByteArray[] chunks = _gather_data(encodingParam.getK(), block_size,
				crypttext_segment_hasher, false);
		ByteArray hash = new ByteArray(crypttext_segment_hasher.digest());
		cryptext_hashes.add(hash);
		logger.debug("(SI={}): trigger encode for segment {}", storageIndex,
				segment_num);
		trigger(new Encode(encodingParam.getK(), encodingParam.getN(), chunks),
				redundancy);
	}

	/**
	 * Encode_and_send_tailsegment.
	 */
	private void encode_and_send_tailsegment() {
		logger.info("(SI={}): encode segment {}/{}", new Object[] {
				storageIndex, segment_num + 1, num_segments });

		start_encoding = System.nanoTime();
		SHA256d crypttext_segment_hasher = Hasher.getCrypttextSegmenthasher();
		ByteArray[] chunks = _gather_data(encodingParam.getK(),
				last_block_size, crypttext_segment_hasher, true);
		ByteArray hash = new ByteArray(crypttext_segment_hasher.digest());
		cryptext_hashes.add(hash);
		logger.debug("(SI={}): trigger encode for segment {}", storageIndex,
				segment_num);
		trigger(new Encode(encodingParam.getK(), encodingParam.getN(), chunks),
				redundancy);
	}

	/**
	 * Finish_hashing.
	 */
	private void finish_hashing() {
		start_hashing = System.nanoTime();
		byte[] crypttexthash = crypttext_hasher.digest();
		logger.debug("(SI={}): crypttextHash={}", storageIndex,
				Base32.encode(crypttexthash));

		uriExtensionData.addKeyValue("crypttext_hash", crypttexthash);
		try {
			uploadable.close();
		} catch (IOException e) {
			logger.error("Exception while closing the file: ", e);
		}
	}

	/**
	 * Put block completed.
	 */
	private void putBlockCompleted() {
		if (segment_num < (num_segments - 1)) {
			encode_and_send_segments();
		} else if (segment_num == num_segments - 1) {
			encode_and_send_tailsegment();
		} else if (segment_num == num_segments) {
			finish_hashing();
			send_crypttext_hash_tree_to_all_shareholders();
		}
	}

	/**
	 * Removes the shareholder.
	 * 
	 * @param sharenum
	 *            the sharenum
	 * @return true, if successful
	 */
	private synchronized boolean removeShareholder(int sharenum) {
		// FIXME: should trigger a destroy event for all failed writeproxies
		if (landlords.size() != servermap.size()) {
			// this case -> there is some servers got more than one share

			if (landlords.containsKey(sharenum)) {
				Set<Integer> othershares = servermap.remove(landlords.get(
						sharenum).getAddress());
				for (Integer share : othershares) {
					landlords.remove(share);
					logger.warn("(SI={}): share{} failed", storageIndex, share);
				}
			}
		} else {
			logger.warn("(SI={}): share{} failed", storageIndex, sharenum);
			landlords.remove(sharenum);
		}

		barrier.setLimit(landlords.size());

		logger.debug("(SI={}): currentShareHolders={}", storageIndex,
				DumpUtils.dumptolog(landlords));

		// TODO: server of happiness
		if (landlords.size() < encodingParam.getK()) {
			logger.warn("(SI={}): uploading failed, shareholders={}",
					storageIndex, DumpUtils.dumptolog(landlords));
			trigger(new EncoderDone(
					new StatusMsg(Status.Failed,
							"number of available servers is not enough to upload the file"),
					null), encoder);
			return false;
		}
		return true;
	}

	/**
	 * Send_all_block_hash_trees.
	 */
	public void send_all_block_hash_trees() {
		logger.info("(SI={}): sending all block hashtrees", storageIndex);
		for (int i = 0; i < encodingParam.getN(); i++) {
			HashTree blockHashes = new HashTree(block_hashes.get(i));
			share_root_hashes.add(blockHashes.get(0));
			if (landlords.containsKey(i)) {
				trigger(new PutBlockHashData(landlords.get(i).getId(),
						blockHashes), wbproxy);
			}
		}
	}

	/**
	 * Send_all_share_hash_trees.
	 */
	public void send_all_share_hash_trees() {

		logger.info("(SI={}): sending all shares hashtrees", storageIndex);
		HashTree ht = new HashTree(share_root_hashes);
		ByteArray rootHash = ht.get(0);
		logger.debug("(SI={}): share root hash ={}", storageIndex,
				rootHash.getArrayinBase32());

		uriExtensionData.addKeyValue("share_root_hash", rootHash);

		for (int i : landlords.keySet()) {
			List<Integer> needed = ht.neededHashes(i, true);
			Map<Short, ByteArray> needed_hashes = new Hashtable<Short, ByteArray>();
			logger.debug(
					"(SI={}): share{} needs hashes {} from share root hashes",
					new Object[] { storageIndex, i, DumpUtils.dumptolog(needed) });
			for (int k : needed) {
				needed_hashes.put((short) k, ht.get(k));
			}

			trigger(new PutShareHashData(landlords.get(i).getId(),
					needed_hashes), wbproxy);
		}
	}

	/**
	 * Send_close.
	 */
	public void send_close() {
		logger.debug("(SI={}): send close to all shares", storageIndex);
		for (int shareid : landlords.keySet()) {
			trigger(new Close(landlords.get(shareid).getId()), wbproxy);
		}
	}

	/**
	 * Send_crypttext_hash_tree_to_all_shareholders.
	 */
	private void send_crypttext_hash_tree_to_all_shareholders() {
		logger.info("(SI={}): send crypttext hash tree to all shareholders",
				storageIndex);
		HashTree all_hashes = new HashTree(cryptext_hashes);
		uriExtensionData.addKeyValue("crypttext_root_hash", all_hashes.get(0));

		for (int i : landlords.keySet()) {
			trigger(new PutCryptHashData(landlords.get(i).getId(), all_hashes),
					wbproxy);
		}

	}

	/**
	 * Send_uri_extension_to_all_shareholders.
	 */
	public void send_uri_extension_to_all_shareholders() {
		logger.info("(SI={}): send UEB to all shareholders", storageIndex);
		byte[] uri_packed = uriExtensionData.getPackedUEB();
		uri_extension_hash = Hash.uri_extension_hash(uri_packed);
		logger.debug(
				"(SI={}): UEB={}, UEBHash={}",
				new Object[] { storageIndex, uriExtensionData,
						Base32.encode(uri_extension_hash) });
		for (int shareid : landlords.keySet()) {
			trigger(new PutUEB(landlords.get(shareid).getId(), uri_packed),
					wbproxy);
		}
	}

	/**
	 * Sets the encryptable uploadable.
	 * 
	 * @param eu
	 *            the new encryptable uploadable
	 */
	private void setEncryptableUploadable(EncryptFileHandle eu) {
		uploadable = eu;
		long filesize = eu.getSize();
		try {
			storageIndex = Base32.encode(uploadable.getStorageIndex());
		} catch (IOException e) {
			logger.error("Exception while getting the storageIndex: ", e);
		}
		encodingParam = eu.getEncodingParam();

		logger.debug("(SI={}): Encoding Params={}", encodingParam);

		num_segments = (int) MathUtils.div_ceil(filesize,
				encodingParam.getSegmentSize());
		sharesize = MathUtils.div_ceil(filesize, encodingParam.getK());

		block_size = (int) MathUtils.div_ceil(encodingParam.getSegmentSize(),
				encodingParam.getK());

		uriExtensionData.addKeyValue("codec_name", "crs");
		uriExtensionData.addKeyValue("codec_params", encodingParam.toString());
		uriExtensionData.addKeyValue("size", filesize);
		uriExtensionData.addKeyValue("num_segments", num_segments);
		uriExtensionData.addKeyValue("segment_size",
				encodingParam.getSegmentSize());
		uriExtensionData.addKeyValue("needed_shares", encodingParam.getK());
		uriExtensionData.addKeyValue("total_shares", encodingParam.getN());

		long tail_size = filesize % encodingParam.getSegmentSize();
		tail_size = tail_size == 0 ? encodingParam.getSegmentSize() : tail_size;

		long padded_tail_size = MathUtils.next_multiple(tail_size,
				encodingParam.getK());

		EncodingParam tail_encoding_params = new EncodingParam(
				encodingParam.getK(), encodingParam.getN(), padded_tail_size);
		last_block_size = (int) MathUtils.div_ceil(padded_tail_size,
				encodingParam.getK());

		uriExtensionData.addKeyValue("tail_codec_params",
				tail_encoding_params.toString());
	}

	/**
	 * Start_all_shareholders.
	 */
	private void start_all_shareholders() {
		for (Integer i : landlords.keySet()) {
			logger.debug("(SI={}): PutHeader for ShareFile ({})", storageIndex,
					i);
			trigger(new PutHeader(landlords.get(i).getId()), wbproxy);
		}

		if (fileAlreadyExists) {
			putBlockCompleted();
		}
	}
}
