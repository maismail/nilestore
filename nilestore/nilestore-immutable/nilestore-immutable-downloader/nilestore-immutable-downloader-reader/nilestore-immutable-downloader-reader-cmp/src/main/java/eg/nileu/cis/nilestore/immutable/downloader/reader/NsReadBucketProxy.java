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
package eg.nileu.cis.nilestore.immutable.downloader.reader;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import eg.nileu.cis.nilestore.immutable.common.PutGetData;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetBlock;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetBlockResponse;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetUEB;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GetUEBResponse;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GotCiphertextHashes;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.GotSharesHashes;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.RBProxy;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.SetCommonParameters;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.storage.CloseShareFile;
import eg.nileu.cis.nilestore.storage.immutable.ImmutableShareFile;
import eg.nileu.cis.nilestore.storage.immutable.reader.port.RemoteRead;
import eg.nileu.cis.nilestore.storage.immutable.reader.port.RemoteReadResponse;
import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.DataUtils;
import eg.nileu.cis.nilestore.utils.hashtree.BadHashError;
import eg.nileu.cis.nilestore.utils.hashtree.NotEnoughHashesError;

// TODO: Auto-generated Javadoc
/**
 * The Class NsReadBucketProxy.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsReadBucketProxy extends ComponentDefinition {

	/** The rbpport. */
	Negative<RBProxy> rbpport = provides(RBProxy.class);

	/** The net. */
	Positive<Network> net = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/**
	 * The Enum Operation.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	private enum Operation {

		/** The Get header. */
		GetHeader,

		/** The Set common params. */
		SetCommonParams,

		/** The Get ueb. */
		GetUEB,

		/** The Get block. */
		GetBlock,

		/** The Get crypto hashes. */
		GetCryptoHashes,

		/** The Get block hashes. */
		GetBlockHashes,

		/** The Get share hashes. */
		GetShareHashes,

		/** The Close. */
		Close
	}

	/** The self. */
	private ComponentAddress self;

	/** The dest. */
	private ComponentAddress dest;

	/** The sharenum. */
	private int sharenum;

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsReadBucketProxy.class);

	/** The common. */
	private CommonShare common;

	/** The active read operations. */
	private Map<Long, Operation> activeReadOperations;

	/** The segment num associated. */
	private Map<Long, Integer> segmentNumAssociated;

	// private List<PutGetData> postponedEvents;
	/** The postponed events. */
	private Map<Operation, List<PutGetData>> postponedEvents;

	/** The notifyon failure event. */
	private NotifyonFailure notifyonFailureEvent;

	/** The request id. */
	private long requestId;

	/** The closed. */
	private boolean closed;

	/**
	 * Instantiates a new ns read bucket proxy.
	 */
	public NsReadBucketProxy() {
		requestId = 0;
		closed = false;

		common = new CommonShare();
		activeReadOperations = new HashMap<Long, Operation>();
		segmentNumAssociated = new HashMap<Long, Integer>();
		postponedEvents = new HashMap<NsReadBucketProxy.Operation, List<PutGetData>>();

		subscribe(handleInit, control);
		subscribe(handleGetUEB, rbpport);
		subscribe(handleSetCommonParameters, rbpport);
		subscribe(handleGetBlock, rbpport);
		subscribe(handleClose, rbpport);

		subscribe(handleReadResponse, net);

		subscribe(handleFailure, cfd);
	}

	/** The handle init. */
	Handler<NsReadBucketProxyInit> handleInit = new Handler<NsReadBucketProxyInit>() {

		@Override
		public void handle(NsReadBucketProxyInit init) {

			self = init.getSelf();
			dest = init.getDest();
			sharenum = init.getSharenum();

			logger.debug("({}) initiated for ({})", self, dest);

			notifyonFailureEvent = new NotifyonFailure(dest.getAddress());
			getHeader();
		}
	};

	/** The handle get ueb. */
	Handler<GetUEB> handleGetUEB = new Handler<GetUEB>() {

		@Override
		public void handle(GetUEB event) {
			if (!common.haveHeader()) {
				postponeEvent(event, Operation.GetHeader);
				return;
			}
			getUEB();
		}
	};

	/** The handle get block. */
	Handler<GetBlock> handleGetBlock = new Handler<GetBlock>() {
		@Override
		public void handle(GetBlock event) {
			if (!common.haveHeader()) {
				postponeEvent(event, Operation.GetHeader);
				return;
			}
			if (!common.haveCommonParams()) {
				postponeEvent(event, Operation.SetCommonParams);
				return;
			}
			if (common.needHashesToValidate(event.getSegmentNum())) {
				postponeEvent(event, Operation.GetBlockHashes);
				getBlockHashTree();
				return;
			}
			getBlock(event.getSegmentNum());
		}
	};

	/** The handle set common parameters. */
	Handler<SetCommonParameters> handleSetCommonParameters = new Handler<SetCommonParameters>() {
		@Override
		public void handle(SetCommonParameters event) {

			if (!common.haveHeader()) {
				postponeEvent(event, Operation.GetHeader);
				return;
			}

			logger.debug("({}) SETCOMMON from parent", self);
			common.setCommonParameters(event);
			triggerPostponedEvents(Operation.SetCommonParams);
			getShareHashTree();
		}
	};

	/** The handle read response. */
	Handler<RemoteReadResponse> handleReadResponse = new Handler<RemoteReadResponse>() {

		@Override
		public void handle(RemoteReadResponse event) {
			trigger(new CancelNotifyonFailure(notifyonFailureEvent), cfd);
			// logger.debug("ActiveReadOperations={}",
			// DumpUtils.dumptolog(activeReadOperations));
			long reqId = event.getRequestId();
			byte[] data = event.getData();
			Operation currentOperation = getReadOperation(reqId);

			if (currentOperation == null) {
				logger.debug(
						"({}), UNUSUAL: remote read response refers to an operation which is not exists anymore",
						self);
				return;
			}
			switch (currentOperation) {
			case GetHeader:
				gotHeader(data);
				break;
			case GetUEB:
				gotUEB(data);
				break;
			case GetBlock:
				gotBlock(data, reqId);
				break;
			case GetShareHashes:
				gotShareHashTree(data);
				break;
			case GetCryptoHashes:
				gotCipherTextHashTree(data);
				break;
			case GetBlockHashes:
				gotBlockHashTree(data);
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

			logger.info("({}) failed", self);
			synchronized (activeReadOperations) {
				for (Operation op : activeReadOperations.values()) {
					switch (op) {
					case GetBlock:
						trigger(new GetBlockResponse(sharenum, Status.Failed,
								null), rbpport);
						break;
					case GetUEB:
						trigger(new GetUEBResponse(sharenum, Status.Failed,
								null), rbpport);
						break;
					default:
						break;
					}
				}
			}
		}
	};

	/** The handle close. */
	Handler<Close> handleClose = new Handler<Close>() {
		@Override
		public void handle(Close event) {
			if (closed)
				return;

			logger.info("({}) send close to ({})", self, dest);
			trigger(new CloseShareFile(self, dest), net);
		}
	};

	/**
	 * Gets the header.
	 * 
	 * @return the header
	 */
	private void getHeader() {

		logger.debug("({}) getheader from ({})", self, dest);

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetHeader);

		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId, 0,
				ImmutableShareFile.HEADER_SIZE), net);
	}

	/**
	 * Got header.
	 * 
	 * @param data
	 *            the data
	 */
	private void gotHeader(byte[] data) {
		common.gotHeader(data);
		triggerPostponedEvents(Operation.GetHeader);
	}

	/**
	 * Gets the uEB.
	 * 
	 * @return the uEB
	 */
	private void getUEB() {
		logger.debug("({}) getUEB from ({})", self, dest);

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetUEB);

		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId,
				common.getUriExtensionOffset(), FileInfo.MaxURIExt), net);
	}

	/**
	 * Got ueb.
	 * 
	 * @param data
	 *            the data
	 */
	private void gotUEB(byte[] data) {
		
		getCiphertextHashTree();
		
		logger.debug("({}) gotUEB", self);
		ByteBuffer buffer = ByteBuffer.wrap(data);
		int len = buffer.getInt();
		// UEB ueb = new UEB(Arrays.copyOfRange(data, 4, len));
		trigger(new GetUEBResponse(sharenum, Status.Succeeded,
				Arrays.copyOfRange(data, 4, len + 4)), rbpport);
	}

	/**
	 * Gets the block.
	 * 
	 * @param segmentnum
	 *            the segmentnum
	 * @return the block
	 */
	private void getBlock(int segmentnum) {

		logger.debug("({}) getBlock from ({}) for segment#{}", new Object[] {
				self, dest, segmentnum });
		int offset = common.getDataOffset() + segmentnum
				* common.getBlockSize();

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetBlock);

		synchronized (segmentNumAssociated) {
			segmentNumAssociated.put(reqId, segmentnum);
		}

		int blockSize = common.isTailSegment(segmentnum) ? common
				.getTailBlockSize() : common.getBlockSize();

		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId, offset, blockSize), net);
	}

	/**
	 * Got block.
	 * 
	 * @param data
	 *            the data
	 * @param reqId
	 *            the req id
	 */
	private void gotBlock(byte[] data, long reqId) {

		Integer segmentNum = null;
		synchronized (segmentNumAssociated) {
			segmentNum = segmentNumAssociated.remove(reqId);
		}
		if (segmentNum == null) {
			logger.debug(
					"({}), UNUSUAL: segment number wasn't in the segmentNumAssociated list",
					self);
			return;
		}
		try {
			common.checkBlock(data, segmentNum.intValue());
		} catch (BadHashError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughHashesError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		trigger(new GetBlockResponse(sharenum, Status.Succeeded, data), rbpport);

		if (common.isTailSegment(segmentNum)) {
			logger.debug(
					"({}), since we have requested the last block send a close request",
					self);
			trigger(new CloseShareFile(self, dest), net);
			closed = true;
		}
	}

	/**
	 * Gets the share hash tree.
	 * 
	 * @return the share hash tree
	 */
	private void getShareHashTree() {

		logger.debug("({}) getSharehashTree from ({})", self, dest);
		int hashlen = common.getUriExtensionOffset()
				- common.getShareHashesOffset();

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetShareHashes);

		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId,
				common.getShareHashesOffset(), hashlen), net);
	}

	/**
	 * Got share hash tree.
	 * 
	 * @param data
	 *            the data
	 */
	private void gotShareHashTree(byte[] data) {
		logger.debug("({}) getSharehashtree from ({})", self, dest);

		Map<Integer, ByteArray> sharehashes = DataUtils.unpackShareHashes(data);
		ByteArray block_root_hash = sharehashes.get(common.getIndexAtShareHT());
		common.setBlockRootHash(block_root_hash);
		// logger.debug("({}), BlockHT={}", self, common.getBlockHashTree());
		trigger(new GotSharesHashes(sharenum, Status.Succeeded, sharehashes),
				rbpport);
	}

	/**
	 * Gets the ciphertext hash tree.
	 * 
	 * @return the ciphertext hash tree
	 */
	private void getCiphertextHashTree() {
		logger.debug("({}) getCiphertextHashTree from ({})", self, dest);
		int hashlen = common.getBlocksHashesOffset()
				- common.getCiphertextHashOffset();

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetCryptoHashes);

		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId,
				common.getCiphertextHashOffset(), hashlen), net);
	}

	/**
	 * Got cipher text hash tree.
	 * 
	 * @param data
	 *            the data
	 */
	private void gotCipherTextHashTree(byte[] data) {
		logger.debug("({}) gotCiphertextHashTree from ({})", self, dest);
		Map<Integer, ByteArray> ciphertextHashes = DataUtils
				.unpackHashesList(data);
		trigger(new GotCiphertextHashes(sharenum, Status.Succeeded,
				ciphertextHashes), rbpport);
	}

	// TODO: instead of getting the whole tree try to parse the needed elements
	// from the hash tree
	/**
	 * Gets the block hash tree.
	 * 
	 * @return the block hash tree
	 */
	private void getBlockHashTree() {
		logger.debug("({}) getBlockHashTree from ({})", self, dest);
		int hashlen = common.getShareHashesOffset()
				- common.getBlocksHashesOffset();

		long reqId = getRequestId();

		addActiveReadOperation(reqId, Operation.GetBlockHashes);
		// TODO: add a method triggerWithNotification
		trigger(notifyonFailureEvent, cfd);
		trigger(new RemoteRead(self, dest, reqId,
				common.getBlocksHashesOffset(), hashlen), net);
	}

	/**
	 * Got block hash tree.
	 * 
	 * @param data
	 *            the data
	 */
	private void gotBlockHashTree(byte[] data) {
		logger.debug("({}) got block hash tree from ({})", self, dest);
		Map<Integer, ByteArray> blockHashes = DataUtils.unpackHashesList(data);
		try {
			common.getBlockHashTree().setHashes(blockHashes);
			// logger.debug("({}), BlockHT={}", self,
			// common.getBlockHashTree());
			triggerPostponedEvents(Operation.GetBlockHashes);

		} catch (BadHashError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughHashesError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds the active read operation.
	 * 
	 * @param reqId
	 *            the req id
	 * @param operation
	 *            the operation
	 */
	private void addActiveReadOperation(Long reqId, Operation operation) {
		synchronized (activeReadOperations) {
			activeReadOperations.put(reqId, operation);
		}
	}

	/**
	 * Gets the read operation.
	 * 
	 * @param reqId
	 *            the req id
	 * @return the read operation
	 */
	private Operation getReadOperation(Long reqId) {
		synchronized (activeReadOperations) {
			return activeReadOperations.remove(reqId);
		}
	}

	/**
	 * Postpone event.
	 * 
	 * @param event
	 *            the event
	 * @param afterOperation
	 *            the after operation
	 */
	private void postponeEvent(PutGetData event, Operation afterOperation) {
		logger.debug("({}) postpone Event {} after completeting operation {}",
				new Object[] { self, event.getClass().getCanonicalName(),
						afterOperation });
		synchronized (postponedEvents) {
			if (!postponedEvents.containsKey(afterOperation)) {
				postponedEvents
						.put(afterOperation, new ArrayList<PutGetData>());
			}
			postponedEvents.get(afterOperation).add(event);
		}
	}

	/**
	 * Trigger postponed events.
	 * 
	 * @param afterOperation
	 *            the after operation
	 */
	private void triggerPostponedEvents(Operation afterOperation) {
		logger.debug("({}) trigger postponed events after {}", self,
				afterOperation);
		synchronized (postponedEvents) {
			List<PutGetData> events = postponedEvents.remove(afterOperation);
			logger.debug("({}) postponedEvents after {} = {}", new Object[] {
					self, afterOperation, dump(events) });
			if (events == null) {
				return;
			}

			for (PutGetData event : events) {
				if (event instanceof GetUEB) {
					handleGetUEB.handle((GetUEB) event);
				} else if (event instanceof GetBlock) {
					handleGetBlock.handle((GetBlock) event);
				} else if (event instanceof SetCommonParameters) {
					handleSetCommonParameters
							.handle((SetCommonParameters) event);
				}
			}
		}
	}

	/**
	 * Gets the request id.
	 * 
	 * @return the request id
	 */
	private synchronized long getRequestId() {
		return ++requestId;
	}

	/**
	 * Dump.
	 * 
	 * @param ls
	 *            the ls
	 * @return the string
	 */
	private String dump(List<PutGetData> ls) {
		if (ls == null)
			return "[]";
		String s = "[";
		for (PutGetData e : ls) {
			s += e.getClass().getCanonicalName() + ",";
		}
		s = s.substring(0, s.length() - 1);
		s += "]";
		return s;
	}
}
