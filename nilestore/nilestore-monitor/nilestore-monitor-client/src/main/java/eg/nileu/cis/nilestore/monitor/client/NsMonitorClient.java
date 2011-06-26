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
package eg.nileu.cis.nilestore.monitor.client;

import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.monitor.port.SendStatus;
import eg.nileu.cis.nilestore.monitor.port.StorageStatusNotification;
import eg.nileu.cis.nilestore.storage.port.status.StorageServerStatus;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusRequest;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusResponse;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;

// TODO: Auto-generated Javadoc
/**
 * The Class NsMonitorClient.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsMonitorClient extends ComponentDefinition {

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The storage server status. */
	Positive<StorageServerStatus> storageServerStatus = requires(StorageServerStatus.class);

	/** The logger. */
	private Logger logger; //= LoggerFactory.getLogger(NsMonitorClient.class);

	/** The update period. */
	private long updatePeriod;

	/** The self. */
	private NilestoreAddress self;

	/** The monitor server address. */
	private Address monitorServerAddress;

	/** The protocol. */
	private Transport protocol;

	/**
	 * Instantiates a new ns monitor client.
	 */
	public NsMonitorClient() {
		subscribe(handleInit, control);
		subscribe(handleStart, control);

		subscribe(handleSendStatus, timer);
		subscribe(handleGotStorageStatus, storageServerStatus);
	}

	/** The handle init. */
	Handler<NsMonitorClientInit> handleInit = new Handler<NsMonitorClientInit>() {

		@Override
		public void handle(NsMonitorClientInit init) {

			self = init.getSelf();
			logger = Slf4jInstantiator.getLogger(NsMonitorClient.class, self.getNickname());
			updatePeriod = init.getConfig().getClientUpdatePeriod();
			monitorServerAddress = init.getConfig().getMonitorServerAddress();
			protocol = init.getConfig().getProtocol();
			logger.info("initiated updatePeriod={}, monitorServerAddress={}",updatePeriod,monitorServerAddress);
		}
	};

	/** The handle start. */
	Handler<Start> handleStart = new Handler<Start>() {

		@Override
		public void handle(Start event) {

			SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(
					updatePeriod, updatePeriod);
			spt.setTimeoutEvent(new SendStatus(spt));
			
			trigger(spt, timer);
		}
	};

	/** The handle send status. */
	Handler<SendStatus> handleSendStatus = new Handler<SendStatus>() {
		@Override
		public void handle(SendStatus event) {

			logger.debug("get storage status then send to the monitor server");
			
			StorageStatusRequest req = new StorageStatusRequest();
			trigger(req, storageServerStatus);
		}

	};

	/** The handle got storage status. */
	Handler<StorageStatusResponse> handleGotStorageStatus = new Handler<StorageStatusResponse>() {

		@Override
		public void handle(StorageStatusResponse event) {

			logger.debug("got storage server status");
			trigger(new StorageStatusNotification(self.getPeerAddress(),
					monitorServerAddress, protocol, self, event.getStatus()),
					network);
		}
	};
}
