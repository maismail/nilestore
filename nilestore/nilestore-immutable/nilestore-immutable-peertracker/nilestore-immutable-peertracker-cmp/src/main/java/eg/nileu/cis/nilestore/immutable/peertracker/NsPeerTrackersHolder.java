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
package eg.nileu.cis.nilestore.immutable.peertracker;

import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.channelfilters.ExtMessageDestinationFilter;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.peertracker.port.Abort;
import eg.nileu.cis.nilestore.immutable.peertracker.port.GetSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.HaveSharesResponse;
import eg.nileu.cis.nilestore.immutable.peertracker.port.PeerTracker;
import eg.nileu.cis.nilestore.immutable.peertracker.port.QueryResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerTrackersHolder.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeerTrackersHolder extends ComponentDefinition {

	/**
	 * The Class PeerTrackerEntry.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	protected class PeerTrackerEntry {

		/** The peer tracker id. */
		private final String peerTrackerId;

		/** The peer tracker. */
		private final Component peerTracker;

		/**
		 * Instantiates a new peer tracker entry.
		 * 
		 * @param peerTrackerId
		 *            the peer tracker id
		 * @param peerTracker
		 *            the peer tracker
		 */
		public PeerTrackerEntry(String peerTrackerId, Component peerTracker) {
			this.peerTrackerId = peerTrackerId;
			this.peerTracker = peerTracker;
		}

		/**
		 * Gets the peer tracker id.
		 * 
		 * @return the peer tracker id
		 */
		public String getPeerTrackerId() {
			return peerTrackerId;
		}

		/**
		 * Gets the peer tracker.
		 * 
		 * @return the peer tracker
		 */
		public Component getPeerTracker() {
			return peerTracker;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((peerTracker == null) ? 0 : peerTracker.hashCode());
			result = prime * result
					+ ((peerTrackerId == null) ? 0 : peerTrackerId.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeerTrackerEntry other = (PeerTrackerEntry) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (peerTracker == null) {
				if (other.peerTracker != null)
					return false;
			} else if (!peerTracker.equals(other.peerTracker))
				return false;
			if (peerTrackerId == null) {
				if (other.peerTrackerId != null)
					return false;
			} else if (!peerTrackerId.equals(other.peerTrackerId))
				return false;
			return true;
		}

		/**
		 * Gets the outer type.
		 * 
		 * @return the outer type
		 */
		private NsPeerTrackersHolder getOuterType() {
			return NsPeerTrackersHolder.this;
		}

	}

	/**
	 * The Class PeerTrackerAddress.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	protected class PeerTrackerAddress {

		/** The peer address. */
		private final NilestoreAddress peerAddress;

		/** The peer tracker id. */
		private final String peerTrackerId;

		/**
		 * Instantiates a new peer tracker address.
		 * 
		 * @param peerTrackerId
		 *            the peer tracker id
		 * @param peerAddress
		 *            the peer address
		 */
		public PeerTrackerAddress(String peerTrackerId,
				NilestoreAddress peerAddress) {
			this.peerTrackerId = peerTrackerId;
			this.peerAddress = peerAddress;
		}

		/**
		 * Gets the peer address.
		 * 
		 * @return the peer address
		 */
		public NilestoreAddress getPeerAddress() {
			return peerAddress;
		}

		/**
		 * Gets the peer tracker id.
		 * 
		 * @return the peer tracker id
		 */
		public String getPeerTrackerId() {
			return peerTrackerId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((peerTrackerId == null) ? 0 : peerTrackerId.hashCode());
			result = prime * result
					+ ((peerAddress == null) ? 0 : peerAddress.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeerTrackerAddress other = (PeerTrackerAddress) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (peerTrackerId == null) {
				if (other.peerTrackerId != null)
					return false;
			} else if (!peerTrackerId.equals(other.peerTrackerId))
				return false;
			if (peerAddress == null) {
				if (other.peerAddress != null)
					return false;
			} else if (!peerAddress.equals(other.peerAddress))
				return false;
			return true;
		}

		/**
		 * Gets the outer type.
		 * 
		 * @return the outer type
		 */
		private NsPeerTrackersHolder getOuterType() {
			return NsPeerTrackersHolder.this;
		}

	}

	/** The available peers. */
	protected Positive<AvailablePeers> availablePeers = requires(AvailablePeers.class);

	/** The network. */
	protected Positive<Network> network = requires(Network.class);

	/** The cfd. */
	protected Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The logger. */
	Logger logger = LoggerFactory.getLogger(NsPeerTrackersHolder.class);

	/** The peer trackers. */
	private HashMap<String, Component> peerTrackers;

	/** The last peer tracker id. */
	private int lastPeerTrackerId;

	/**
	 * Instantiates a new ns peer trackers holder.
	 */
	public NsPeerTrackersHolder() {
		this.peerTrackers = new HashMap<String, Component>();
		this.lastPeerTrackerId = 0;
	}

	// TODO:
	/**
	 * Creates the and start peer tracker.
	 * 
	 * @param handleQueryResponse
	 *            the handle query response
	 * @param handleHaveSharesResponse
	 *            the handle have shares response
	 * @param handleGetSharesResponse
	 *            the handle get shares response
	 * @param storageIndex
	 *            the storage index
	 * @param self
	 *            the self
	 * @param storageServeradd
	 *            the storage serveradd
	 * @return the peer tracker entry
	 */
	protected PeerTrackerEntry createAndStartPeerTracker(
			Handler<QueryResponse> handleQueryResponse,
			Handler<HaveSharesResponse> handleHaveSharesResponse,
			Handler<GetSharesResponse> handleGetSharesResponse,
			String storageIndex, NilestoreAddress self,
			NilestoreAddress storageServeradd) {

		Component pt = create(NsPeerTracker.class);

		String ptId = String.format("%s-%s-%s", storageIndex, "WSC",
				++lastPeerTrackerId);

		connect(pt.required(Network.class), network,
				new ExtMessageDestinationFilter(ptId));
		connect(pt.required(CFailureDetector.class), cfd);

		if (handleQueryResponse != null)
			subscribe(handleQueryResponse, pt.provided(PeerTracker.class));

		if (handleHaveSharesResponse != null)
			subscribe(handleHaveSharesResponse, pt.provided(PeerTracker.class));
		if (handleGetSharesResponse != null)
			subscribe(handleGetSharesResponse, pt.provided(PeerTracker.class));

		synchronized (peerTrackers) {
			peerTrackers.put(ptId, pt);
		}

		trigger(new NsPeerTrackerInit(new ComponentAddress(
				self.getPeerAddress(), ptId), new ComponentAddress(
				storageServeradd.getPeerAddress(), "StorageServer"), self,
				storageServeradd), pt.getControl());
		trigger(new Start(), pt.getControl());

		logger.debug("PeerTacker ({}) created for StorageServer ({})", ptId,
				storageServeradd);
		return new PeerTrackerEntry(ptId, pt);
	}

	/**
	 * Destroy peer tracker.
	 * 
	 * @param peerTrackerId
	 *            the peer tracker id
	 */
	protected void destroyPeerTracker(String peerTrackerId) {
		Component pt;
		synchronized (peerTrackers) {
			pt = peerTrackers.remove(peerTrackerId);
		}

		if (pt == null) {
			logger.debug("PeerTracker ({}) doesn't exists", peerTrackerId);
			return;
		}

		destroyPeerTracker(pt);
	}

	/**
	 * Destroy peer tracker.
	 * 
	 * @param pt
	 *            the pt
	 */
	private void destroyPeerTracker(Component pt) {
		disconnect(pt.required(Network.class), network);
		disconnect(pt.required(CFailureDetector.class), cfd);
		destroy(pt);
	}

	/**
	 * Gets the peer tracker.
	 * 
	 * @param peerTrackerId
	 *            the peer tracker id
	 * @return the peer tracker
	 */
	protected Component getPeerTracker(String peerTrackerId) {
		synchronized (peerTrackers) {
			return peerTrackers.get(peerTrackerId);
		}
	}

	/**
	 * Destroy all peer trackers.
	 */
	protected void destroyAllPeerTrackers() {
		synchronized (peerTrackers) {
			Collection<Component> cmps = peerTrackers.values();
			for (Component cmp : cmps) {
				destroyPeerTracker(cmp);
			}
			peerTrackers.clear();
		}
	}

	/**
	 * Destroy and abort all peer trackers.
	 */
	protected void destroyAndAbortAllPeerTrackers() {
		synchronized (peerTrackers) {
			Collection<Component> cmps = peerTrackers.values();
			for (Component pt : cmps) {
				trigger(new Abort(), pt.provided(PeerTracker.class));
				destroyPeerTracker(pt);
			}
			peerTrackers.clear();
		}
	}
}
