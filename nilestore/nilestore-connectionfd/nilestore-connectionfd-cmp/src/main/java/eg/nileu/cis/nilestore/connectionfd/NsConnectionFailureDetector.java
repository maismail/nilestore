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
package eg.nileu.cis.nilestore.connectionfd;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.network.NetworkSessionClosed;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.p2p.fd.PeerFailureSuspicion;
import se.sics.kompics.p2p.fd.StartProbingPeer;
import se.sics.kompics.p2p.fd.StopProbingPeer;
import se.sics.kompics.p2p.fd.SuspicionStatus;
import se.sics.kompics.p2p.fd.ping.PingFailureDetector;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorInit;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.availablepeers.port.RemovePeer;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.connectionfd.port.CancelNotifyonFailure;
import eg.nileu.cis.nilestore.connectionfd.port.ConnectionFailure;
import eg.nileu.cis.nilestore.connectionfd.port.NotifyonFailure;
import eg.nileu.cis.nilestore.connectionfd.port.RequestTimeout;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsConnectionFailureDetector.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsConnectionFailureDetector extends ComponentDefinition {

	/** The cfd. */
	Negative<CFailureDetector> cfd = provides(CFailureDetector.class);

	/** The pfd. */
	Negative<FailureDetector> pfd = provides(FailureDetector.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The netcontrol. */
	Positive<NetworkControl> netcontrol = requires(NetworkControl.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The available peers. */
	Positive<AvailablePeers> availablePeers = requires(AvailablePeers.class);

	/** The logger. */
	private Logger logger; // =LoggerFactory.getLogger(NsConnectionFailureDetector.class);

	/** The ping failure detector. */
	private Component pingFailureDetector;

	/** The RETRIES. */
	private final int RETRIES;

	/** The DEFAUL t_ delay. */
	private long DEFAULT_DELAY;

	/** The self. */
	private Address self;

	/** The active requests. */
	private HashMap<Address, List<NotifyonFailure>> activeRequests;

	/** The outstanding timers. */
	private HashMap<Address, UUID> outstandingTimers;

	/** The outstanding probers. */
	private HashMap<Address, UUID> outstandingProbers;

	// TODO: add the networkException Handler
	/**
	 * Instantiates a new ns connection failure detector.
	 */
	public NsConnectionFailureDetector() {

		RETRIES = 2;
		pingFailureDetector = create(PingFailureDetector.class);
		activeRequests = new HashMap<Address, List<NotifyonFailure>>();
		outstandingTimers = new HashMap<Address, UUID>();
		outstandingProbers = new HashMap<Address, UUID>();

		connect(pingFailureDetector.required(Network.class), network);
		connect(pingFailureDetector.required(Timer.class), timer);
		connect(pfd, pingFailureDetector.provided(FailureDetector.class));

		subscribe(handleInit, control);
		subscribe(handleNotify, cfd);
		subscribe(handleRequestTimeout, timer);
		subscribe(handlePingFDResponse,
				pingFailureDetector.provided(FailureDetector.class));
		subscribe(handleConnectionSucceeded, cfd);
		subscribe(handleConnectionClose, netcontrol);
	}

	/** The handle init. */
	Handler<NsConnectionFailureDetectorInit> handleInit = new Handler<NsConnectionFailureDetectorInit>() {
		@Override
		public void handle(NsConnectionFailureDetectorInit init) {
			DEFAULT_DELAY = init.getDelay();
			self = init.getSelf().getPeerAddress();
			logger = Slf4jInstantiator.getLogger(
					NsConnectionFailureDetector.class, init.getSelf()
							.getNickname());
			logger.info("intitiated with delay={} msec and retries={}",
					DEFAULT_DELAY, RETRIES);
			trigger(new PingFailureDetectorInit(self,
					init.getPingFDConfiguration()),
					pingFailureDetector.getControl());
		}
	};

	/** The handle notify. */
	Handler<NotifyonFailure> handleNotify = new Handler<NotifyonFailure>() {

		@Override
		public void handle(NotifyonFailure event) {

			Address peerAddress = event.getAddress();
			if (peerAddress.equals(self)) {
				logger.debug("local: no failure detection required");
				return;
			}

			List<NotifyonFailure> requests;
			synchronized (activeRequests) {
				requests = activeRequests.get(peerAddress);
			}

			if (requests != null) {
				requests.add(event);
				Collections.sort(requests);

				synchronized (activeRequests) {
					activeRequests.put(peerAddress, requests);
				}
				logger.debug(
						"got NotifyonFailure for {}, current number of requests = {}",
						peerAddress, requests.size());
				return;
			}

			requests = new ArrayList<NotifyonFailure>();
			long delay = event.getDelay() == -1 ? DEFAULT_DELAY : event
					.getDelay();
			event.setDelay(delay);
			requests.add(event);

			logger.debug("got NotifyonFailure for {}", peerAddress);

			synchronized (activeRequests) {
				activeRequests.put(peerAddress, requests);
			}
			logger.debug("activeRequests={}",
					DumpUtils.dumptolog(activeRequests));

			scheduleTimer(peerAddress, delay);
		}
	};

	/** The handle request timeout. */
	Handler<RequestTimeout> handleRequestTimeout = new Handler<RequestTimeout>() {
		@Override
		public void handle(RequestTimeout event) {

			Address peerAddress = event.getPeerAddress();

			UUID tId;
			synchronized (outstandingTimers) {
				tId = outstandingTimers.remove(peerAddress);
			}

			if (tId == null)
				return;

			StartProbingPeer spp = new StartProbingPeer(peerAddress, null,
					RETRIES);

			synchronized (outstandingProbers) {
				outstandingProbers.put(peerAddress, spp.getRequestId());
			}

			logger.debug("start ping failure detector, Retries={}", RETRIES);
			trigger(spp, pingFailureDetector.provided(FailureDetector.class));
		}
	};

	/** The handle ping fd response. */
	Handler<PeerFailureSuspicion> handlePingFDResponse = new Handler<PeerFailureSuspicion>() {
		@Override
		public void handle(PeerFailureSuspicion event) {
			Address peerAddress = event.getPeerAddress();
			UUID pId;
			synchronized (outstandingProbers) {
				pId = outstandingProbers.remove(peerAddress);
			}

			if (pId == null)
				return;

			if (event.getSuspicionStatus().equals(SuspicionStatus.ALIVE)) {
				logger.debug("{} is ALIVE", peerAddress);

				long delay = DEFAULT_DELAY;
				synchronized (activeRequests) {
					List<NotifyonFailure> requests = activeRequests
							.get(peerAddress);
					if (requests != null) {
						if (!requests.isEmpty()) {
							delay = requests.get(0).getDelay();
						}
					}
				}

				scheduleTimer(peerAddress, delay);
			} else {
				connectionFailed(peerAddress);
			}
		}
	};

	/** The handle connection succeeded. */
	Handler<CancelNotifyonFailure> handleConnectionSucceeded = new Handler<CancelNotifyonFailure>() {
		@Override
		public void handle(CancelNotifyonFailure event) {
			NotifyonFailure req = event.getRequest();
			Address peerAddress = req.getAddress();
			logger.debug("got cancel NotifyonFailure for {}", peerAddress);

			List<NotifyonFailure> requests;

			synchronized (activeRequests) {
				requests = activeRequests.get(peerAddress);
			}

			if (requests == null) {
				logger.debug("{} has no active requests", peerAddress);
				return;
			}

			if (!requests.remove(req)) {
				logger.debug("UNUSUAL: request doesn't exits");
				return;
			}

			if (requests.isEmpty()) {
				synchronized (activeRequests) {
					activeRequests.remove(peerAddress);
				}
				cancelTimer(peerAddress);
			}
			logger.debug("activeRequests={}",
					DumpUtils.dumptolog(activeRequests));
		}
	};

	/** The handle connection close. */
	Handler<NetworkSessionClosed> handleConnectionClose = new Handler<NetworkSessionClosed>() {
		@Override
		public void handle(NetworkSessionClosed event) {
			Address peerAddress = InetSocketAddr2Address(event
					.getRemoteAddress());
			logger.debug("got connection closed for {}", peerAddress);

			cancelTimer(peerAddress);
			stopProber(peerAddress);
			connectionFailed(peerAddress);
		}
	};

	/**
	 * Connection failed.
	 * 
	 * @param peerAddress
	 *            the peer address
	 */
	private void connectionFailed(Address peerAddress) {

		logger.debug("connection to {} failed", peerAddress);
		List<NotifyonFailure> requests;

		synchronized (activeRequests) {
			requests = activeRequests.remove(peerAddress);
		}

		if (requests == null) {
			logger.debug("{} has no requests", peerAddress);
			return;
		}

		logger.debug("trigger remove peer on the available peers port");
		trigger(new RemovePeer(peerAddress), availablePeers);

		for (NotifyonFailure req : requests) {
			trigger(new ConnectionFailure(req), cfd);
		}
	}

	/**
	 * Schedule timer.
	 * 
	 * @param peerAddress
	 *            the peer address
	 * @param delay
	 *            the delay
	 */
	private void scheduleTimer(Address peerAddress, long delay) {

		ScheduleTimeout st = new ScheduleTimeout(delay);
		st.setTimeoutEvent(new RequestTimeout(st, peerAddress));

		logger.debug("schedule RequestTimeout for {} ", peerAddress);

		synchronized (outstandingTimers) {
			outstandingTimers.put(peerAddress, st.getTimeoutEvent()
					.getTimeoutId());
		}
		trigger(st, timer);
	}

	/**
	 * Cancel timer.
	 * 
	 * @param peerAddress
	 *            the peer address
	 */
	private void cancelTimer(Address peerAddress) {

		UUID tId;
		synchronized (outstandingTimers) {
			tId = outstandingTimers.remove(peerAddress);
		}

		if (tId == null)
			return;

		logger.debug("trigger cancel timer for {}", peerAddress);
		CancelTimeout ct = new CancelTimeout(tId);
		trigger(ct, timer);
	}

	/**
	 * Stop prober.
	 * 
	 * @param peerAddress
	 *            the peer address
	 */
	private void stopProber(Address peerAddress) {
		UUID pId;
		synchronized (outstandingProbers) {
			pId = outstandingProbers.remove(peerAddress);
		}

		if (pId == null)
			return;

		logger.debug("trigger stop peer probing for {}", peerAddress);
		StopProbingPeer sp = new StopProbingPeer(peerAddress, pId);
		trigger(sp, pingFailureDetector.provided(FailureDetector.class));
	}

	/**
	 * Inet socket addr2 address.
	 * 
	 * @param address
	 *            the address
	 * @return the address
	 */
	private Address InetSocketAddr2Address(InetSocketAddress address) {
		// In Nilestore all addresses are with id=0 except in the case of the
		// simulator
		return new Address(address.getAddress(), address.getPort(), 0);
	}
}
