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
package eg.nileu.cis.nilestore.immutable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import eg.nileu.cis.nilestore.channelfilters.ExtMessageDestinationFilter;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.connectionfd.port.CFailureDetector;
import eg.nileu.cis.nilestore.immutable.common.BucketProxy;
import eg.nileu.cis.nilestore.immutable.common.BucketProxyDestinationFilter;
import eg.nileu.cis.nilestore.immutable.common.Close;
import eg.nileu.cis.nilestore.immutable.common.GetDataResponse;
import eg.nileu.cis.nilestore.immutable.common.PutGetData;
import eg.nileu.cis.nilestore.immutable.downloader.reader.NsReadBucketProxy;
import eg.nileu.cis.nilestore.immutable.downloader.reader.NsReadBucketProxyInit;
import eg.nileu.cis.nilestore.immutable.downloader.reader.port.RBProxy;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;
import eg.nileu.cis.nilestore.immutable.uploader.writer.NsWriteBucketProxy;
import eg.nileu.cis.nilestore.immutable.uploader.writer.NsWriteBucketProxyInit;
import eg.nileu.cis.nilestore.immutable.uploader.writer.port.WBProxy;
import eg.nileu.cis.nilestore.storage.common;

// TODO: Auto-generated Javadoc
/**
 * The Class NsBucketProxiesHolder.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsBucketProxiesHolder extends ComponentDefinition {

	/** The network. */
	protected Positive<Network> network = requires(Network.class);

	/** The cfd. */
	protected Positive<CFailureDetector> cfd = requires(CFailureDetector.class);

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(NsBucketProxiesHolder.class);

	/** The proxies. */
	private Map<String, Component> proxies;

	/**
	 * Instantiates a new ns bucket proxies holder.
	 */
	public NsBucketProxiesHolder() {
		proxies = new HashMap<String, Component>();
	}

	/**
	 * Creates the write bucket proxy.
	 * 
	 * @param self
	 *            the self
	 * @param dest
	 *            the dest
	 * @param fileinfo
	 *            the fileinfo
	 * @param sharenum
	 *            the sharenum
	 * @param outputPort
	 *            the output port
	 * @return the string
	 */
	protected String createWriteBucketProxy(NilestoreAddress self,
			ComponentAddress dest, FileInfo fileinfo, int sharenum,
			Negative<WBProxy> outputPort) {
		Component wbp = create(NsWriteBucketProxy.class);

		String wbpId = common.getWriteBucketProxyID(fileinfo.getStorageIndex(),
				sharenum);
		ComponentAddress wbpadd = new ComponentAddress(self.getPeerAddress(),
				wbpId);

		connect(wbp.required(Network.class), network,
				new ExtMessageDestinationFilter(wbpId));
		connect(wbp.required(CFailureDetector.class), cfd);
		connect(outputPort, wbp.provided(WBProxy.class),
				new BucketProxyDestinationFilter(wbpId));

		synchronized (proxies) {
			proxies.put(wbpId, wbp);
		}

		trigger(new NsWriteBucketProxyInit(dest, wbpadd, self, fileinfo,
				sharenum), wbp.getControl());
		trigger(new Start(), wbp.getControl());

		logger.debug("WriteBucketProxy ({}) created for ShareNum ({})", wbpId,
				sharenum);
		return wbpId;
	}

	/**
	 * Creates the read bucket proxy.
	 * 
	 * @param self
	 *            the self
	 * @param dest
	 *            the dest
	 * @param storageIndex
	 *            the storage index
	 * @param sharenum
	 *            the sharenum
	 * @param outputPort
	 *            the output port
	 * @return the string
	 */
	protected String createReadBucketProxy(NilestoreAddress self,
			ComponentAddress dest, String storageIndex, int sharenum,
			Negative<RBProxy> outputPort) {
		Component rbp = create(NsReadBucketProxy.class);

		String rbpId = common.getReadBucketProxyID(storageIndex, sharenum);

		connect(rbp.required(Network.class), network,
				new ExtMessageDestinationFilter(rbpId));
		connect(outputPort, rbp.provided(RBProxy.class),
				new BucketProxyDestinationFilter(rbpId));
		connect(rbp.required(CFailureDetector.class), cfd);

		synchronized (proxies) {
			proxies.put(rbpId, rbp);
		}

		trigger(new NsReadBucketProxyInit(new ComponentAddress(
				self.getPeerAddress(), rbpId), dest, sharenum),
				rbp.getControl());
		trigger(new Start(), rbp.getControl());

		logger.debug("ReadBucketProxy ({}) created for ShareNum ({})", rbpId,
				sharenum);
		return rbpId;
	}

	/**
	 * Subscribe rbp to.
	 * 
	 * @param componentId
	 *            the component id
	 * @param handler
	 *            the handler
	 * @return true, if successful
	 */
	protected boolean subscribeRBPTo(String componentId,
			Handler<? extends GetDataResponse> handler) {
		Component cmp;
		synchronized (proxies) {
			cmp = proxies.get(componentId);
		}
		if (cmp == null)
			return false;

		subscribe(handler, cmp.provided(RBProxy.class));
		return true;
	}

	/**
	 * Trigger on rbp.
	 * 
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	protected boolean triggerOnRBP(PutGetData event) {
		String componentId = event.getDestID();
		Component cmp;
		synchronized (proxies) {
			cmp = proxies.get(componentId);
		}
		if (cmp == null)
			return false;
		trigger(event, cmp.provided(RBProxy.class));
		return true;
	}

	/**
	 * Trigger close on all rb ps.
	 */
	protected void triggerCloseOnAllRBPs() {
		synchronized (proxies) {
			for (Map.Entry<String, Component> e : proxies.entrySet()) {
				trigger(new Close(e.getKey()),
						e.getValue().provided(RBProxy.class));
			}
		}
	}

	/**
	 * Destroy.
	 * 
	 * @param componentId
	 *            the component id
	 * @param port
	 *            the port
	 */
	protected void destroy(String componentId,
			Negative<? extends BucketProxy> port) {
		Component cmp;
		synchronized (proxies) {
			cmp = proxies.remove(componentId);
		}

		if (cmp == null) {
			logger.debug("BucketProxy ({}) doesn't exists", componentId);
			return;
		}

		destroyProxy(cmp, port);
	}

	/**
	 * Destroy all bucket proxies.
	 * 
	 * @param port
	 *            the port
	 */
	protected void destroyAllBucketProxies(Negative<? extends BucketProxy> port) {
		synchronized (proxies) {
			Collection<Component> cmps = proxies.values();
			for (Component cmp : cmps) {
				destroyProxy(cmp, port);
			}
			proxies.clear();
		}
	}

	/**
	 * Destroy proxy.
	 * 
	 * @param cmp
	 *            the cmp
	 * @param port
	 *            the port
	 */
	@SuppressWarnings("unchecked")
	private void destroyProxy(Component cmp,
			Negative<? extends BucketProxy> port) {
		disconnect(cmp.required(Network.class), network);
		disconnect(cmp.required(CFailureDetector.class), cfd);
		if (port.getPortType() instanceof WBProxy) {
			disconnect((Negative<WBProxy>) port, cmp.provided(WBProxy.class));
		} else if (port.getPortType() instanceof RBProxy) {
			disconnect((Negative<RBProxy>) port, cmp.provided(RBProxy.class));
		}
		destroy(cmp);
	}
}
