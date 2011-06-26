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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.monitor.port.StorageStatusNotification;
import eg.nileu.cis.nilestore.storage.port.status.SIStatusItem;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusView;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;
import eg.nileu.cis.nilestore.webserver.port.OperationRequest;
import eg.nileu.cis.nilestore.webserver.port.ServletRequest;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class NsMonitorServer.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsMonitorServer extends ComponentDefinition {

	/** The network. */
	Positive<Network> network = requires(Network.class);

	/** The timer. */
	Positive<Timer> timer = requires(Timer.class);

	/** The web. */
	Negative<Web> web = provides(Web.class);

	/** The logger. */
	private Logger logger; //= LoggerFactory.getLogger(NsMonitorServer.class);

	/** The evict time. */
	private long evictTime = 40000;

	/** The outstandingtimers. */
	private final HashMap<NilestoreAddress, UUID> outstandingtimers;
	// private final Map<NilestoreAddress, Map<String, Map<String,Long>>> view;
	/** The view. */
	private final Map<NilestoreAddress, StorageStatusView> view;

	// private final Map<NilestoreAddress,Map<String,Long>> grview;
	/** The deadval. */
	private Long deadval = Long.valueOf(-1);

	/** The total_size. */
	private double total_size = 0;

	/** The total_count. */
	private double total_count = 0;

	/**
	 * Instantiates a new ns monitor server.
	 */
	public NsMonitorServer() {
		outstandingtimers = new HashMap<NilestoreAddress, UUID>();
		view = new HashMap<NilestoreAddress, StorageStatusView>();

		subscribe(handleInit, control);
		subscribe(handleGotStorageStatusNotification, network);
		subscribe(handleWebRequest, web);
		subscribe(handleTimeout, timer);
	}

	/** The handle init. */
	Handler<NsMonitorServerInit> handleInit = new Handler<NsMonitorServerInit>() {

		@Override
		public void handle(NsMonitorServerInit init) {
			logger = Slf4jInstantiator.getLogger(NsMonitorServer.class, "monitor");
			logger.info("initiated");
		}
	};

	/** The handle got storage status notification. */
	Handler<StorageStatusNotification> handleGotStorageStatusNotification = new Handler<StorageStatusNotification>() {

		@Override
		public void handle(StorageStatusNotification event) {

			addStatustoView(event.getSelf(), event.getStatus());
			logger.info("got storageStatusNotification from {}", event.getSelf());
		}

	};

	/** The handle web request. */
	Handler<WebRequest> handleWebRequest = new Handler<WebRequest>() {

		@Override
		public void handle(WebRequest event) {

			ServletRequest request = event.getRequest();
			if (request instanceof OperationRequest) {
				String op = ((OperationRequest) request).getOperation();
				logger.info("got {} request from web",op);
				if (op.equals("getwholeview")) {
					String str = dumpWholeViewtoJSON();
					trigger(new WebResponse(event, str), web);
				} else if (op.equals("getgroupedview")) {
					String str = dumpGroupedViewtoJSON();
					trigger(new WebResponse(event, str), web);
				}
			}
		}

	};

	/** The handle timeout. */
	Handler<NotificationTimeout> handleTimeout = new Handler<NotificationTimeout>() {

		@Override
		public void handle(NotificationTimeout event) {

			NilestoreAddress add = event.getNilestoreAddress();
			logger.info("{} seems to be dead",add);
			if (outstandingtimers.containsKey(add)) {
				outstandingtimers.remove(add);
				StorageStatusView status = view.remove(add);
				Map<String, SIStatusItem> statusperSI = status.getStatusPerSI();
				for (String si : statusperSI.keySet()) {

					statusperSI.put(si, new SIStatusItem(deadval, deadval,
							new HashSet<Integer>()));
				}

				view.put(add, new StorageStatusView(statusperSI, 0, 0));
				logger.info(add + " timeout");

			}
		}

	};

	/**
	 * Adds the statusto view.
	 * 
	 * @param peer
	 *            the peer
	 * @param peerstatus
	 *            the peerstatus
	 */
	private void addStatustoView(NilestoreAddress peer,
			StorageStatusView peerstatus) {

		boolean equal = false;
		if (view.containsKey(peer)) {
			equal = view.get(peer).equals(peerstatus);
			if (outstandingtimers.containsKey(peer)) {
				CancelTimeout ct = new CancelTimeout(
						outstandingtimers.remove(peer));
				trigger(ct, timer);
			}

		}

		view.put(peer, peerstatus);

		if (!equal) {
			StorageStatusView status = view.get(peer);
			total_size += status.getUsedSpace();
			total_count += status.getCountofShares();
		}

		ScheduleTimeout st = new ScheduleTimeout(evictTime);
		NotificationTimeout t = new NotificationTimeout(st, peer);
		st.setTimeoutEvent(t);

		outstandingtimers.put(peer, t.getTimeoutId());

		trigger(st, timer);
	}

	/**
	 * Dump whole viewto json.
	 * 
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	private synchronized String dumpWholeViewtoJSON() {

		List<String> serversList = new ArrayList<String>();
		List<String> filesList = new ArrayList<String>();
		List<Map<String, Object>> links = new ArrayList<Map<String, Object>>();
		Map<String, String> sslinks = new HashMap<String, String>();

		for (NilestoreAddress peer : view.keySet()) {

			StorageStatusView peerstatus = view.get(peer);
			Map<String, SIStatusItem> statusperSI = peerstatus.getStatusPerSI();
			if (statusperSI.size() == 0)
				continue;

			String peerkey = peer.getNickname();

			String peerurl = String.format("http://%s:%s", peer
					.getPeerAddress().getIp(), peer.getWebPort());
			sslinks.put(peerkey, peerurl);

			serversList.add(peerkey);
			int serverIndex = serversList.indexOf(peerkey);
			int siIndex = 0;

			for (String si : statusperSI.keySet()) {

				if (!filesList.contains(si))
					filesList.add(si);
				siIndex = filesList.indexOf(si);

				SIStatusItem item = statusperSI.get(si);
				Map<String, Object> elem = new HashMap<String, Object>();

				elem.put("ss", serverIndex);
				elem.put("si", siIndex);
				Object val = item.getCount();

				if (val.equals(deadval)) {
					val = "dead";
				}

				elem.put("val", val);

				if (!links.contains(elem))
					links.add(elem);
			}
		}

		JSONObject obj = new JSONObject();
		obj.put("ss", serversList);
		obj.put("si", filesList);
		obj.put("links", links);
		obj.put("sslinks", sslinks);

		return obj.toJSONString();
	}

	/**
	 * Dump grouped viewto json.
	 * 
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	private synchronized String dumpGroupedViewtoJSON() {
		List<List<Double>> data = new ArrayList<List<Double>>();
		List<String> nodes = new ArrayList<String>();
		Map<String, String> sslinks = new HashMap<String, String>();

		List<String> legend = new ArrayList<String>();
		legend.add("Used Space");
		legend.add("Count of Shares");
		//

		for (NilestoreAddress peer : view.keySet()) {
			StorageStatusView peerstatus = view.get(peer);
			// if(peerstatus.getUsedSpace() == 0 ||
			// peerstatus.getCountofShares() == 0)
			// continue;

			double size = peerstatus.getUsedSpace() / total_size;
			double number = peerstatus.getCountofShares() / total_count;

			String peerkey = peer.getNickname();
			String peerurl = String.format("http://%s:%s", peer
					.getPeerAddress().getIp().getHostAddress(),
					peer.getWebPort());

			List<Double> row = new ArrayList<Double>();
			row.add(size);
			row.add(number);

			data.add(row);
			nodes.add(peerkey);
			sslinks.put(peerkey, peerurl);
		}

		JSONObject obj = new JSONObject();
		obj.put("data", data);
		obj.put("nodes", nodes);
		obj.put("sslinks", sslinks);
		obj.put("legend", legend);

		return obj.toJSONString();
	}

}
