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
package eg.nileu.cis.nilestore.peer.main;

import java.io.IOException;
import java.util.HashMap;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.NetworkControl;
import se.sics.kompics.network.grizzly.GrizzlyNetwork;
import se.sics.kompics.network.grizzly.GrizzlyNetworkInit;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;
import eg.nileu.cis.nilestore.peer.ClientConfiguration;
import eg.nileu.cis.nilestore.peer.NsPeer;
import eg.nileu.cis.nilestore.peer.NsPeerInit;
import eg.nileu.cis.nilestore.webapp.servlets.DownloadServlet;
import eg.nileu.cis.nilestore.webapp.servlets.UploadServlet;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServer;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerConfiguration;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerInit;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;
import eg.nileu.cis.nilestore.webserver.servlets.OperationalServlet;
import eg.nileu.cis.nilestore.webserver.servlets.ResourceServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class NsPeerMain.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsPeerMain extends ComponentDefinition {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsPeerMain.class);

	/**
	 * Instantiates a new ns peer main.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public NsPeerMain() throws IOException {
		final String netCmp = System.getProperty("nilestore.net.cmp");
		
		Component network = netCmp.equals("netty") ? create(NettyNetwork.class)
				: netCmp.equals("grizzly") ? create(GrizzlyNetwork.class)
						: create(MinaNetwork.class);

		Component timer = create(JavaTimer.class);
		Component webServer = create(NsWebServer.class);
		Component nilestorePeer = create(NsPeer.class);

		// final BootstrapConfiguration bootConfiguration =
		// BootstrapConfiguration.load(System.getProperty("bootstrap.configuration"));
		final IntroducerConfiguration introducerConfiguration = IntroducerConfiguration
				.load(System.getProperty("introducer.configuration"));

		final NsWebServerConfiguration webConfiguration = NsWebServerConfiguration
				.load(System.getProperty("jetty.web.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration
				.load(System.getProperty("network.configuration"));
		final ClientConfiguration clientConfiguration = ClientConfiguration
				.load(System.getProperty("client.configuration"));
		final PingFailureDetectorConfiguration pingConfiguration = PingFailureDetectorConfiguration
				.load(System.getProperty("ping.fd.configuration"));
		final NilestoreMonitorConfiguration monitorConfiguration = NilestoreMonitorConfiguration
				.load(System.getProperty("monitor.configuration"));

		connect(nilestorePeer.required(Network.class),
				network.provided(Network.class));
		connect(nilestorePeer.required(NetworkControl.class),
				network.provided(NetworkControl.class));
		connect(nilestorePeer.required(Timer.class),
				timer.provided(Timer.class));
		connect(nilestorePeer.provided(Web.class),
				webServer.required(Web.class));

		final int cl = Integer.valueOf(System.getProperty("nilestore.net.cl"));
		logger.info("initiating {}Network with Address={},compressionLevel={}",
				new Object[] { netCmp, networkConfiguration.getAddress(), cl });

		if (netCmp.equals("netty")) {
			trigger(new NettyNetworkInit(networkConfiguration.getAddress(), 5,
					cl), network.getControl());
		} else if (netCmp.equals("grizzly")) {
			//initialBuffer = blocksize + other message objects (almost 600 bytes) + class serialization overhead 
			int initialBufferSize = (int) (clientConfiguration.getEncodingParam().getSegmentSize() / clientConfiguration.getEncodingParam().getK()) + 1024;
			int maxBuffer = 1024*1024;
			logger.info("initiating kryoSerialization with initialBuffer={}, maxBuffer={}",initialBufferSize,maxBuffer);
			trigger(new GrizzlyNetworkInit(networkConfiguration.getAddress(),
					5, cl,initialBufferSize, maxBuffer), network.getControl());
		} else {
			trigger(new MinaNetworkInit(networkConfiguration.getAddress(), 5,
					networkConfiguration.getAddress().getPort() + 1, cl),
					network.getControl());
		}
		NsWebServerInit webServerInit = createWebServer(webConfiguration);
		trigger(webServerInit, webServer.getControl());

		trigger(new NsPeerInit(networkConfiguration.getAddress(),
				webConfiguration.getPort(), clientConfiguration, null,
				introducerConfiguration, monitorConfiguration,
				pingConfiguration), nilestorePeer.getControl());

		trigger(new Start(), nilestorePeer.getControl());
		logger.info("peer initiated @ {}", networkConfiguration.getAddress());
	}

	/**
	 * Creates the web server.
	 * 
	 * @param webConfig
	 *            the web config
	 * @return the ns web server init
	 */
	private NsWebServerInit createWebServer(NsWebServerConfiguration webConfig) {
		HashMap<String, AbstractServlet> servlets = new HashMap<String, AbstractServlet>();

		Context servletContext = new Context();
		servletContext.setContextPath("/");

		AbstractServlet upload = new UploadServlet("upload");
		servlets.put("upload", upload);
		servletContext.addServlet(new ServletHolder(upload), "/upload");

		AbstractServlet download = new DownloadServlet("download");
		servlets.put("download", download);
		servletContext.addServlet(new ServletHolder(download), "/download/*");

		AbstractServlet home = new OperationalServlet("/", "/peer/peer.html",
				Integer.MAX_VALUE);
		servlets.put("/", home);
		servletContext.addServlet(new ServletHolder(home), "/");

		ResourceServlet resource = new ResourceServlet("/peer/peer.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/peer.js");

		ResourceServlet fav = new ResourceServlet("/favicon.ico",
				"image/x-icon");
		servletContext.addServlet(new ServletHolder(fav), "/favicon.ico");

		/*
		 * Lib files
		 */

		resource = new ResourceServlet("/lib/js/jquery-1.5.1.min.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/jquery.js");

		resource = new ResourceServlet(
				"/lib/js/jquery-ui-1.8.11.custom.min.js", "text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/jquery-ui.js");

		resource = new ResourceServlet("/lib/css/jquery-ui-1.8.11.custom.css",
				"text/css");
		servletContext
				.addServlet(new ServletHolder(resource), "/jquery-ui.css");

		/*
		 * JQuery-File-Upload resource files
		 */
		resource = new ResourceServlet("/lib/js/jquery.fileupload.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.fileupload.js");

		resource = new ResourceServlet("/lib/js/jquery.fileupload-ui.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.fileupload-ui.js");

		resource = new ResourceServlet("/lib/css/jquery.fileupload-ui.css",
				"text/css");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.fileupload-ui.css");

		resource = new ResourceServlet("/lib/css/images/pbar-ani.gif",
				"image/gif");
		servletContext.addServlet(new ServletHolder(resource), "/pbar-ani.gif");

		/*
		 * 
		 */
		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_0_aaaaaa_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_flat_0_aaaaaa_40x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_0_eeeeee_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_flat_0_eeeeee_40x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_55_ffffff_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_flat_55_ffffff_40x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_75_ffffff_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_flat_75_ffffff_40x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_glass_65_ffffff_1x400.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_glass_65_ffffff_1x400.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_100_f6f6f6_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_highlight-soft_100_f6f6f6_1x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_25_0073ea_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_highlight-soft_25_0073ea_1x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_50_dddddd_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-bg_highlight-soft_50_dddddd_1x100.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-icons_0073ea_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-icons_0073ea_256x240.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-icons_454545_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-icons_454545_256x240.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-icons_666666_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-icons_666666_256x240.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-icons_ff0084_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-icons_ff0084_256x240.png");

		resource = new ResourceServlet(
				"/lib/css/images/ui-icons_ffffff_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/ui-icons_ffffff_256x240.png");

		resource = new ResourceServlet("/lib/css/table.css", "text/css");
		servletContext.addServlet(new ServletHolder(resource), "/table.css");

		resource = new ResourceServlet("/lib/css/images/bg-table-thead.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/bg-table-thead.png");

		resource = new ResourceServlet("/lib/css/images/folder.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/folder.png");

		resource = new ResourceServlet("/lib/css/images/page_white_text.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/page_white_text.png");

		resource = new ResourceServlet("/lib/js/jquery.treeTable.min.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.treeTable.js");

		resource = new ResourceServlet("/lib/css/jquery.treeTable.css",
				"text/css");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.treeTable.css");

		resource = new ResourceServlet(
				"/lib/css/images/toggle-collapse-dark.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/toggle-collapse-dark.png");

		resource = new ResourceServlet(
				"/lib/css/images/toggle-expand-dark.png", "image/png");
		servletContext.addServlet(new ServletHolder(resource),
				"/images/toggle-expand-dark.png");

		return new NsWebServerInit(webConfig, servlets, servletContext);

	}
}
