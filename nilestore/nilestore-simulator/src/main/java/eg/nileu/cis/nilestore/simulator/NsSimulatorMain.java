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
package eg.nileu.cis.nilestore.simulator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import se.sics.kompics.network.model.king.KingLatencyMap;
import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;
import se.sics.kompics.p2p.orchestrator.P2pOrchestrator;
import se.sics.kompics.p2p.orchestrator.P2pOrchestratorInit;
import se.sics.kompics.timer.Timer;
import eg.nile.cis.nilestore.introducer.server.NsIntroducerServer;
import eg.nile.cis.nilestore.introducer.server.NsIntroducerServerInit;
import eg.nileu.cis.nilestore.channelfilters.MessageDestinationFilter;
import eg.nileu.cis.nilestore.channelfilters.WebRequestDestinationFilter;
import eg.nileu.cis.nilestore.introducer.IntroducerConfiguration;
import eg.nileu.cis.nilestore.monitor.NilestoreMonitorConfiguration;
import eg.nileu.cis.nilestore.monitor.server.NsMonitorServer;
import eg.nileu.cis.nilestore.monitor.server.NsMonitorServerInit;
import eg.nileu.cis.nilestore.simulator.port.NsExperiment;
import eg.nileu.cis.nilestore.webapp.servlets.DownloadServlet;
import eg.nileu.cis.nilestore.webapp.servlets.UploadServlet;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServer;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerConfiguration;
import eg.nileu.cis.nilestore.webserver.jetty.NsWebServerInit;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.servlets.AbstractServlet;
import eg.nileu.cis.nilestore.webserver.servlets.ResourceServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class NsSimulatorMain.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsSimulatorMain extends ComponentDefinition {

	// private static SimulatorScheduler simulatorScheduler = new
	// SimulatorScheduler();
	/** The scenario. */
	@SuppressWarnings("serial")
	private static SimulationScenario scenario = new SimulationScenario() {
		{

		}
	};

	/*
	 * public static void main(String[] args) {
	 * //Kompics.setScheduler(simulatorScheduler);
	 * Kompics.createAndStart(NsSimulatorMain.class, 1); }
	 */
	/**
	 * Instantiates a new ns simulator main.
	 * 
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	public NsSimulatorMain() throws UnknownHostException {

		configureLogger();

		P2pOrchestrator.setSimulationPortType(NsExperiment.class);

		Component p2pOrchestrator = create(P2pOrchestrator.class);
		Component introducer = create(NsIntroducerServer.class);
		Component simulator = create(NsSimulator.class);
		Component webServer = create(NsWebServer.class);
		Component monitor = create(NsMonitorServer.class);

		Address peer0Address = getIntroducerAddress();
		IntroducerConfiguration introducerConfiguration = new IntroducerConfiguration(
				peer0Address, 0);
		Address monitorAddress = getMonitorAddress();
		NilestoreMonitorConfiguration monitorConfiguration = new NilestoreMonitorConfiguration(
				true, monitorAddress, 5000, 0, Transport.TCP);
		NsWebServerConfiguration webConfiguration = new NsWebServerConfiguration(
				peer0Address.getIp(), 8080, 3);
		NsWebServerInit webserverinit = createWebServer(webConfiguration);

		trigger(new P2pOrchestratorInit(scenario, new KingLatencyMap()),
				p2pOrchestrator.getControl());

		String homeDir = System.getProperty("nilestore.sim.homedir");
		int webport = Integer.valueOf(System
				.getProperty("nilestore.sim.webport"));

		connect(introducer.required(Network.class),
				p2pOrchestrator.provided(Network.class),
				new MessageDestinationFilter(peer0Address));

		connect(simulator.required(Network.class),
				p2pOrchestrator.provided(Network.class));
		connect(simulator.required(Timer.class),
				p2pOrchestrator.provided(Timer.class));
		// connect(simulator.provided(NilestoreExperiment.class),
		// p2pOrchestrator.required(NilestoreExperiment.class));

		connect(webServer.required(Web.class), simulator.provided(Web.class));

		connect(monitor.required(Network.class),
				p2pOrchestrator.provided(Network.class));
		connect(monitor.required(Timer.class),
				p2pOrchestrator.provided(Timer.class));
		connect(webServer.required(Web.class), monitor.provided(Web.class),
				new WebRequestDestinationFilter(monitorAddress.getId()));

		trigger(new NsSimulatorInit(peer0Address, introducerConfiguration,
				monitorConfiguration, homeDir, webport), simulator.getControl());
		trigger(new NsIntroducerServerInit(introducerConfiguration),
				introducer.getControl());
		trigger(webserverinit, webServer.getControl());
		trigger(new NsMonitorServerInit(), monitor.getControl());

	}

	/**
	 * Gets the introducer address.
	 * 
	 * @return the introducer address
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	private Address getIntroducerAddress() throws UnknownHostException {
		return new Address(InetAddress.getByName("127.0.0.1"), 12450, -1);
	}

	/**
	 * Gets the monitor address.
	 * 
	 * @return the monitor address
	 * @throws UnknownHostException
	 *             the unknown host exception
	 */
	private Address getMonitorAddress() throws UnknownHostException {
		return new Address(InetAddress.getByName("127.0.0.1"), 12450, 0);
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

		AbstractServlet upload = new UploadServlet("upload");
		servlets.put("upload", upload);
		servletContext.addServlet(new ServletHolder(upload), "/upload");

		AbstractServlet download = new DownloadServlet("download");
		servlets.put("download", download);
		servletContext.addServlet(new ServletHolder(download), "/download/*");

		AbstractServlet sim = new NilestoreSimulatorServlet("/");
		servlets.put("/", sim);
		servletContext.addServlet(new ServletHolder(sim), "/");

		ResourceServlet resource = new ResourceServlet(
				"/simulator/simulator.js", "text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/simulator.js");

		resource = new ResourceServlet("/monitor/monitor.js", "text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/monitor.js");

		resource = new ResourceServlet("/lib/js/protovis-r3.2.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/protovis.js");

		/*
		 * Lib files
		 */

		ResourceServlet fav = new ResourceServlet("/favicon.ico",
				"image/x-icon");
		servletContext.addServlet(new ServletHolder(fav), "/favicon.ico");

		resource = new ResourceServlet("/lib/js/protovis-r3.2.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource), "/protovis.js");

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

		resource = new ResourceServlet("/lib/js/jquery.layout-1.3.0.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.layout.js");

		resource = new ResourceServlet("/lib/css/layout-default.css",
				"text/css");
		servletContext.addServlet(new ServletHolder(resource),
				"/layout-default.css");

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

		resource = new ResourceServlet("/lib/js/jquery.scrollTo-1.4.2-min.js",
				"text/javascript");
		servletContext.addServlet(new ServletHolder(resource),
				"/jquery.scrollTo.js");

		return new NsWebServerInit(config, servlets, servletContext);
	}

	private void configureLogger() {

		Properties props = new Properties();
		props.setProperty("log4j.appender.C1",
				"org.apache.log4j.ConsoleAppender");
		props.setProperty("log4j.appender.C1.layout",
				"org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.C1.layout.ConversionPattern",
				"%d{[HH:mm:ss,SSS]} %-5p {%c{1}} %m%n");

		props.setProperty("log4j.rootLogger", "INFO, C1");

		PropertyConfigurator.configure(props);
	}
}
