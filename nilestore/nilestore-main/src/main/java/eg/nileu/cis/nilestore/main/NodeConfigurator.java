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

import java.io.FileWriter;
import java.io.IOException;

import eg.nileu.cis.nilestore.utils.FileUtils;
import eg.nileu.cis.nilestore.utils.config.ConfigFile;

// TODO: Auto-generated Javadoc
/**
 * The Class NodeConfigurator.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NodeConfigurator {

	/** The nodetype. */
	private String nodetype;

	/**
	 * Instantiates a new node configurator.
	 * 
	 * @param nodeType
	 *            the node type
	 */
	public NodeConfigurator(String nodeType) {
		this.nodetype = nodeType;
	}

	/**
	 * Instantiates a new node configurator.
	 */
	public NodeConfigurator() {
		this.nodetype = "introducer";
	}

	/**
	 * Gets the node type.
	 * 
	 * @return the node type
	 */
	public String getNodeType() {
		return nodetype;
	}

	/**
	 * Creates the nodeconfig.
	 * 
	 * @param nodedir
	 *            the nodedir
	 * @param inc
	 *            the inc
	 * @return the config file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ConfigFile createNodeconfig(String nodedir, Integer inc)
			throws IOException {

		if (FileUtils.exists(nodedir)) {
			FileUtils.rmdir(nodedir, false);
		} else {
			FileUtils.mkdirs(nodedir);
		}

		return getOrCreateNodeConfig(nodedir, inc);
	}

	/**
	 * Gets the nodeconfig.
	 * 
	 * @param nodedir
	 *            the nodedir
	 * @return the nodeconfig
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ConfigFile getNodeconfig(String nodedir) throws IOException {

		if (!FileUtils.exists(nodedir)) {
			return null;
		}
		return getOrCreateNodeConfig(nodedir, 1);
	}

	/**
	 * Gets the or create node config.
	 * 
	 * @param nodedir
	 *            the nodedir
	 * @param inc
	 *            the inc
	 * @return the or create node config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private ConfigFile getOrCreateNodeConfig(String nodedir, Integer inc)
			throws IOException {
		String conffile = FileUtils.JoinPath(nodedir, "nilestore.conf");
		ConfigFile configFile = null;
		if (FileUtils.exists(conffile)) {
			configFile = new ConfigFile(conffile);
			if (configFile.hasValue("introducernode", "ip"))
				nodetype = "introducer";
			if (configFile.hasValue("introducer", "ip"))
				nodetype = "client";
			if (configFile.hasValue("monitornode", "ip"))
				nodetype = "monitor";
		} else {
			if (nodetype.equals("introducer")) {
				configFile = getIntroducerConfig(conffile);
			} else if (nodetype.equals("client")) {
				configFile = getClientConfig(conffile, inc);
			} else if (nodetype.equals("monitor")) {
				configFile = getMonitorConfig(conffile);
			}
			saveLog4jProps(nodedir);
		}

		return configFile;
	}

	/**
	 * Gets the introducer config.
	 * 
	 * @param conffile
	 *            the conffile
	 * @return the introducer config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private ConfigFile getIntroducerConfig(String conffile) throws IOException {
		ConfigFile file = new ConfigFile(conffile);
		file.setString("introducernode", "ip", "127.0.0.1");
		file.setInteger("introducernode", "networkport", 12345);
		file.setInteger("introducernode", "webport", 8081);
		file.save();

		return file;
	}

	/**
	 * Gets the monitor config.
	 * 
	 * @param conffile
	 *            the conffile
	 * @return the monitor config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private ConfigFile getMonitorConfig(String conffile) throws IOException {
		ConfigFile file = new ConfigFile(conffile);
		file.setString("monitornode", "ip", "127.0.0.1");
		file.setInteger("monitornode", "networkport", 12340);
		file.setInteger("monitornode", "webport", 8080);
		file.save();
		return file;
	}

	/**
	 * Gets the client config.
	 * 
	 * @param conffile
	 *            the conffile
	 * @param inc
	 *            the inc
	 * @return the client config
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private ConfigFile getClientConfig(String conffile, Integer inc)
			throws IOException {

		ConfigFile file = new ConfigFile(conffile);
		file.setString("introducer", "ip", "127.0.0.1");
		file.setInteger("introducer", "port", 12345);

		file.setString("node", "nickname", "node" + inc);
		file.setString("node", "ip", "127.0.0.1");
		file.setInteger("node", "networkport", 12345 + inc);
		file.setInteger("node", "webport", 8081 + inc);

		file.setBoolean("storage", "enabled", true);

		file.setBoolean("monitor", "enabled", false);
		file.setString("monitor", "ip", "127.0.0.1");
		file.setInteger("monitor", "port", 12340);
		file.setInteger("monitor", "updateperiod", 20000);

		file.setInteger("shares", "shares.k", 3);
		file.setInteger("shares", "shares.n", 10);

		file.save();

		return file;
	}

	/**
	 * Save log4j props.
	 * 
	 * @param nodedir
	 *            the nodedir
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveLog4jProps(String nodedir) throws IOException {
		String logdir = FileUtils.JoinPath(nodedir, "log");
		FileUtils.mkdirsifnotExists(logdir);

		String logconf = FileUtils.JoinPath(logdir, "log4j.props");
		String conf = getDefaultLog4jProps(logdir);
		FileWriter writer = new FileWriter(logconf);
		writer.write(conf);
		writer.close();
	}

	/**
	 * Gets the default log4j props.
	 * 
	 * @param logdir
	 *            the logdir
	 * @return the default log4j props
	 */
	private String getDefaultLog4jProps(String logdir) {

		/*
		 * Properties props = new Properties();
		 * props.setProperty("log4j.appender.C1",
		 * "org.apache.log4j.ConsoleAppender");
		 * props.setProperty("log4j.appender.C1.layout"
		 * ,"org.apache.log4j.PatternLayout");
		 * props.setProperty("log4j.appender.C1.layout.ConversionPattern"
		 * ,"%d{[HH:mm:ss,SSS]} %-5p {%c{1}} %m%n");
		 * 
		 * props.setProperty("log4j.appender.F1",
		 * "org.apache.log4j.RollingFileAppender");
		 * props.setProperty("log4j.appender.F1.file","nilestore.log");
		 * props.setProperty("log4j.appender.F1.MaxFileSize","10MB");
		 * props.setProperty
		 * ("log4j.appender.F1.layout","org.apache.log4j.PatternLayout");
		 * props.setProperty("log4j.appender.F1.layout.ConversionPattern",
		 * "%d{[HH:mm:ss,SSS]} %-5p {%c{1}} %m%n");
		 * 
		 * props.setProperty("log4j.rootLogger","INFO, C1 , F1");
		 * 
		 * props.setProperty("log4j.logger.mina","ERROR");
		 * props.setProperty("log4j.logger.org.mortbay.log","WARN");
		 * props.setProperty("log4j.logger.se.sics.kompics.timer.java","WARN");
		 * 
		 * return props;
		 */

		StringBuilder sb = new StringBuilder();
		sb.append("# C1 is a console appender");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.C1=org.apache.log4j.ConsoleAppender");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.C1.layout=org.apache.log4j.PatternLayout");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.C1.layout.ConversionPattern=%d{[HH:mm:ss,SSS]} %-5p {%c{1}} %m%n");
		sb.append(FileUtils.newLine);
		sb.append(FileUtils.newLine);

		sb.append("# F1 is a file appender");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.F1=org.apache.log4j.RollingFileAppender");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.F1.file=");
		String filepath =FileUtils.getAbsPath(FileUtils.JoinPath(logdir,
		"nilestore.log"));
		if(isWindows()){
			filepath = filepath.replace("\\", "\\\\");
		}
		sb.append(filepath);
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.F1.MaxFileSize=10MB");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.F1.layout=org.apache.log4j.PatternLayout");
		sb.append(FileUtils.newLine);
		sb.append("log4j.appender.F1.layout.ConversionPattern=%d{[HH:mm:ss,SSS]} %-5p {%c{1}} %m%n");
		sb.append(FileUtils.newLine);
		sb.append(FileUtils.newLine);

		sb.append("#root logger");
		sb.append(FileUtils.newLine);
		sb.append("log4j.rootLogger=INFO, C1 , F1");
		sb.append(FileUtils.newLine);
		sb.append(FileUtils.newLine);

		sb.append("#customization");
		sb.append(FileUtils.newLine);
		sb.append("log4j.logger.mina=ERROR");
		sb.append(FileUtils.newLine);
		sb.append("log4j.logger.org.mortbay.log=ERROR");
		sb.append(FileUtils.newLine);
		sb.append("log4j.logger.se.sics.kompics.timer.java=ERROR");
		sb.append(FileUtils.newLine);

		return sb.toString();
	}
	
	private boolean isWindows(){
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf( "win" ) >= 0); 
 
	}
}
