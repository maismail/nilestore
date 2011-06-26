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
package eg.nileu.cis.nilestore.immutable.manager;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.SecretHolder;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.common.TaggedRequest;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.downloader.NsDownloader;
import eg.nileu.cis.nilestore.immutable.downloader.NsDownloaderInit;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.DownloadingDone;
import eg.nileu.cis.nilestore.immutable.downloader.port.DownloadResponse;
import eg.nileu.cis.nilestore.immutable.downloader.port.Downloader;
import eg.nileu.cis.nilestore.immutable.file.FileHandle;
import eg.nileu.cis.nilestore.immutable.manager.port.Download;
import eg.nileu.cis.nilestore.immutable.manager.port.GetCapStore;
import eg.nileu.cis.nilestore.immutable.manager.port.GetCapStoreResponse;
import eg.nileu.cis.nilestore.immutable.manager.port.GotBlockData;
import eg.nileu.cis.nilestore.immutable.manager.port.Immutable;
import eg.nileu.cis.nilestore.immutable.manager.port.Upload;
import eg.nileu.cis.nilestore.immutable.manager.port.UploadCompleted;
import eg.nileu.cis.nilestore.immutable.uploader.NsUploader;
import eg.nileu.cis.nilestore.immutable.uploader.NsUploaderInit;
import eg.nileu.cis.nilestore.immutable.uploader.port.Uploader;
import eg.nileu.cis.nilestore.immutable.uploader.port.UploadingDone;
import eg.nileu.cis.nilestore.interfaces.file.IUploadable;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.uri.BadURIException;
import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsImmutableManager.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsImmutableManager extends ComponentDefinition {

	/** The immutablemanager. */
	Negative<Immutable> immutablemanager = provides(Immutable.class);

	/** The peers. */
	Positive<AvailablePeers> peers = requires(AvailablePeers.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The redundancy. */
	Positive<Redundancy> redundancy = requires(Redundancy.class);

	// TODO: add recent canceledrequests
	/** The self. */
	private NilestoreAddress self;

	/** The active operations. */
	private final Map<String, Component> activeOperations;

	/** The active requests. */
	private final Map<String, TaggedRequest> activeRequests;

	/** The logger. */
	private Logger logger;

	/** The secret holder. */
	private SecretHolder secretHolder;

	/** The cap store. */
	private CapsStore capStore;

	/** The home dir. */
	private String homeDir;

	/** The encoding param. */
	private EncodingParam encodingParam;

	/**
	 * Instantiates a new ns immutable manager.
	 */
	public NsImmutableManager() {
		activeOperations = new HashMap<String, Component>();
		activeRequests = new HashMap<String, TaggedRequest>();

		subscribe(handleInit, control);
		subscribe(handleUpload, immutablemanager);
		subscribe(handleDownload, immutablemanager);
		subscribe(handleGetCapStore, immutablemanager);

	}

	/** The handle init. */
	Handler<NsImmutableManagerInit> handleInit = new Handler<NsImmutableManagerInit>() {

		@Override
		public void handle(NsImmutableManagerInit init) {

			self = init.getSelf();
			logger = Slf4jInstantiator.getLogger(NsImmutableManager.class,
					self.getNickname());
			secretHolder = init.getSecretHolder();
			homeDir = init.getHomeDir();
			encodingParam = init.getEncodingParam();

			try {
				capStore = new CapsStore(homeDir);
			} catch (IOException e) {
				logger.error("Exception while initializing ", e);
			} catch (BadURIException e) {
				logger.error("Exception while initializing", e);
			}
			logger.info("started with encoding parameters = {}",
					encodingParam.toString());
		}

	};

	/** The handle upload. */
	Handler<Upload> handleUpload = new Handler<Upload>() {

		@Override
		public void handle(Upload event) {

			try {
				FileHandle filehandle = new FileHandle(event.getFilepath(),
						secretHolder.get_convergence_s(), encodingParam);
				long start = System.currentTimeMillis();
				byte[] storageIndexBytes = filehandle.getStorageIndex();
				long elapsed = System.currentTimeMillis() - start;

				filehandle.getUploadResults().setTimeforKey("storage_index",
						elapsed);

				String storageIndex = Base32.encode(storageIndexBytes);

				if (!ContainOperation(storageIndex)) {

					putRequest(storageIndex, event);
					createAndStartUploader(storageIndex, filehandle,
							secretHolder);
					logger.info("Uploader created for {}, storage Index = {} ",
							event.getFilename(), storageIndex);
				} else {
					trigger(new UploadCompleted(
							event,
							new StatusMsg(Status.Failed,
									"there is another uploading process for that file"),
							filehandle.getUploadResults()), immutablemanager);
					logger.info("cannot create an uploader because there is a running one now");
				}
			} catch (IOException e) {
				logger.error("Exception while handling an upload: ", e);
			}
		}
	};

	/** The handle uploading done. */
	Handler<UploadingDone> handleUploadingDone = new Handler<UploadingDone>() {

		@Override
		public void handle(UploadingDone event) {

			String storageIndex = event.getStorageIndex();

			Upload request = (Upload) removeRequest(storageIndex);
			if (request == null) {
				logger.debug("UNUSUAL: uploading completed but the request isn't available, request could be canceled");
				// FIXME: trigger new upload completed with the appropriate
				// status message
				return;
			}

			if (!event.getStatus().isSucceeded()) {
				trigger(new UploadCompleted(request, event.getStatus(),
						event.getUploadResults()), immutablemanager);
			} else {

				Date uploadDate = new Date(System.currentTimeMillis());
				capStore.addCapStoreItem(request.getFilename(),
						(CHKFileURI) event.getUploadResults().getUri(),
						uploadDate);
				try {
					capStore.Save();
				} catch (IOException e) {
					logger.error("Exception while saving the cap.store ", e);
				}

				trigger(new UploadCompleted(request, event.getStatus(),
						event.getUploadResults()), immutablemanager);
				logger.info("Uploading Completed for {} , storage Index={}",
						request.getFilename(), storageIndex);
				logger.info("URI = " + event.getUploadResults().getUri());
			}

			destroyUploader(storageIndex);
		}
	};

	/** The handle download. */
	Handler<Download> handleDownload = new Handler<Download>() {

		@Override
		public void handle(Download event) {

			String storageIndex = Base32.encode(event.getUriCap()
					.getStorageIndex());
			if (!ContainOperation(storageIndex)) {

				putRequest(storageIndex, event);
				createAndStartDownloader(storageIndex, event);
				logger.info("Downloader created for {}", event.getUriCap());
			} else {
				// FIXME: trigger download failure event back
				logger.info("downloader couldn't be create because there is an existing downloader");
			}

		}
	};

	/** The handle download done. */
	Handler<DownloadingDone> handleDownloadDone = new Handler<DownloadingDone>() {

		@Override
		public void handle(DownloadingDone event) {
			String storageIndex = event.getStorageIndex();

			Download request = (Download) removeRequest(storageIndex);

			if (request == null) {
				logger.debug("UNUSUAL: downloading completed but the request isn't available, request could be canceled");
				// FIXME: trigger new download completed with the appropriate
				// status message
				return;
			}

			if (!event.getStatus().isSucceeded()) {
				trigger(new GotBlockData(request, event.getStatus(), 1, 1,
						null, event.getStorageIndex()), immutablemanager);
			}
			destroyDownloader(storageIndex);
		}
	};

	/** The handle download response. */
	Handler<DownloadResponse> handleDownloadResponse = new Handler<DownloadResponse>() {
		@Override
		public void handle(DownloadResponse event) {
			String storageIndex = event.getStorageIndex();
			Download download = (Download) getRequest(storageIndex);

			StatusMsg status = new StatusMsg(Status.Succeeded, String.format(
					"got segment %s/%s", event.getIndex(), event.getTotal()));
			trigger(new GotBlockData(download, status, event.getIndex(), event
					.getTotal(), event.getData(), event.getStorageIndex()),
					immutablemanager);
		}
	};

	/** The handle get cap store. */
	Handler<GetCapStore> handleGetCapStore = new Handler<GetCapStore>() {

		@Override
		public void handle(GetCapStore event) {

			logger.info("get my cap store");
			int id = self.getPeerAddress().getId();
			String dest = id == 0 ? "" : "?dest=" + id;
			String data = capStore.dumptoHtml(dest);
			trigger(new GetCapStoreResponse(event, data), immutablemanager);
		}

	};

	/**
	 * Creates the and start downloader.
	 * 
	 * @param storageIndex
	 *            the storage index
	 * @param request
	 *            the request
	 */
	private void createAndStartDownloader(String storageIndex, Download request) {
		CHKFileURI uri = request.getUriCap();
		Component downloader = create(NsDownloader.class);

		connect(downloader.required(Network.class), network);
		connect(downloader.required(CFailureDetector.class), cfd);
		connect(downloader.required(Redundancy.class), redundancy);
		connect(downloader.required(AvailablePeers.class), peers);

		subscribe(handleDownloadDone, downloader.provided(Downloader.class));
		subscribe(handleDownloadResponse, downloader.provided(Downloader.class));

		putOperation(storageIndex, downloader);

		trigger(new NsDownloaderInit(self, uri), downloader.getControl());
		trigger(new Start(), downloader.getControl());
	}

	/**
	 * Creates the and start uploader.
	 * 
	 * @param storageIndex
	 *            the storage index
	 * @param uploadable
	 *            the uploadable
	 * @param secretholder
	 *            the secretholder
	 */
	private void createAndStartUploader(String storageIndex,
			IUploadable uploadable, SecretHolder secretholder) {
		Component uploader = create(NsUploader.class);

		connect(uploader.required(Network.class), network);
		connect(uploader.required(CFailureDetector.class), cfd);
		connect(uploader.required(Redundancy.class), redundancy);
		connect(uploader.required(AvailablePeers.class), peers);

		subscribe(handleUploadingDone, uploader.provided(Uploader.class));

		putOperation(storageIndex, uploader);

		trigger(new NsUploaderInit(self, uploadable, secretholder),
				uploader.getControl());
		trigger(new Start(), uploader.getControl());
	}

	/**
	 * Destroy downloader.
	 * 
	 * @param storageIndex
	 *            the storage index
	 */
	private void destroyDownloader(String storageIndex) {
		Component downloader = getOperation(storageIndex);

		if (downloader == null) {
			logger.debug(
					"UNUSUAL: trying to destroy unexisted downloader for storage index {}",
					storageIndex);
			return;
		}

		disconnect(downloader.required(Network.class), network);
		disconnect(downloader.required(CFailureDetector.class), cfd);
		disconnect(downloader.required(Redundancy.class), redundancy);
		disconnect(downloader.required(AvailablePeers.class), peers);

		destroy(downloader);

		logger.info("Downloader ({}) Destroyed", storageIndex);

	}

	/**
	 * Destroy uploader.
	 * 
	 * @param storageIndex
	 *            the storage index
	 */
	private void destroyUploader(String storageIndex) {
		Component uploader = getOperation(storageIndex);

		if (uploader == null) {
			logger.debug(
					"UNUSUAL: trying to destroy unexisted uploader for storage index {}",
					storageIndex);
			return;
		}

		disconnect(uploader.required(Network.class), network);
		disconnect(uploader.required(CFailureDetector.class), cfd);
		disconnect(uploader.required(Redundancy.class), redundancy);
		disconnect(uploader.required(AvailablePeers.class), peers);

		destroy(uploader);

		logger.info("Uploader ({}) Destroyed", storageIndex);
	}

	/**
	 * Contain operation.
	 * 
	 * @param opkey
	 *            the opkey
	 * @return true, if successful
	 */
	private boolean ContainOperation(String opkey) {
		boolean contains;
		synchronized (activeOperations) {
			contains = activeOperations.containsKey(opkey);
		}
		return contains;
	}

	/**
	 * Put operation.
	 * 
	 * @param key
	 *            the key
	 * @param cmp
	 *            the cmp
	 */
	private void putOperation(String key, Component cmp) {
		synchronized (activeOperations) {
			activeOperations.put(key, cmp);
		}
	}

	/**
	 * Gets the operation.
	 * 
	 * @param key
	 *            the key
	 * @return the operation
	 */
	private Component getOperation(String key) {
		synchronized (activeOperations) {
			return activeOperations.remove(key);
		}
	}

	/**
	 * Put request.
	 * 
	 * @param key
	 *            the key
	 * @param request
	 *            the request
	 */
	private void putRequest(String key, TaggedRequest request) {
		synchronized (activeRequests) {
			activeRequests.put(key, request);
		}
	}

	/**
	 * Gets the request.
	 * 
	 * @param key
	 *            the key
	 * @return the request
	 */
	private TaggedRequest getRequest(String key) {
		synchronized (activeRequests) {
			return activeRequests.get(key);
		}
	}

	/**
	 * Removes the request.
	 * 
	 * @param key
	 *            the key
	 * @return the tagged request
	 */
	private TaggedRequest removeRequest(String key) {
		synchronized (activeRequests) {
			return activeRequests.remove(key);
		}
	}
}
