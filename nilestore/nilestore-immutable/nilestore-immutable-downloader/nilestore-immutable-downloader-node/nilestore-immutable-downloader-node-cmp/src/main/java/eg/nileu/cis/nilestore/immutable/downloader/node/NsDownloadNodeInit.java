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
package eg.nileu.cis.nilestore.immutable.downloader.node;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.uri.CHKFileVerifierURI;

// TODO: Auto-generated Javadoc
/**
 * The Class NsDownloadNodeInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsDownloadNodeInit extends Init {

	/** The self. */
	private final NilestoreAddress self;

	/** The verify cap. */
	private final CHKFileVerifierURI verifyCap;

	/**
	 * Instantiates a new ns download node init.
	 * 
	 * @param self
	 *            the self
	 * @param verifyCap
	 *            the verify cap
	 */
	public NsDownloadNodeInit(NilestoreAddress self,
			CHKFileVerifierURI verifyCap) {
		this.self = self;
		this.verifyCap = verifyCap;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}

	/**
	 * Gets the verify cap.
	 * 
	 * @return the verify cap
	 */
	public CHKFileVerifierURI getVerifyCap() {
		return verifyCap;
	}

}
