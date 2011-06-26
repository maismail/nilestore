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
package eg.nileu.cis.nilestore.immutable.downloader.reader.port;

import java.util.Map;

import eg.nileu.cis.nilestore.common.Status;
import eg.nileu.cis.nilestore.immutable.common.ReqCompleted;
import eg.nileu.cis.nilestore.utils.ByteArray;

// TODO: Auto-generated Javadoc
/**
 * The Class GotSharesHashes.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class GotSharesHashes extends ReqCompleted {

	/** The share hashes. */
	private final Map<Integer, ByteArray> shareHashes;

	/**
	 * Instantiates a new got shares hashes.
	 * 
	 * @param sharenum
	 *            the sharenum
	 * @param status
	 *            the status
	 * @param shareHashes
	 *            the share hashes
	 */
	public GotSharesHashes(int sharenum, Status status,
			Map<Integer, ByteArray> shareHashes) {
		super(sharenum, status);
		this.shareHashes = shareHashes;
	}

	/**
	 * Gets the share hashes.
	 * 
	 * @return the share hashes
	 */
	public Map<Integer, ByteArray> getShareHashes() {
		return shareHashes;
	}

}
