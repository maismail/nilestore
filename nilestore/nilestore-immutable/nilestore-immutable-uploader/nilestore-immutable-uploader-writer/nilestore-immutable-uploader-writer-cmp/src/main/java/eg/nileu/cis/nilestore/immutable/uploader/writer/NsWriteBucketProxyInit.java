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
package eg.nileu.cis.nilestore.immutable.uploader.writer;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class NsWriteBucketProxyInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsWriteBucketProxyInit extends Init {

	/** The self address. */
	private final NilestoreAddress selfAddress;

	/** The dest. */
	private final ComponentAddress dest;

	/** The self. */
	private final ComponentAddress self;

	/** The fileinfo. */
	private final FileInfo fileinfo;

	/** The sharenum. */
	private final int sharenum;

	/**
	 * Instantiates a new ns write bucket proxy init.
	 * 
	 * @param dest
	 *            the dest
	 * @param self
	 *            the self
	 * @param selfAddress
	 *            the self address
	 * @param fileinfo
	 *            the fileinfo
	 * @param sharenum
	 *            the sharenum
	 */
	public NsWriteBucketProxyInit(ComponentAddress dest, ComponentAddress self,
			NilestoreAddress selfAddress, FileInfo fileinfo, int sharenum) {
		this.dest = dest;
		this.self = self;
		this.selfAddress = selfAddress;
		this.fileinfo = fileinfo;
		this.sharenum = sharenum;

	}

	/**
	 * Gets the self address.
	 * 
	 * @return the self address
	 */
	public NilestoreAddress getSelfAddress() {
		return selfAddress;
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

	/**
	 * Gets the fileinfo.
	 * 
	 * @return the fileinfo
	 */
	public FileInfo getFileinfo() {
		return fileinfo;
	}

}
