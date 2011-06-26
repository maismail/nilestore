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
package eg.nile.cis.nilestore.introducer.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.p2p.overlay.OverlayAddress;
import eg.nileu.cis.nilestore.introducer.Service;
import eg.nileu.cis.nilestore.introducer.port.Announce;
import eg.nileu.cis.nilestore.introducer.port.Publish;
import eg.nileu.cis.nilestore.introducer.port.Subscribe;

// TODO: Auto-generated Javadoc
/**
 * The Class NsIntroducerServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsIntroducerServer extends ComponentDefinition {

	/** The network. */
	Positive<Network> network = requires(Network.class);
	// Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The subscribers. */
	HashMap<Service, HashSet<Address>> subscribers;

	/** The published services. */
	HashMap<Service, HashSet<OverlayAddress>> publishedServices;

	// Logger logger = LoggerFactory.getLogger(IntroducerServer.class);
	/** The logger. */
	Logger logger = LoggerFactory.getLogger("introducer."
			+ NsIntroducerServer.class.getName());

	/** The self. */
	Address self;

	// TODO: failure handling to be added, add web interface
	/**
	 * Instantiates a new ns introducer server.
	 */
	public NsIntroducerServer() {
		subscribers = new HashMap<Service, HashSet<Address>>();
		publishedServices = new HashMap<Service, HashSet<OverlayAddress>>();

		subscribe(handleInit, control);
		subscribe(handleSubscribe, network);
		subscribe(handlePublish, network);
	}

	/** The handle init. */
	Handler<NsIntroducerServerInit> handleInit = new Handler<NsIntroducerServerInit>() {
		@Override
		public void handle(NsIntroducerServerInit init) {
			self = init.getConfiguration().getIntroducerAddress();
			logger.info("Initiated @ " + self.toString());
		}
	};

	/** The handle subscribe. */
	Handler<Subscribe> handleSubscribe = new Handler<Subscribe>() {
		@Override
		public void handle(Subscribe event) {
			Service service = event.getService();
			Address peerAddress = event.getSource();
			logger.info("{} subscribe to {}", peerAddress, service);

			synchronized (subscribers) {
				if (!subscribers.containsKey(service)) {
					subscribers.put(service, new HashSet<Address>());
				}
				subscribers.get(service).add(peerAddress);
			}
			firstAnnouncement(service, peerAddress);
		}
	};

	/** The handle publish. */
	Handler<Publish> handlePublish = new Handler<Publish>() {
		@Override
		public void handle(Publish event) {
			Service service = event.getService();
			OverlayAddress overlayAddress = event.getOverlayAddress();

			logger.info("{} publish {}", overlayAddress.getPeerAddress(),
					service);

			synchronized (publishedServices) {
				if (!publishedServices.containsKey(service)) {
					publishedServices.put(service,
							new HashSet<OverlayAddress>());
				}
				publishedServices.get(service).add(overlayAddress);
			}

			announce(service, overlayAddress);
		}
	};

	/**
	 * Announce.
	 * 
	 * @param service
	 *            the service
	 * @param publisher
	 *            the publisher
	 */
	private void announce(Service service, OverlayAddress publisher) {
		Set<Address> _subscribers = null;
		synchronized (subscribers) {
			_subscribers = subscribers.get(service);
		}
		if (_subscribers == null) {
			logger.debug("there are no subscribers for {}", service);
			return;
		}

		Set<OverlayAddress> publishers = new HashSet<OverlayAddress>();
		publishers.add(publisher);

		for (Address subscriber : _subscribers) {
			trigger(new Announce(self, subscriber, publishers), network);
		}
	}

	/**
	 * First announcement.
	 * 
	 * @param service
	 *            the service
	 * @param subscriber
	 *            the subscriber
	 */
	private void firstAnnouncement(Service service, Address subscriber) {
		Set<OverlayAddress> publishers = null;
		synchronized (publishedServices) {
			publishers = publishedServices.get(service);
		}

		if (publishers == null) {
			logger.debug("there are no publishers for {}", service);
			return;
		}
		trigger(new Announce(self, subscriber, publishers), network);
	}
}
