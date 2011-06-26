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
package eg.nileu.cis.nilestore.storage.port.status;

import java.io.Serializable;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class SIStatusItem.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class SIStatusItem implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -116563895111102290L;

	/** The size. */
	private final long size;

	/** The count. */
	private final long count;

	/** The share nums. */
	private final Set<Integer> shareNums;

	/**
	 * Instantiates a new sI status item.
	 * 
	 * @param count
	 *            the count
	 * @param size
	 *            the size
	 * @param sharenums
	 *            the sharenums
	 */
	public SIStatusItem(long count, long size, Set<Integer> sharenums) {
		this.size = size;
		this.count = count;
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
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Gets the count.
	 * 
	 * @return the count
	 */
	public long getCount() {
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (count ^ (count >>> 32));
		result = prime * result
				+ ((shareNums == null) ? 0 : shareNums.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SIStatusItem other = (SIStatusItem) obj;
		if (count != other.count)
			return false;
		if (shareNums == null) {
			if (other.shareNums != null)
				return false;
		} else if (!shareNums.equals(other.shareNums))
			return false;
		if (size != other.size)
			return false;
		return true;
	}

}