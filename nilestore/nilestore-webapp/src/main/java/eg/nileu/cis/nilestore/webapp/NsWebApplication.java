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
package eg.nileu.cis.nilestore.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.slf4j.Logger;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.immutable.manager.port.Download;
import eg.nileu.cis.nilestore.immutable.manager.port.GetCapStore;
import eg.nileu.cis.nilestore.immutable.manager.port.GetCapStoreResponse;
import eg.nileu.cis.nilestore.immutable.manager.port.GotBlockData;
import eg.nileu.cis.nilestore.immutable.manager.port.Immutable;
import eg.nileu.cis.nilestore.immutable.manager.port.Upload;
import eg.nileu.cis.nilestore.immutable.manager.port.UploadCompleted;
import eg.nileu.cis.nilestore.interfaces.uri.IURI;
import eg.nileu.cis.nilestore.storage.port.status.SIStatusItem;
import eg.nileu.cis.nilestore.storage.port.status.StorageServerStatus;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusRequest;
import eg.nileu.cis.nilestore.storage.port.status.StorageStatusResponse;
import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.utils.logging.Slf4jInstantiator;
import eg.nileu.cis.nilestore.webapp.servlets.DownloadServletRequest;
import eg.nileu.cis.nilestore.webapp.servlets.UploadServletRequest;
import eg.nileu.cis.nilestore.webserver.port.OperationRequest;
import eg.nileu.cis.nilestore.webserver.port.ServletRequest;
import eg.nileu.cis.nilestore.webserver.port.Web;
import eg.nileu.cis.nilestore.webserver.port.WebRequest;
import eg.nileu.cis.nilestore.webserver.port.WebResponse;
import eg.nileu.cis.nilestore.webserver.servlets.WebResponseCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWebApplication.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsWebApplication extends ComponentDefinition {

	/** The web. */
	Negative<Web> web = provides(Web.class);

	/** The immutablemanger. */
	Positive<Immutable> immutablemanger = requires(Immutable.class);

	/** The storage server status. */
	Positive<StorageServerStatus> storageServerStatus = requires(StorageServerStatus.class);

	/** The logger. */
	private Logger logger;// = LoggerFactory.getLogger(NsWebApplication.class);

	/** The self. */
	private NilestoreAddress self;

	/** The active requests. */
	private HashMap<Long, WebRequest> activeRequests;

	/**
	 * Instantiates a new ns web application.
	 */
	public NsWebApplication() {
		activeRequests = new HashMap<Long, WebRequest>();
		subscribe(handleInit, control);
		subscribe(handleWebRequest, web);
		subscribe(handleGotData, immutablemanger);

		subscribe(handleUploadCompleted, immutablemanger);
		subscribe(handleGotCapStore, immutablemanger);
		subscribe(handleSSSResponse, storageServerStatus);
	}

	/** The handle init. */
	Handler<NsWebApplicationInit> handleInit = new Handler<NsWebApplicationInit>() {
		@Override
		public void handle(NsWebApplicationInit init) {
			self = init.getSelf();
			logger = Slf4jInstantiator.getLogger(NsWebApplication.class,
					self.getNickname());
			logger.info("initiated");
		}
	};

	/** The handle web request. */
	Handler<WebRequest> handleWebRequest = new Handler<WebRequest>() {

		@Override
		public void handle(WebRequest event) {
			String target = event.getTarget();
			long reqId = event.getId();

			logger.info("Handling Request {}, requestId={}", target, reqId);
			putRequest(event);
			ServletRequest request = event.getRequest();

			if (target.equals("upload")) {
				if (request instanceof UploadServletRequest) {
					String filepath = ((UploadServletRequest) request)
							.getFilePath();
					String filename = ((UploadServletRequest) request)
							.getFileName();

					logger.debug("got the file [name={},path={}]", filename,
							filepath);
					trigger(new Upload(filepath, filename, new Agent("WebApp",
							reqId)), immutablemanger);
				} else {
					logger.debug("UNUSUAL: a servlet request that associated with [upload] target must be of type UploadServletRequest");
				}
			} else if (target.equals("download")) {
				if (request instanceof DownloadServletRequest) {
					IURI cap = ((DownloadServletRequest) request).getCap();
					if (cap instanceof CHKFileURI) {
						trigger(new Download((CHKFileURI) cap, new Agent(
								"WebApp", reqId)), immutablemanger);
					}
				} else {
					logger.debug("UNUSUAL: a servlet request that associated with [download] target must be of type DownloadServletRequest");
				}

			} else if (target.equals("/")) {
				if (request instanceof OperationRequest) {
					String op = ((OperationRequest) request).getOperation();

					if (op.equals("mystore")) {
						trigger(new GetCapStore(new Agent("WebApp", reqId)),
								immutablemanger);
					} else if (op.equals("explore")) {
						trigger(new StorageStatusRequest(new Agent("WebApp",
								reqId)), storageServerStatus);
					}
				} else {
					logger.debug("UNUSUAL: a servlet request that associated with [/] target must be of type OperationRequest");
				}
			}
		}
	};

	/** The handle upload completed. */
	Handler<UploadCompleted> handleUploadCompleted = new Handler<UploadCompleted>() {

		@Override
		public void handle(UploadCompleted event) {

			long requestId = event.getRequest().getRequestAgent()
					.getRequestId();
			logger.debug("got uploadCompleted for request {}", requestId);
			WebRequest requestEvent = removeRequest(requestId);
			if (requestEvent == null) {
				logger.debug(
						"UNUSUAL: requestId ({}) doesn't have an associted WebRequest",
						requestId);
				return;
			}

			UploadServletRequest uprequest = (UploadServletRequest) requestEvent
					.getRequest();

			String data;
			if (event.getStatus().isSucceeded()) {
				String webAddress_format = "/download/%s";
				if (requestEvent.isFilterEnabled()) {
					webAddress_format += "?dest="
							+ requestEvent.getDestination();
				}
				webAddress_format += requestEvent.isFilterEnabled() ? "&save=true"
						: "?save=true";

				if (uprequest.getReturnType().equals("json")) {
					data = event.getUploadResults().dumptoJson(
							webAddress_format);
				} else {
					data = event.getUploadResults().dumptohtml(
							webAddress_format);
				}

			} else {
				data = event.getStatus().getMessage();
			}

			trigger(new WebResponse(requestEvent, data), web);
		}

	};

	/** The handle got data. */
	Handler<GotBlockData> handleGotData = new Handler<GotBlockData>() {

		@Override
		public void handle(GotBlockData event) {
			logger.info(
					"Got Block {}/{}, status={}",
					new Object[] { event.getIndex(), event.getTotal(),
							event.getStatus() });

			long requestId = event.getRequest().getRequestAgent()
					.getRequestId();
			boolean remove = event.getIndex() == event.getTotal();
			WebRequest requestEvent = remove ? removeRequest(requestId)
					: getRequest(requestId);
			if (requestEvent == null) {
				logger.debug(
						"UNUSUAL: requestId ({}) doesn't have an associted WebRequest",
						requestId);
				return;
			}

			if (event.getStatus().isSucceeded()) {
				trigger(new WebResponse(requestEvent, event.getData(),
						event.getIndex(), event.getTotal()), web);
			} else {
				trigger(new WebResponse(requestEvent, event.getStatus()
						.getMessage(), WebResponseCollector.ERRORID,
						WebResponseCollector.ERRORID), web);
			}
		}
	};

	/*
	 * private String dumpListofPeerstoHtml(List<NilestoreAddress> peers) {
	 * StringBuilder sb=new StringBuilder(); sb.append("<table border=3>");
	 * sb.append(
	 * "<tr> <th> Peer Nickname</th> <th> Peer ID </th><th> Peer IP </th><th> Explore </th></tr>"
	 * ); String row =
	 * "<tr> <td>%s</td><td>%s</td><td>%s</td> <td>%s</td></tr></br>"; for
	 * (NilestoreAddress peer : peers) { String exploreaddress =
	 * String.format("<a target='_blank' href='http://%s:%s/explore'> explore </a>"
	 * , peer.getPeerAddress().getIp(),peer.getWebPort());
	 * sb.append(String.format
	 * (row,peer.getNickname(),peer.getPeerId(),peer.getPeerAddress
	 * ().getIp(),exploreaddress)); } sb.append("</table>");
	 * 
	 * return sb.toString();
	 * 
	 * }
	 */

	/** The handle got cap store. */
	Handler<GetCapStoreResponse> handleGotCapStore = new Handler<GetCapStoreResponse>() {

		@Override
		public void handle(GetCapStoreResponse event) {
			long requestId = event.getRequest().getRequestAgent()
					.getRequestId();
			logger.debug("got capstore response, requestId={}", requestId);
			WebRequest requestEvent = removeRequest(requestId);
			if (requestEvent == null) {
				logger.debug(
						"UNUSUAL: requestId ({}) doesn't have an associted WebRequest",
						requestId);
				return;
			}

			trigger(new WebResponse(requestEvent, event.getData()), web);

		}

	};

	/** The handle sss response. */
	Handler<StorageStatusResponse> handleSSSResponse = new Handler<StorageStatusResponse>() {
		@SuppressWarnings("unchecked")
		@Override
		public void handle(StorageStatusResponse event) {

			Map<String, SIStatusItem> status = event.getStatus()
					.getStatusPerSI();

			JSONObject obj = new JSONObject();
			List<String> sis = new ArrayList<String>(status.keySet());
			List<Long> sizes = new ArrayList<Long>();
			List<Set<Integer>> sub_si = new ArrayList<Set<Integer>>();
			obj.put("SI", sis);
			for (String si : sis) {
				SIStatusItem item = status.get(si);
				sub_si.add(item.getShareNums());
				sizes.add(item.getSize());
			}

			obj.put("sizes", sizes);
			obj.put("subsi", sub_si);

			long requestId = event.getRequest().getRequestAgent()
					.getRequestId();
			logger.debug(
					"got status from storage server for requestId={}, status={}",
					requestId, obj.toString());
			WebRequest requestEvent = removeRequest(requestId);
			if (requestEvent == null) {
				logger.debug(
						"UNUSUAL: requestId ({}) doesn't have an associted WebRequest",
						requestId);
				return;
			}
			trigger(new WebResponse(requestEvent, obj.toJSONString()), web);
		}

	};

	/**
	 * Put request.
	 * 
	 * @param req
	 *            the req
	 */
	private void putRequest(WebRequest req) {
		synchronized (activeRequests) {
			activeRequests.put(req.getId(), req);
		}
	}

	/**
	 * Gets the request.
	 * 
	 * @param reqId
	 *            the req id
	 * @return the request
	 */
	private WebRequest getRequest(Long reqId) {
		synchronized (activeRequests) {
			return activeRequests.get(reqId);
		}
	}

	/**
	 * Removes the request.
	 * 
	 * @param reqId
	 *            the req id
	 * @return the web request
	 */
	private WebRequest removeRequest(Long reqId) {
		synchronized (activeRequests) {
			return activeRequests.remove(reqId);
		}
	}
}
