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
package eg.nileu.cis.nilestore.monitor.server;

import java.io.IOException;
import java.util.HashMap;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.grizzly.GrizzlyNetwork;
import se.sics.kompics.network.grizzly.GrizzlyNetworkInit;
import se.sics.kompics.network.mina.MinaNetwork;
import se.sics.kompics.network.mina.MinaNetworkInit;
import se.sics.kompics.network.netty.NettyNetwork;
import se.sics.kompics.network.netty.NettyNetworkInit;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServer;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerConfiguration;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerInit;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;
import eg.nileu.cis.nilestore.webserver.servlets.ResourceServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class NsMonitorMain.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsMonitorMain extends ComponentDefinition {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(NsMonitorMain.class);

	/**
	 * Instantiates a new ns monitor main.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public NsMonitorMain() throws IOException {
		final String netCmp = System.getProperty("nilestore.net.cmp");
		Component network = netCmp.equals("netty") ? create(NettyNetwork.class)
				: netCmp.equals("grizzly") ? create(GrizzlyNetwork.class)
						: create(MinaNetwork.class);

		Component timer = create(JavaTimer.class);
		Component webServer = create(NsWebServer.class);
		Component monitor = create(NsMonitorServer.class);

		final NsWebServerConfiguration webConfiguration = NsWebServerConfiguration
				.load(System.getProperty("jetty.web.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration
				.load(System.getProperty("network.configuration"));

		connect(monitor.required(Network.class),
				network.provided(Network.class));
		connect(monitor.required(Timer.class), timer.provided(Timer.class));
		connect(monitor.provided(Web.class), webServer.required(Web.class));

		final int cl = Integer.valueOf(System.getProperty("nilestore.net.cl"));

		logger.info("initiating {}Network with Address={},compressionLevel={}",
				new Object[] { netCmp, networkConfiguration.getAddress(), cl });

		if (netCmp.equals("netty")) {
			trigger(new NettyNetworkInit(networkConfiguration.getAddress(), 5,
					cl), network.getControl());
		} else if (netCmp.equals("grizzly")) {
			trigger(new GrizzlyNetworkInit(networkConfiguration.getAddress(),
					5, cl), network.getControl());
		} else {
			trigger(new MinaNetworkInit(networkConfiguration.getAddress(), 5,
					networkConfiguration.getAddress().getPort() + 1, cl),
					network.getControl());
		}

		NsWebServerInit webServerInit = createWebServer(webConfiguration);
		trigger(webServerInit, webServer.getControl());

		trigger(new NsMonitorServerInit(), monitor.getControl());

		logger.info("monitor initiated @ {}", networkConfiguration.getAddress());

	}

	/**
	 * Creates the web server.
	 * 
	 * @param config
	 *            the config
	 * @return the ns web server init
	 */
	private NsWebServerInit createWebServer(NsWebServerConfiguration config) {
		HashMap<String, AbstractServlet> servlets = new HashMap<String, AbstractServlet>();

		Context servletContext = new Context();
		servletContext.setContextPath("/");

		AbstractServlet monitor = new MonitorServlet("/");
		servlets.put("/", monitor);
		servletContext.addServlet(new ServletHolder(monitor), "/");

		/*
		 * ResourceServlet r = new ResourceServlet("/monitor/grview.html",
		 * "text/html"); servletContext.addServlet(new ServletHolder(r),
		 * "/grview");
		 * 
		 * ResourceServlet r0 = new ResourceServlet("/monitor/currview.html",
		 * "text/html"); servletContext.addServlet(new ServletHolder(r0),
		 * "/currview");
		 */

		ResourceServlet fav = new ResourceServlet("/favicon.ico",
				"image/x-icon");
		servletContext.addServlet(new ServletHolder(fav), "/favicon.ico");

		ResourceServlet r0 = new ResourceServlet("/monitor/monitor.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(r0), "/monitor.js");

		ResourceServlet r1 = new ResourceServlet("/lib/js/jquery-1.5.1.min.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(r1), "/jquery.js");

		ResourceServlet r2 = new ResourceServlet("/lib/js/protovis-r3.2.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(r2), "/protovis.js");

		ResourceServlet r3 = new ResourceServlet(
				"/lib/js/jquery-ui-1.8.11.custom.min.js", "text/javascript");
		servletContext.addServlet(new ServletHolder(r3), "/jquery-ui.js");

		ResourceServlet r4 = new ResourceServlet(
				"/lib/css/jquery-ui-1.8.11.custom.css", "text/css");
		servletContext.addServlet(new ServletHolder(r4), "/jquery-ui.css");

		ResourceServlet r5 = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_0_aaaaaa_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(r5),
				"/images/ui-bg_flat_0_aaaaaa_40x100.png");

		ResourceServlet r6 = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_0_eeeeee_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(r6),
				"/images/ui-bg_flat_0_eeeeee_40x100.png");

		ResourceServlet r7 = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_55_ffffff_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(r7),
				"/images/ui-bg_flat_55_ffffff_40x100.png");

		ResourceServlet r8 = new ResourceServlet(
				"/lib/css/images/ui-bg_flat_75_ffffff_40x100.png", "image/png");
		servletContext.addServlet(new ServletHolder(r8),
				"/images/ui-bg_flat_75_ffffff_40x100.png");

		ResourceServlet r9 = new ResourceServlet(
				"/lib/css/images/ui-bg_glass_65_ffffff_1x400.png", "image/png");
		servletContext.addServlet(new ServletHolder(r9),
				"/images/ui-bg_glass_65_ffffff_1x400.png");

		ResourceServlet r10 = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_100_f6f6f6_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(r10),
				"/images/ui-bg_highlight-soft_100_f6f6f6_1x100.png");

		ResourceServlet r11 = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_25_0073ea_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(r11),
				"/images/ui-bg_highlight-soft_25_0073ea_1x100.png");

		ResourceServlet r12 = new ResourceServlet(
				"/lib/css/images/ui-bg_highlight-soft_50_dddddd_1x100.png",
				"image/png");
		servletContext.addServlet(new ServletHolder(r12),
				"/images/ui-bg_highlight-soft_50_dddddd_1x100.png");

		ResourceServlet r13 = new ResourceServlet(
				"/lib/css/images/ui-icons_0073ea_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(r13),
				"/images/ui-icons_0073ea_256x240.png");

		ResourceServlet r14 = new ResourceServlet(
				"/lib/css/images/ui-icons_454545_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(r14),
				"/images/ui-icons_454545_256x240.png");

		ResourceServlet r15 = new ResourceServlet(
				"/lib/css/images/ui-icons_666666_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(r15),
				"/images/ui-icons_666666_256x240.png");

		ResourceServlet r16 = new ResourceServlet(
				"/lib/css/images/ui-icons_ff0084_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(r16),
				"/images/ui-icons_ff0084_256x240.png");

		ResourceServlet r17 = new ResourceServlet(
				"/lib/css/images/ui-icons_ffffff_256x240.png", "image/png");
		servletContext.addServlet(new ServletHolder(r17),
				"/images/ui-icons_ffffff_256x240.png");

		return new NsWebServerInit(config, servlets, servletContext);
	}
}
