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
package eg.nileu.cis.nilestore.immutable.downloader;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.cryptography.AESCipher;
import eg.nileu.cis.nilestore.immutable.downloader.node.NsDownloadNode;
import eg.nileu.cis.nilestore.immutable.downloader.node.NsDownloadNodeInit;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.DownloadNode;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.DownloadingDone;
import eg.nileu.cis.nilestore.immutable.downloader.node.port.GotValidatedSegment;
import eg.nileu.cis.nilestore.immutable.downloader.port.DownloadResponse;
import eg.nileu.cis.nilestore.immutable.downloader.port.Downloader;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsDownloader.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsDownloader extends ComponentDefinition {

	/** The downloader. */
	Negative<Downloader> downloader = provides(Downloader.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The redundancy. */
	Positive<Redundancy> redundancy = requires(Redundancy.class);

	/** The peers. */
	Positive<AvailablePeers> peers = requires(AvailablePeers.class);

	/** The download node. */
	private Component downloadNode;

	/** The logger. */
	private Logger logger; // = LoggerFactory.getLogger(NsDownloader.class);

	/** The self. */
	private NilestoreAddress self;

	/** The storage index. */
	private String storageIndex;

	/** The uri. */
	private CHKFileURI uri;

	/** The decryptor. */
	private AESCipher decryptor;

	/**
	 * Instantiates a new ns downloader.
	 */
	public NsDownloader() {
		downloadNode = create(NsDownloadNode.class);

		connect(downloadNode.required(AvailablePeers.class), peers);
		connect(downloadNode.required(Network.class), network);
		connect(downloadNode.required(Redundancy.class), redundancy);
		connect(downloadNode.required(CFailureDetector.class), cfd);

		subscribe(handleInit, control);
		subscribe(handleGotValidatedSegment,
				downloadNode.provided(DownloadNode.class));
		subscribe(handleDownloadNodeDone,
				downloadNode.provided(DownloadNode.class));
	}

	/** The handle init. */
	Handler<NsDownloaderInit> handleInit = new Handler<NsDownloaderInit>() {
		@Override
		public void handle(NsDownloaderInit init) {

			self = init.getSelf();
			uri = init.getUri();
			logger = Slf4jInstantiator.getLogger(NsDownloader.class,
					self.getNickname());
			storageIndex = Base32.encode(uri.getStorageIndex());
			decryptor = new AESCipher(uri.getKey(), false);

			logger.info("(SI={}): initiated", storageIndex);

			trigger(new NsDownloadNodeInit(self, uri.getVerifyCap()),
					downloadNode.getControl());
		}
	};

	/** The handle got validated segment. */
	Handler<GotValidatedSegment> handleGotValidatedSegment = new Handler<GotValidatedSegment>() {
		@Override
		public void handle(GotValidatedSegment event) {

			logger.info(
					"(SI={}): gotValidatedSegment ({}/{}) from download node",
					new Object[] { storageIndex, event.getIndex(),
							event.getTotal() });
			byte[] ciphertext = event.getSegment();
			byte[] out = new byte[ciphertext.length];
			decryptor.update(ciphertext, 0, ciphertext.length, out, 0);
			trigger(new DownloadResponse(storageIndex, event.getIndex(),
					event.getTotal(), out), downloader);
		}
	};

	/** The handle download node done. */
	Handler<DownloadingDone> handleDownloadNodeDone = new Handler<DownloadingDone>() {
		@Override
		public void handle(DownloadingDone event) {
			logger.info("(SI={}): got downloadingdone from DownloadNode, Status={}", storageIndex, event.getStatus());
			destroyAll();
			trigger(event, downloader);
		}
	};

	/**
	 * Destroy all.
	 */
	private void destroyAll() {

		disconnect(downloadNode.required(AvailablePeers.class), peers);
		disconnect(downloadNode.required(Network.class), network);
		disconnect(downloadNode.required(Redundancy.class), redundancy);
		disconnect(downloadNode.required(CFailureDetector.class), cfd);
		destroy(downloadNode);
	}
}
