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
package eg.nileu.cis.nilestore.main;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.PropertyConfigurator;

import se.sics.kompics.Kompics;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import eg.nile.cis.nilestore.introducer.server.NsIntroducerMain;
import eg.nileu.cis.nilestore.monitor.server.NsMonitorMain;
import eg.nileu.cis.nilestore.peer.main.NsPeerMain;
import eg.nileu.cis.nilestore.simulator.NsSimulatorMain;
import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.config.ConfigFile;

// TODO: Auto-generated Javadoc
/**
 * The Class CLI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CLI {

	/** The threads. */
	@Parameter(names = { "-t", "--num-threads" }, description = "num of used threads for kompics")
	private Integer threads = 8;

	/** The compression level. */
	@Parameter(names = { "-cl", "--compression-level" }, description = "compression level for the compression filter attached with the network component it takes values from 0 to 9")
	private Integer compressionLevel = 9;
	//TODO: add parameter validators
	/** The network. */
	@Parameter(names = { "-net", "--network-cmp" }, description = "network component to be used. [mina or netty or grizzly]")
	private String network = "mina";

	/** The help. */
	@Parameter(names = { "-h", "--help" }, description = "help for command")
	private String help;

	/**
	 * The Class CreateIntroducerCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "create an introducer node")
	private class CreateIntroducerCommand {

		/** The nodepath. */
		@Parameter(names = { "-d", "--node-directory" }, description = "node directory")
		protected String nodepath = FileUtils.JoinPath(
				System.getProperty("user.home"), ".nilestore");

		/**
		 * Gets the node path.
		 * 
		 * @return the node path
		 */
		public String getNodePath() {
			return nodepath;
		}
	}

	/**
	 * The Class CreateMonitorCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "create a monitor node")
	private class CreateMonitorCommand extends CreateIntroducerCommand {

	}

	/**
	 * The Class CreateClientCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "create a client node")
	private class CreateClientCommand extends CreateIntroducerCommand {

		/** The increment. */
		@Parameter(names = { "-c", "--increment" }, description = "", hidden = true)
		private Integer increment=1;

		/**
		 * Gets the inc.
		 * 
		 * @return the inc
		 */
		public Integer getInc() {
			return increment;
		}
	}

	/**
	 * The Class StartCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "start a node")
	private class StartCommand extends CreateIntroducerCommand {

	}

	/**
	 * The Class StopCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "stop a node")
	private class StopCommand extends CreateIntroducerCommand {

	}

	/**
	 * The Class RunSimCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "run nilestore simulator")
	private class RunSimCommand extends CreateIntroducerCommand {

		/** The webport. */
		@Parameter(names = { "-w", "--web-port" }, description = "web port")
		private Integer webport = 8080;

		/**
		 * Instantiates a new run sim command.
		 */
		public RunSimCommand() {
			nodepath = "simulator";
		}

		/**
		 * Gets the web port.
		 * 
		 * @return the web port
		 */
		public Integer getWebPort() {
			return webport;
		}
	}

	/**
	 * The Class PutCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "upload a file into the nilestore grid")
	private class PutCommand extends CreateIntroducerCommand {

		/** The files. */
		@Parameter(description = "put <file1 file2 file3> [paths of files to be uploaded]")
		private List<String> files = new ArrayList<String>();

		/**
		 * Gets the file path.
		 * 
		 * @return the file path
		 */
		public List<String> getFilePath() {
			return files;
		}
	}

	/**
	 * The Class GetCommand.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	@Parameters(commandDescription = "download a file from the nilestore grid using its capabaility")
	private class GetCommand extends CreateIntroducerCommand {

		/** The caps. */
		@Parameter(description = "get <cap1 cap2 cap3> [paths of files to be uploaded]")
		private List<String> caps = new ArrayList<String>();

		/** The download dir. */
		@Parameter(names = { "-dd", "--download-dir" }, description = "directory to save the downloaded file to")
		private String downloadDir = System.getProperty("user.dir");

		/**
		 * Gets the download dir.
		 * 
		 * @return the download dir
		 */
		public String getDownloadDir() {
			return downloadDir;
		}

		/**
		 * Gets the caps.
		 * 
		 * @return the caps
		 */
		public List<String> getCaps() {
			return caps;
		}
	}

	/** The jc. */
	private JCommander jc;

	/** The create introducer. */
	private CreateIntroducerCommand createIntroducer;

	/** The create monitor. */
	private CreateMonitorCommand createMonitor;

	/** The create client. */
	private CreateClientCommand createClient;

	/** The start. */
	private StartCommand start;

	/** The stop. */
	private StopCommand stop;

	/** The run sim. */
	private RunSimCommand runSim;

	/** The put. */
	private PutCommand put;

	/** The get. */
	private GetCommand get;

	/**
	 * Instantiates a new cLI.
	 */
	public CLI() {

		jc = new JCommander(this);
		jc.setProgramName("Nilestore");

		createIntroducer = new CreateIntroducerCommand();
		createMonitor = new CreateMonitorCommand();
		createClient = new CreateClientCommand();

		start = new StartCommand();
		stop = new StopCommand();

		runSim = new RunSimCommand();

		put = new PutCommand();
		get = new GetCommand();

		jc.addCommand("create-introducer", createIntroducer);
		jc.addCommand("create-client", createClient);
		jc.addCommand("create-monitor", createMonitor);

		jc.addCommand("run-simulator", runSim);

		jc.addCommand("start", start);
		jc.addCommand("stop", stop);

		jc.addCommand("put", put);
		jc.addCommand("get", get);
	}

	/**
	 * Parses the.
	 * 
	 * @param args
	 *            the args
	 */
	public void parse(String[] args) {
		if (args.length == 0) {
			jc.usage();
			return;
		}

		try {
			jc.parse(args);
		} catch (ParameterException e) {
			printError("Error during parsing: " + e.getMessage());
		}

		if (help != null) {
			if (!help.isEmpty()) {
				jc.usage(help);
				return;
			}
		}

		String command = jc.getParsedCommand();

		if (command.isEmpty()) {
			jc.usage();
			return;
		}

		if (command.startsWith("create")) {
			String nodetype = "";
			String nodePath = "";
			Integer inc = 1;
			if (command.equals("create-introducer")) {
				nodetype = "introducer";
				nodePath = createIntroducer.getNodePath();
			} else if (command.equals("create-client")) {
				nodetype = "client";
				nodePath = createClient.getNodePath();
				inc = createClient.getInc();
			} else if (command.equals("create-monitor")) {
				nodetype = "monitor";
				nodePath = createMonitor.getNodePath();
			}
			NodeConfigurator nc = new NodeConfigurator(nodetype);
			try {
				nc.createNodeconfig(nodePath, inc);
			} catch (IOException e) {
				System.err.println("Error during executing command : "
						+ e.getMessage());
				System.exit(-1);
			}
			System.out.println(String.format("%s node created @ %s", nodetype,
					FileUtils.getAbsPath(nodePath)));
		} else if (command.equals("start")) {
			
			// TODO: use the process builder for starting 

			PropertyConfigurator.configureAndWatch(FileUtils.JoinPath(
					start.getNodePath(), "log", "log4j.props"));

			NodeConfigurator nc = new NodeConfigurator();
			ConfigFile file = null;
			try {
				file = nc.getNodeconfig(start.getNodePath());
			} catch (IOException e) {
				System.err.println("Error during executing command : "
						+ e.getMessage());
				System.exit(-1);
			}
			if (file == null) {
				System.out
						.println(String
								.format("you have to create the node first: %s doesn't have a nilestore.conf file",
										start.getNodePath()));
				System.exit(0);
			}
			String nodetype = nc.getNodeType();
			Configuration conf = new Configuration(file, start.getNodePath(),
					nodetype);
			try {
				conf.set();
			} catch (Exception e) {
				System.err.println("Error during setting the configuration: "
						+ e.getMessage());
				System.exit(-1);
			}
			System.setProperty("nilestore.net.cl", compressionLevel.toString());
			System.setProperty("nilestore.net.cmp", network);
			
			writePidFile(start.getNodePath());
			
			if (nodetype.equals("introducer")) {
				Kompics.createAndStart(NsIntroducerMain.class, threads);
			} else if (nodetype.equals("client")) {
				Kompics.createAndStart(NsPeerMain.class, threads);
			} else if (nodetype.equals("monitor")) {
				Kompics.createAndStart(NsMonitorMain.class, threads);
			}
		} else if (command.equals("stop")) {
			//TODO:
		} else if (command.equals("run-simulator")) {
			System.setProperty("nilestore.sim.homedir", runSim.getNodePath());
			System.setProperty("nilestore.sim.webport",
					String.valueOf(runSim.getWebPort()));
			Kompics.createAndStart(NsSimulatorMain.class, threads);
		} else if (command.equals("put")) {
			disableTheLogger();
			String nodepath = put.getNodePath();
			String nodeurl = FileUtils.JoinPath(nodepath, "node.url");
			if (FileUtils.exists(nodeurl)) {
				String url = null;
				try {
					url = FileUtils.readLine(nodeurl);
				} catch (IOException e) {
					System.err.println("Error during reading node.url: "
							+ e.getMessage());
					System.exit(-1);
				}
				List<String> files = put.getFilePath();
				for (String filepath : files) {
					if (FileUtils.exists(filepath)) {
						try {
							HttpDealer.put(filepath, url);
						} catch (ClientProtocolException e) {
							System.err.println("Error during HTTP Post: "
									+ e.getMessage());
							System.exit(-1);
						} catch (IOException e) {
							System.err.println("Error during HTTP Post: "
									+ e.getMessage());
							System.exit(-1);
						}
					} else {
						System.err.println("Error: " + filepath
								+ "doesn't exists");
						System.exit(-1);
					}
				}
			} else {
				System.err.println("Error: node.url doesn't exists in "
						+ nodepath);
				System.exit(-1);
			}
		}
		// TODO: add ls command to list the cap.store content
		// also add an option to use the filename instead of the uri cap
		// if that cap contained in the cap.store
		else if (command.equals("get")) {
			disableTheLogger();
			String nodepath = get.getNodePath();
			String nodeurl = FileUtils.JoinPath(nodepath, "node.url");
			if (FileUtils.exists(nodeurl)) {
				String url = null;
				try {
					url = FileUtils.readLine(nodeurl);
				} catch (IOException e) {
					System.err.println("Error during reading node.url: "
							+ e.getMessage());
					System.exit(-1);
				}

				List<String> caps = get.getCaps();
				String downloadDir = get.getDownloadDir();
				for (String cap : caps) {
					try {
						HttpDealer.get(url, cap, downloadDir);
					} catch (ClientProtocolException e) {
						System.err.println("Error during HTTP Get: "
								+ e.getMessage());
						System.exit(-1);
					} catch (IOException e) {
						System.err.println("Error during HTTP Get: "
								+ e.getMessage());
						System.exit(-1);
					}
				}
			} else {
				System.err.println("Error: node.url doesn't exists in "
						+ nodepath);
				System.exit(-1);
			}
		}
	}

	/**
	 * Prints the error.
	 * 
	 * @param message
	 *            the message
	 */
	private void printError(String message) {
		printError(message, null);
	}

	/**
	 * Prints the error.
	 * 
	 * @param message
	 *            the message
	 * @param command
	 *            the command
	 */
	private void printError(String message, String command) {
		System.err.println(message);
		if (command != null) {
			jc.usage(command);
		} else {
			jc.usage();
		}
		System.exit(-1);
	}

	/**
	 * Disable the logger.
	 */
	private void disableTheLogger() {
		// disable the logger
		Properties props = new Properties();
		props.setProperty("log4j.rootLogger", "OFF");
		PropertyConfigurator.configure(props);
	}
	
	private void writePidFile(String dir){
		String name=ManagementFactory.getRuntimeMXBean().getName();
		String pid = name.split("@")[0];
		try {
			FileUtils.writeLine(pid, FileUtils.JoinPath(dir, "nilestore.pid"));
		} catch (IOException e) {
			System.err.println("Error during writing nilestore.pid : "
					+ e.getMessage());
			System.exit(-1);
		}
	}
}
