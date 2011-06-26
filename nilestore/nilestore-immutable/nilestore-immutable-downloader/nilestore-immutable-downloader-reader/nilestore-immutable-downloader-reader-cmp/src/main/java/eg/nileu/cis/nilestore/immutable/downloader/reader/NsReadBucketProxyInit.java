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
package eg.nileu.cis.nilestore.immutable.downloader.reader;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.ComponentAddress;

// TODO: Auto-generated Javadoc
/**
 * The Class NsReadBucketProxyInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsReadBucketProxyInit extends Init {

	/** The dest. */
	private final ComponentAddress dest;

	/** The self. */
	private final ComponentAddress self;

	/** The sharenum. */
	private final int sharenum;

	/**
	 * Instantiates a new ns read bucket proxy init.
	 * 
	 * @param self
	 *            the self
	 * @param dest
	 *            the dest
	 * @param sharenum
	 *            the sharenum
	 */
	public NsReadBucketProxyInit(ComponentAddress self, ComponentAddress dest,
			int sharenum) {
		this.self = self;
		this.dest = dest;
		this.sharenum = sharenum;

	}

	/**
	 * Gets the sharenum.
	 * 
	 * @return the sharenum
	 */
	public int getSharenum() {
		return sharenum;
	}

	/**
	 * Gets the dest.
	 * 
	 * @return the dest
	 */
	public ComponentAddress getDest() {
		return dest;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public ComponentAddress getSelf() {
		return self;
	}
}
