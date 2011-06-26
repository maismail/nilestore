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
package eg.nileu.cis.nilestore.immutable.peertracker.port;

import java.util.Set;

import se.sics.kompics.Event;
import eg.nileu.cis.nilestore.immutable.file.FileInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class Query.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Query extends Event {

	/** The file info. */
	private final FileInfo fileInfo;

	/** The share nums. */
	private final Set<Integer> shareNums;

	/**
	 * Instantiates a new query.
	 * 
	 * @param fileinfo
	 *            the fileinfo
	 * @param sharenums
	 *            the sharenums
	 */
	public Query(FileInfo fileinfo, Set<Integer> sharenums) {
		this.fileInfo = fileinfo;
		this.shareNums = sharenums;

	}

	/**
	 * Gets the share nums.
	 * 
	 * @return the share nums
	 */
	public Set<Integer> getShareNums() {
		return shareNums;
	}

	/**
	 * Gets the file info.
	 * 
	 * @return the file info
	 */
	public FileInfo getFileInfo() {
		return fileInfo;
	}

}
