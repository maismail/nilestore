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
package eg.nileu.cis.nilestore.storage.immutable.writer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.connectionfd.port.CancelNotifyonFailure;
import eg.nileu.cis.nilestore.connectionfd.port.ConnectionFailure;
import eg.nileu.cis.nilestore.connectionfd.port.NotifyonFailure;
import eg.nileu.cis.nilestore.storage.AbortBucket;
import eg.nileu.cis.nilestore.storage.AbstractStorageServer;
import eg.nileu.cis.nilestore.storage.CloseShareFile;
import eg.nileu.cis.nilestore.storage.immutable.ImmutableShareFile;
import eg.nileu.cis.nilestore.storage.immutable.UnkownImmutableContainerVersionError;
import eg.nileu.cis.nilestore.storage.immutable.writer.port.OperationCompleted;
import eg.nileu.cis.nilestore.storage.immutable.writer.port.RemoteWrite;
import eg.nileu.cis.nilestore.utils.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NsBucketWriter.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsBucketWriter extends ComponentDefinition {

	/** The net. */
	Positive<Network> net = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsBucketWriter.class);

	/** The self. */
	private ComponentAddress self;

	/** The dest. */
	private ComponentAddress dest;

	/** The sharefile. */
	private ImmutableShareFile sharefile;

	/** The incominghome. */
	private String incominghome;

	/** The finalhome. */
	private String finalhome;

	/** The filename. */
	private String filename;

	/** The parent. */
	private AbstractStorageServer parent;

	/** The first write. */
	private boolean firstWrite;

	/** The last accessed time. */
	private long lastAccessedTime;

	/** The max rtt. */
	private long maxRTT;

	/** The notifyon failure. */
	private NotifyonFailure notifyonFailure;

	/**
	 * Instantiates a new ns bucket writer.
	 */
	public NsBucketWriter() {
		firstWrite = true;
		maxRTT = 10000;

		subscribe(handleInit, control);

		subscribe(handleWrite, net);
		subscribe(handleAbort, net);
		subscribe(handleCloseShareFile, net);
		subscribe(handleFailure, cfd);
	}

	/** The handle init. */
	Handler<NsBucketWriterInit> handleInit = new Handler<NsBucketWriterInit>() {

		@Override
		public void handle(NsBucketWriterInit init) {

			self = init.getSelf();
			dest = init.getDest();
			incominghome = init.getIncomingHome();
			finalhome = init.getFinalHome();
			filename = init.getFilename();
			parent = init.getParent();
			try {

				FileUtils.mkdirsifnotExists(incominghome);

				sharefile = new ImmutableShareFile(FileUtils.JoinPath(
						incominghome, filename), init.getMaxSpacePerBucket(),
						true);
				sharefile.add_lease(init.getLeaseInfo());
				logger.info("create ShareFile {}",
						FileUtils.JoinPath(incominghome, filename));
			} catch (IOException e) {
				logger.error("Exception while reading sharefile", e);
			} catch (UnkownImmutableContainerVersionError e) {
				logger.error("Exception while reading sharefile", e);
				// TODO: let the other side know about that error?
			}
		}

	};

	/** The handle write. */
	Handler<RemoteWrite> handleWrite = new Handler<RemoteWrite>() {
		@Override
		public void handle(RemoteWrite event) {
			update();
			long offset = event.getOffset();
			byte[] data = event.getData();

			logger.debug("{} got remote write [offset={}, length={}] from {}",
					new Object[] { self, offset, data.length, dest });

			try {
				sharefile.write_share_data(event.getOffset(), event.getData());
				trigger(new OperationCompleted(self, dest), net);
			} catch (IOException e) {
				logger.error("Exception while writing sharefile", e);
				// TODO: propagate this error to the other side to use other
				// writers
			}
		}

	};

	/** The handle abort. */
	Handler<AbortBucket> handleAbort = new Handler<AbortBucket>() {
		@Override
		public void handle(AbortBucket event) {

			logger.debug("{} got Abort from {}", self, dest);
			FileUtils.rmdir2(incominghome, 2);
			FileUtils.rmdir2(finalhome, 2);
			parent.bucketClosed(self.getId());
		}
	};

	/** The handle close share file. */
	Handler<CloseShareFile> handleCloseShareFile = new Handler<CloseShareFile>() {

		@Override
		public void handle(CloseShareFile event) {

			logger.info("{} got close share file from {}", self, dest);
			FileUtils.mkdirs(finalhome);
			FileUtils.mvfile(FileUtils.JoinPath(incominghome, filename),
					FileUtils.JoinPath(finalhome, filename));
			FileUtils.rmdir2(incominghome, 2);

			trigger(new OperationCompleted(self, dest), net);
			parent.bucketClosed(self.getId());
		}
	};

	/** The handle failure. */
	Handler<ConnectionFailure> handleFailure = new Handler<ConnectionFailure>() {
		@Override
		public void handle(ConnectionFailure event) {
			logger.info("{}: connection to {} failed", self, dest);

			FileUtils.rmdir2(FileUtils.JoinPath(incominghome, filename), 3);
			parent.bucketClosed(self.getId());
		}
	};

	/**
	 * Update.
	 */
	private void update() {
		long now = System.currentTimeMillis();

		if (firstWrite) {
			lastAccessedTime = now;
			firstWrite = false;
		} else {
			logger.debug("{}: send cancel notifyonFailure to CFD", self);
			trigger(new CancelNotifyonFailure(notifyonFailure), cfd);
		}

		long elapsed = now - lastAccessedTime;
		if (elapsed > maxRTT) {
			maxRTT = elapsed;
		}
		lastAccessedTime = now;

		logger.debug("{}: maxRTT={}", self, maxRTT);
		notifyonFailure = new NotifyonFailure(maxRTT * 2, dest.getAddress());
		logger.debug("{}: trigger {}", self, notifyonFailure);
		trigger(notifyonFailure, cfd);
	}

}
