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
package eg.nileu.cis.nilestore.immutable.uploader.writer;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.connectionfd.port.CancelNotifyonFailure;
import eg.nileu.cis.nilestore.connectionfd.port.ConnectionFailure;
import eg.nileu.cis.nilestore.connectionfd.port.NotifyonFailure;
import eg.nileu.cis.nilestore.immutable.common.Close;
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
import eg.nileu.cis.nilestore.storage.CloseShareFile;
import eg.nileu.cis.nilestore.storage.immutable.ImmutableShareFile;
import eg.nileu.cis.nilestore.storage.immutable.writer.port.OperationCompleted;
import eg.nileu.cis.nilestore.storage.immutable.writer.port.RemoteWrite;
import eg.nileu.cis.nilestore.utils.DataUtils;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWriteBucketProxy.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsWriteBucketProxy extends ComponentDefinition {

	/** The wb proxy. */
	Negative<WBProxy> wbProxy = provides(WBProxy.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/**
	 * The Enum Operation.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	private enum Operation {

		/** The Put header. */
		PutHeader,

		/** The Put block. */
		PutBlock,

		/** The Put crypto hashes. */
		PutCryptoHashes,

		/** The Put block hashes. */
		PutBlockHashes,

		/** The Put share hashes. */
		PutShareHashes,

		/** The Put ueb. */
		PutUEB,

		/** The Close. */
		Close
	}

	/** The logger. */
	private Logger logger;

	/** The dest. */
	private ComponentAddress dest;

	/** The self. */
	private ComponentAddress self;

	/** The sharenum. */
	private int sharenum;

	/** The data_size. */
	private long data_size;

	/** The block_size. */
	private int block_size;

	/** The segment_hash_size. */
	private int segment_hash_size;

	/** The share_hashtree_size. */
	private int share_hashtree_size;

	/** The offsets. */
	private Map<String, Integer> offsets;

	/** The current operation. */
	private Operation currentOperation;

	/** The notifyon failure event. */
	private NotifyonFailure notifyonFailureEvent;

	/**
	 * Instantiates a new ns write bucket proxy.
	 */
	public NsWriteBucketProxy() {

		subscribe(handleInit, control);

		subscribe(handlePutHeader, wbProxy);
		subscribe(handlePutBlock, wbProxy);
		subscribe(handlePutBlockHashData, wbProxy);
		subscribe(handlePutCryptHashData, wbProxy);
		subscribe(handlePutShareHashData, wbProxy);
		subscribe(handlePutUEB, wbProxy);
		subscribe(handleClose, wbProxy);

		subscribe(handleOperationCompleted, network);
		subscribe(handleFailure, cfd);

	}

	/** The handle init. */
	Handler<NsWriteBucketProxyInit> handleInit = new Handler<NsWriteBucketProxyInit>() {

		@Override
		public void handle(NsWriteBucketProxyInit init) {

			logger = Slf4jInstantiator.getLogger(NsWriteBucketProxy.class, init
					.getSelfAddress().getNickname());
			dest = init.getDest();
			self = init.getSelf();
			sharenum = init.getSharenum();

			data_size = init.getFileinfo().getShareSize();
			block_size = init.getFileinfo().getBlockSize();

			int effective_segments = MathUtils.next_power_of_k(init
					.getFileinfo().getNumSegments(), 2);
			segment_hash_size = (2 * effective_segments - 1) * Hash.HASH_SIZE;
			share_hashtree_size = init.getFileinfo().getNumShareHashes()
					* (2 + Hash.HASH_SIZE);

			logger.debug("({}): initiated", self);
			createOffsets();

		}

	};

	/** The handle put header. */
	Handler<PutHeader> handlePutHeader = new Handler<PutHeader>() {

		@Override
		public void handle(PutHeader event) {

			logger.debug("({}): putHeader on ({})", self, dest);

			ByteBuffer buffer = ByteBuffer
					.allocate(ImmutableShareFile.HEADER_SIZE);

			buffer.putInt(1);
			buffer.putInt(block_size);
			buffer.putInt((int) data_size); // FIXME: in Version 2 all offsets
											// are of type long (8 bytes) to
											// hold larger files
			buffer.putInt(offsets.get("data"));
			buffer.putInt(offsets.get("plaintext_hash_tree"));
			buffer.putInt(offsets.get("crypttext_hash_tree"));
			buffer.putInt(offsets.get("block_hashes"));
			buffer.putInt(offsets.get("share_hashes"));
			buffer.putInt(offsets.get("uri_extension"));

			currentOperation = Operation.PutHeader;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			trigger(new RemoteWrite(self, dest, 0, buffer.array()), network);
		}

	};

	// TODO: conditions on the size of the data
	/** The handle put block. */
	Handler<PutBlock> handlePutBlock = new Handler<PutBlock>() {

		@Override
		public void handle(PutBlock event) {

			logger.debug("({}): putBlock on ({})", self, dest);
			long offset = offsets.get("data") + event.getSegnum() * block_size;
			logger.debug("({}): block={}, offset={}, size={}", new Object[] {
					self, event.getSegnum(), offset, block_size });

			currentOperation = Operation.PutBlock;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			logger.debug("({}): remoteWrite on ({}) offset={},len={}",
					new Object[] { self, dest, offset, block_size });
			trigger(new RemoteWrite(self, dest, offset, event.getBlockData()),
					network);
		}
	};

	/** The handle put crypt hash data. */
	Handler<PutCryptHashData> handlePutCryptHashData = new Handler<PutCryptHashData>() {

		@Override
		public void handle(PutCryptHashData event) {

			logger.debug("({}): putCrypttextHashes on ({})", self, dest);
			long offset = offsets.get("crypttext_hash_tree");
			byte[] data = DataUtils.packHashesList(event.getHashes());

			currentOperation = Operation.PutCryptoHashes;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			logger.debug("({}): remoteWrite on ({}) offset={},len={}",
					new Object[] { self, dest, offset, data.length });
			trigger(new RemoteWrite(self, dest, offset, data), network);
		}
	};

	/** The handle put block hash data. */
	Handler<PutBlockHashData> handlePutBlockHashData = new Handler<PutBlockHashData>() {

		@Override
		public void handle(PutBlockHashData event) {

			logger.debug("({}): putBlockHashes on ({})", self, dest);

			long offset = offsets.get("block_hashes");

			byte[] data = DataUtils.packHashesList(event.getHashes());

			currentOperation = Operation.PutBlockHashes;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			logger.debug("({}): remoteWrite on ({}) offset={},len={}",
					new Object[] { self, dest, offset, data.length });
			trigger(new RemoteWrite(self, dest, offset, data), network);
		}

	};

	/** The handle put share hash data. */
	Handler<PutShareHashData> handlePutShareHashData = new Handler<PutShareHashData>() {

		@Override
		public void handle(PutShareHashData event) {

			logger.debug("({}): putBlockHashes on ({})", self, dest);
			long offset = offsets.get("share_hashes");

			byte[] data = DataUtils.packShareHashes(event.getHashes());

			currentOperation = Operation.PutShareHashes;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			logger.debug("({}): remoteWrite on ({}) offset={},len={}",
					new Object[] { self, dest, offset, data.length });
			trigger(new RemoteWrite(self, dest, offset, data), network);
		}
	};

	/** The handle put ueb. */
	Handler<PutUEB> handlePutUEB = new Handler<PutUEB>() {

		@Override
		public void handle(PutUEB event) {

			logger.debug("({}): putUEB on ({})", self, dest);
			long offset = offsets.get("uri_extension");

			int length = event.getData().length;
			ByteBuffer buffer = ByteBuffer.allocate(length + 4);
			buffer.putInt(length);
			buffer.put(event.getData());
			byte[] data = buffer.array();

			currentOperation = Operation.PutUEB;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			logger.debug("({}): remoteWrite on ({}) offset={},len={}",
					new Object[] { self, dest, offset, data.length });
			trigger(new RemoteWrite(self, dest, offset, data), network);
		}

	};

	/** The handle close. */
	Handler<Close> handleClose = new Handler<Close>() {

		@Override
		public void handle(Close event) {

			logger.debug("({}): close ({})", self, dest);

			currentOperation = Operation.Close;

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			trigger(notifyonFailureEvent, cfd);

			trigger(new CloseShareFile(self, dest), network);
		}

	};

	/** The handle operation completed. */
	Handler<OperationCompleted> handleOperationCompleted = new Handler<OperationCompleted>() {
		@Override
		public void handle(OperationCompleted event) {

			trigger(new CancelNotifyonFailure(notifyonFailureEvent), cfd);
			logger.debug("({}): got {} completed from ({})", new Object[] {
					self, currentOperation, dest });

			switch (currentOperation) {
			case PutHeader:
				trigger(new PutHeaderCompleted(sharenum, Status.Succeeded),
						wbProxy);
				break;
			case PutBlock:
				trigger(new PutBlockCompleted(sharenum, Status.Succeeded),
						wbProxy);
				break;
			case PutCryptoHashes:
				trigger(new PutCryptHashDataCompleted(sharenum,
						Status.Succeeded), wbProxy);
				break;
			case PutBlockHashes:
				trigger(new PutBlockHashDataCompleted(sharenum,
						Status.Succeeded), wbProxy);
				break;
			case PutShareHashes:
				trigger(new PutShareHashDataCompleted(sharenum,
						Status.Succeeded), wbProxy);
				break;
			case PutUEB:
				trigger(new PutUEBCompleted(sharenum, Status.Succeeded),
						wbProxy);
				break;
			case Close:
				trigger(new CloseCompleted(sharenum, Status.Succeeded), wbProxy);
				break;
			default:
				break;
			}
		}
	};

	/** The handle failure. */
	Handler<ConnectionFailure> handleFailure = new Handler<ConnectionFailure>() {
		@Override
		public void handle(ConnectionFailure event) {

			logger.debug("({}): failed at {}", self, currentOperation);

			switch (currentOperation) {
			case PutHeader:
				trigger(new PutHeaderCompleted(sharenum, Status.Failed),
						wbProxy);
				break;
			case PutBlock:
				trigger(new PutBlockCompleted(sharenum, Status.Failed), wbProxy);
				break;
			case PutCryptoHashes:
				trigger(new PutCryptHashDataCompleted(sharenum, Status.Failed),
						wbProxy);
				break;
			case PutBlockHashes:
				trigger(new PutBlockHashDataCompleted(sharenum, Status.Failed),
						wbProxy);
				break;
			case PutShareHashes:
				trigger(new PutShareHashDataCompleted(sharenum, Status.Failed),
						wbProxy);
				break;
			case PutUEB:
				trigger(new PutUEBCompleted(sharenum, Status.Failed), wbProxy);
				break;
			case Close:
				trigger(new CloseCompleted(sharenum, Status.Failed), wbProxy);
				break;
			default:
				break;
			}
		}

	};

	/**
	 * Creates the offsets.
	 */
	private void createOffsets() {
		// TODO:this implementation for the Sharefile v1 (max size 2**32), v2
		// (max size 2**64) need to be implemented by changing the offsets from
		// integer to long

		offsets = new Hashtable<String, Integer>();

		int x = 0x24;
		offsets.put("data", x);
		x += data_size;
		offsets.put("plaintext_hash_tree", x);
		x += segment_hash_size;
		offsets.put("crypttext_hash_tree", x);
		x += segment_hash_size;
		offsets.put("block_hashes", x);
		x += segment_hash_size;
		offsets.put("share_hashes", x);
		x += share_hashtree_size;
		offsets.put("uri_extension", x);

		logger.debug("({}): create offsets={}", self,
				DumpUtils.dumptolog(offsets));
	}

	/**
	 * Gets the allocated size.
	 * 
	 * @param share_size
	 *            the share_size
	 * @param block_size
	 *            the block_size
	 * @param num_segments
	 *            the num_segments
	 * @param num_share_hashes
	 *            the num_share_hashes
	 * @param uri_extension_size_max
	 *            the uri_extension_size_max
	 * @return the allocated size
	 */
	public static int getAllocatedSize(long share_size, long block_size,
			int num_segments, int num_share_hashes, int uri_extension_size_max) {
		int size = 0;

		int effective_segments = MathUtils.next_power_of_k(num_segments, 2);
		int segment_hash_size = (2 * effective_segments - 1) * Hash.HASH_SIZE;

		int share_hashtree_size = num_share_hashes * (2 + Hash.HASH_SIZE);

		int x = 0x24;
		x += share_size + (3 * segment_hash_size) + share_hashtree_size;

		size = x + 4 + uri_extension_size_max;

		return size;

	}

}
