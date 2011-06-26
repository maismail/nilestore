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
package eg.nileu.cis.nilestore.peer;

import java.io.IOException;
import java.util.Random;

import org.bitpedia.util.Base32;
import org.slf4j.Logger;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.p2p.fd.FailureDetector;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.SecretHolder;
import eg.nileu.cis.nilestore.availablepeers.centralized.NsCentralizedAvailablePeers;
import eg.nileu.cis.nilestore.availablepeers.centralized.NsCentralizedAvailablePeersInit;
import eg.nileu.cis.nilestore.availablepeers.port.AvailablePeers;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.NsConnectionFailureDetector;
import eg.nileu.cis.nilestore.connectionfd.NsConnectionFailureDetectorInit;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.manager.NsImmutableManager;
import eg.nileu.cis.nilestore.immutable.manager.NsImmutableManagerInit;
import eg.nileu.cis.nilestore.immutable.manager.port.Immutable;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;
import eg.nileu.cis.nilestore.monitor.client.NsMonitorClient;
import eg.nileu.cis.nilestore.monitor.client.NsMonitorClientInit;
import eg.nileu.cis.nilestore.peer.port.NileStorePeer;
import eg.nileu.cis.nilestore.redundancy.onion.NsReedSolomonCodes;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.storage.port.status.StorageServerStatus;
import eg.nileu.cis.nilestore.storage.server.NsStorageServer;
import eg.nileu.cis.nilestore.storage.server.NsStorageServerConfiguration;
import eg.nileu.cis.nilestore.storage.server.NsStorageServerInit;
import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;
import eg.nileu.cis.nilestore.webapp.NsWebApplication;
import eg.nileu.cis.nilestore.webapp.NsWebApplicationInit;
import eg.nileu.cis.nilestore.webserver.port.Web;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeer extends ComponentDefinition {

	/** The nilestore port. */
	Negative<NileStorePeer> nilestorePort = provides(NileStorePeer.class);

	/** The web. */
	Negative<Web> web = provides(Web.class);

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The netcontrol. */
	Positive<NetworkControl> netcontrol = requires(NetworkControl.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The logger. */
	private static Logger logger;

	/** The self. */
	private NilestoreAddress self;

	/** The client configuration. */
	private ClientConfiguration clientConfiguration;

	/** The peerid. */
	private String peerid;

	/** The secret holder. */
	private SecretHolder secretHolder;
	// private KeyGenerator keyGenerator;

	/** The connection fd. */
	private Component storageServer, immutableManager, replication,
			availablePeers, webApp, monitorClient, connectionFD;

	/**
	 * Instantiates a new ns peer.
	 */
	public NsPeer() {
		immutableManager = create(NsImmutableManager.class);
		replication = create(NsReedSolomonCodes.class);
		availablePeers = create(NsCentralizedAvailablePeers.class);
		webApp = create(NsWebApplication.class);

		connectionFD = create(NsConnectionFailureDetector.class);

		connect(connectionFD.required(Timer.class), timer);
		connect(connectionFD.required(NetworkControl.class), netcontrol);
		connect(connectionFD.required(Network.class), network);

		connect(availablePeers.required(Timer.class), timer);
		connect(availablePeers.required(Network.class), network);
		connect(availablePeers.required(FailureDetector.class),
				connectionFD.provided(FailureDetector.class));
		connect(connectionFD.required(AvailablePeers.class),
				availablePeers.provided(AvailablePeers.class));

		connect(immutableManager.required(Network.class), network);
		connect(immutableManager.required(CFailureDetector.class),
				connectionFD.provided(CFailureDetector.class));
		connect(immutableManager.required(Redundancy.class),
				replication.provided(Redundancy.class));
		connect(immutableManager.required(AvailablePeers.class),
				availablePeers.provided(AvailablePeers.class));

		connect(webApp.provided(Web.class), web);
		connect(webApp.required(Immutable.class),
				immutableManager.provided(Immutable.class));

		subscribe(handleInit, control);

	}

	/** The handle init. */
	Handler<NsPeerInit> handleInit = new Handler<NsPeerInit>() {

		@Override
		public void handle(NsPeerInit init) {

			clientConfiguration = init.getClientConfig();
			logger = Slf4jInstantiator.getLogger(NsPeer.class,
					clientConfiguration.getClientNickname());

			logger.info("{} Started", clientConfiguration.getClientNickname());

			String convergence_secret = get_or_create_private_config("convergence");
			String lease_secret = get_or_create_private_config("secret");
			peerid = get_or_create_private_config("my_nodeid");

			long availableSpace = clientConfiguration.isStorageserver() ? FileUtils
					.getFreeSpace(clientConfiguration.gethomeDir()) : 0;
			self = new NilestoreAddress(init.getSelf(), init.getWebPort(),
					clientConfiguration.getClientNickname(), peerid,
					availableSpace);
			saveNodeUrl();

			secretHolder = new SecretHolder(Base32.decode(convergence_secret),
					Base32.decode(lease_secret));
			// keyGenerator = new KeyGenerator();

			if (clientConfiguration.isStorageserver()) {
				storageServer = create(NsStorageServer.class);
				trigger(new NsStorageServerInit(
						new NsStorageServerConfiguration(self,
								clientConfiguration.gethomeDir())),
						storageServer.getControl());

				connect(storageServer.required(Network.class), network);
				connect(storageServer.required(CFailureDetector.class),
						connectionFD.provided(CFailureDetector.class));
				connect(webApp.required(StorageServerStatus.class),
						storageServer.provided(StorageServerStatus.class));
			}

			NilestoreMonitorConfiguration monitorConfiguration = init
					.getMonitorConfig();

			if (monitorConfiguration.isEnabled()
					&& clientConfiguration.isStorageserver()) {
				monitorClient = create(NsMonitorClient.class);

				connect(monitorClient.required(Network.class), network);
				connect(monitorClient.required(Timer.class), timer);
				connect(monitorClient.required(StorageServerStatus.class),
						storageServer.provided(StorageServerStatus.class));

				trigger(new NsMonitorClientInit(monitorConfiguration, self),
						monitorClient.getControl());
				trigger(new Start(), monitorClient.getControl());
			}

			trigger(new NsCentralizedAvailablePeersInit(self,
					clientConfiguration.isStorageserver(),
					init.getIntroducerConfig()), availablePeers.getControl());
			trigger(new NsImmutableManagerInit(self, secretHolder,
					clientConfiguration.gethomeDir(),
					clientConfiguration.getEncodingParam()),
					immutableManager.getControl());
			trigger(new NsWebApplicationInit(self), webApp.getControl());
			trigger(new NsConnectionFailureDetectorInit(self, 10000,
					init.getPingConfig()), connectionFD.getControl());
		}
	};

	/**
	 * Gets the _or_create_private_config.
	 * 
	 * @param name
	 *            the name
	 * @return the _or_create_private_config
	 */
	private String get_or_create_private_config(String name) {
		String privatepath = name == "my_nodeid" ? clientConfiguration
				.gethomeDir() : FileUtils.JoinPath(
				clientConfiguration.gethomeDir(), "private");
		FileUtils.mkdirsifnotExists(privatepath);

		String filepath = FileUtils.JoinPath(privatepath, name);
		String line = null;
		if (FileUtils.exists(filepath)) {
			try {
				line = FileUtils.readLine(filepath);
			} catch (IOException e) {
				logger.error("Exception while reading configuartion", e);
			}
		} else {
			Random random = new Random();
			int size = 32;
			if (name == "my_nodeid") {
				size = 20;
			}
			byte[] d = new byte[size];
			random.nextBytes(d);
			line = Base32.encode(d);

			try {
				FileUtils.writeLine(line, filepath);
			} catch (IOException e) {
				logger.error("Exception while writing configuartion", e);
			}
		}
		return line;
	}

	/**
	 * Save node url.
	 */
	private void saveNodeUrl() {
		String path = FileUtils.JoinPath(clientConfiguration.gethomeDir(),
				"node.url");
		try {
			String nodeurl = String.format("http://%s:%d/", self
					.getPeerAddress().getIp().getHostAddress(),
					self.getWebPort());
			FileUtils.writeLine(nodeurl, path);
		} catch (IOException e) {
			logger.error("Exception while writing configuartion", e);
		}

	}
}
