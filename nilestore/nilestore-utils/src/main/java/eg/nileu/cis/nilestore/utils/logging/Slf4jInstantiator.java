package eg.nileu.cis.nilestore.utils.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jInstantiator {

	public static Logger getLogger(Class<?> clazz, String nodeId) {
		return getLogger(clazz.getName(), nodeId);
	}

	public static Logger getLogger(String name, String nodeId) {
		String loggername = name;
		if (nodeId != null) {
			loggername = nodeId + "." + loggername;
		}
		return LoggerFactory.getLogger(loggername);
	}

}
