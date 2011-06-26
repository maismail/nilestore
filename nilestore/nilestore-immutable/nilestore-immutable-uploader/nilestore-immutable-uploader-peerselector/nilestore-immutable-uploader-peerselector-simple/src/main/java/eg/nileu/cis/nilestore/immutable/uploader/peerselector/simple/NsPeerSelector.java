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
package eg.nileu.cis.nilestore.immutable.uploader.peerselector.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeers;
import eg.nileu.cis.nilestore.availablepeers.centralized.port.CGetPeersResponse;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.channelfilters.ExtMessageDestinationFilter;
import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.immutable.peertracker.NsPeerTracker;
import eg.nileu.cis.nilestore.immutable.peertracker.NsPeerTrackerInit;
import eg.nileu.cis.nilestore.immutable.peertracker.port.PeerTracker;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Query;
import eg.nileu.cis.nilestore.immutable.peertracker.port.QueryResponse;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeers;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PSGetPeersResponse;
import eg.nileu.cis.nilestore.immutable.uploader.peerselector.port.PeerSelector;
import eg.nileu.cis.nilestore.utils.DumpUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerSelector.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeerSelector extends ComponentDefinition {

	/** The A aport. */
	Positive<AvailablePeers> AAport = requires(AvailablePeers.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);
	// Positive<Timer> timer = requires(Timer.class);
	/** The cfd. */
	Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The peer selectorport. */
	Negative<PeerSelector> peerSelectorport = provides(PeerSelector.class);

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsPeerSelector.class);

	/** The sharemap. */
	private final Map<Integer, ComponentAddress> sharemap;

	/** The storage clients. */
	private final ConcurrentMap<String, Component> storageClients;

	/** The laststorage id. */
	private int laststorageId;

	/** The self. */
	private NilestoreAddress self;

	/** The self id. */
	private final String selfId;

	/** The request event. */
	private PSGetPeers requestEvent;

	/** The responded clients. */
	private int respondedClients;

	/**
	 * Instantiates a new ns peer selector.
	 */
	public NsPeerSelector() {
		sharemap = new HashMap<Integer, ComponentAddress>();
		storageClients = new ConcurrentHashMap<String, Component>();

		laststorageId = 0;
		selfId = "PeerSelector";

		subscribe(handleInit, control);
		subscribe(handleGetPeersResponse, AAport);
		subscribe(handleGetPeersforSI, peerSelectorport);
	}

	/** The handle init. */
	Handler<NsPeerSelectorInit> handleInit = new Handler<NsPeerSelectorInit>() {

		@Override
		public void handle(NsPeerSelectorInit init) {

			self = init.getSelf();
		}

	};

	/** The handle get peersfor si. */
	Handler<PSGetPeers> handleGetPeersforSI = new Handler<PSGetPeers>() {

		@Override
		public void handle(PSGetPeers event) {

			requestEvent = event;
			byte[] peerSelectionIndex = Base32.decode(event.getFileInfo()
					.getStorageIndex());
			trigger(new CGetPeers(new Agent("PeerSelector", 0),
					peerSelectionIndex, event.getFileInfo().getEncodingParam()
							.getN()), AAport);
			logger.info("Asking for AvailablePeers for SI "
					+ event.getFileInfo().getStorageIndex());
		}

	};

	/** The handle get peers response. */
	Handler<CGetPeersResponse> handleGetPeersResponse = new Handler<CGetPeersResponse>() {

		@Override
		public void handle(CGetPeersResponse event) {

			int NumofPeers = event.getPeers().size();
			logger.info("GotPeers from AvailablePeers ({})", NumofPeers);

			/**
			 * Here we are distributing the shares in a simple mechanism
			 */
			FileInfo fileInfo = requestEvent.getFileInfo();

			if (NumofPeers == 0) {
				logger.warn("There are no StorageClients to upload to");
			} else {
				// TODO: here i use My simple way of distribution no serverof
				// happiness is computed

				int sharePerStorage = fileInfo.getEncodingParam().getN()
						/ NumofPeers;
				int lastsharePerStorage = fileInfo.getEncodingParam().getN()
						- (NumofPeers * sharePerStorage) + sharePerStorage;

				int i = 0;
				int c = 0;
				for (NilestoreAddress peer : event.getPeers()) {
					// int[] shareNums;
					Set<Integer> shareNums = new HashSet<Integer>();
					int len = (c == (NumofPeers - 1)) ? lastsharePerStorage
							: sharePerStorage;

					for (int sh = i; sh < i + len; sh++) {
						// shareNums[sh-i] = sh;
						shareNums.add(sh);
					}

					createAndStartStorageClient(peer, fileInfo, shareNums);

					i += len;
					c++;
				}
				respondedClients = NumofPeers;
			}
		}
	};

	/** The handle got share map. */
	Handler<QueryResponse> handleGotShareMap = new Handler<QueryResponse>() {

		@Override
		public void handle(QueryResponse event) {

			logger.info("GotshareMap");

			String SI = requestEvent.getFileInfo().getStorageIndex();

			respondedClients--;
			sharemap.putAll(event.getSharemap());

			if (event.getSharemap().size() == 0) {
				logger.info("Send Remove Peer " + event.getServerAddress());
				// trigger(new RemovePeer(event.getServerAddress()), AAport);
			}

			destroyStorageClient(event.getPeerTrackerId());

			if (respondedClients == 0) {

				logger.info("GotShareMap for SI" + SI + "  "
						+ DumpUtils.dumptolog(sharemap));

				if (sharemap.size() < requestEvent.getFileInfo()
						.getEncodingParam().getN()) {

					// TODO:
					// it means that there are some storage servers failed
					// in our case where i got all available peers we cannot do
					// nothing
					// in other cases we can ask the available peers for other
					// peers and start communicating with

				}

				if (sharemap.size() < requestEvent.getFileInfo()
						.getEncodingParam().getK()) {
					logger.error("File couldn't be placed using only "
							+ sharemap.size());
				}
				// FIXME:
				trigger(new PSGetPeersResponse(null, sharemap,
						new HashMap<Address, Set<Integer>>()), peerSelectorport);

			}

		}

	};

	/*
	 * final class StorageClientEventDestinationFilter extends
	 * ChannelFilter<Query, String> {
	 * 
	 * public StorageClientEventDestinationFilter(String destid) {
	 * super(Query.class,destid,false); }
	 * 
	 * @Override public String getValue(Query event) { return event.getDestId();
	 * } }
	 */
	/**
	 * Creates the and start storage client.
	 * 
	 * @param storageServeradd
	 *            the storage serveradd
	 * @param fileinfo
	 *            the fileinfo
	 * @param shareNums
	 *            the share nums
	 */
	private void createAndStartStorageClient(NilestoreAddress storageServeradd,
			FileInfo fileinfo, Set<Integer> shareNums) {
		Component client = create(NsPeerTracker.class);

		String clientID = String.format("%s-%s-%s", fileinfo.getStorageIndex(),
				"WSC", ++laststorageId);

		trigger(new NsPeerTrackerInit(new ComponentAddress(
				self.getPeerAddress(), clientID), new ComponentAddress(
				storageServeradd.getPeerAddress(), "StorageServer"), self,
				storageServeradd), client.getControl());

		connect(client.required(Network.class), network,
				new ExtMessageDestinationFilter(clientID));
		// connect(client.required(Timer.class), timer);
		connect(client.required(CFailureDetector.class), cfd);

		subscribe(handleGotShareMap, client.provided(PeerTracker.class));

		storageClients.put(clientID, client);

		trigger(new Query(fileinfo, shareNums),
				client.provided(PeerTracker.class));

		logger.info("StorageClient ({}) created for StorageServer ({})",
				clientID, storageServeradd);
	}

	/**
	 * Destroy storage client.
	 * 
	 * @param clientID
	 *            the client id
	 */
	private void destroyStorageClient(String clientID) {
		Component client = storageClients.remove(clientID);
		if (client != null) {
			disconnect(client.required(Network.class), network);
			// disconnect(client.required(Timer.class), timer);
			disconnect(client.required(CFailureDetector.class), cfd);
			destroy(client);
		}
	}
}
