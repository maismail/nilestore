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
package eg.nileu.cis.nilestore.utils;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class Barrier.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Barrier {

	/** The barrier. */
	private boolean[] barrier;

	/** The limit. */
	private int limit;

	/** The internal index. */
	private int internalIndex;

	/**
	 * Instantiates a new barrier.
	 * 
	 * @param size
	 *            the size
	 */
	public Barrier(int size) {
		barrier = new boolean[size];
		clear();
		resetLimit();
	}

	/**
	 * Checks if is limited.
	 * 
	 * @return true, if is limited
	 */
	public boolean isLimited() {
		return limit != -1;
	}

	/**
	 * Reset limit.
	 */
	public void resetLimit() {
		limit = -1;
	}

	/**
	 * Sets the true.
	 * 
	 * @param index
	 *            the index
	 * @return true, if successful
	 */
	public boolean setTrue(int index) {
		if (index > barrier.length)
			return false;

		barrier[index] = true;
		return true;
	}

	/**
	 * Sets the true.
	 * 
	 * @return true, if successful
	 */
	public boolean setTrue() {
		if (internalIndex > barrier.length)
			return false;

		barrier[internalIndex] = true;
		internalIndex++;
		return true;
	}

	/**
	 * Checks if is filled.
	 * 
	 * @return true, if is filled
	 */
	public boolean isFilled() {
		int count = 0;
		for (boolean b : barrier) {
			if (b) {
				count++;
			}
		}

		if (isLimited()) {
			if (count != limit) {
				return false;
			}
			return true;
		}

		if (count != barrier.length)
			return false;

		return true;
	}

	/**
	 * Sets the limit.
	 * 
	 * @param limit
	 *            the new limit
	 */
	public void setLimit(int limit) {
		if (limit > barrier.length)
			this.limit = barrier.length;
		else
			this.limit = limit;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		clear();
	}

	/**
	 * Reset.
	 * 
	 * @param newlength
	 *            the newlength
	 */
	public void reset(int newlength) {
		this.barrier = new boolean[newlength];
		clear();
		resetLimit();
	}

	/**
	 * Clear.
	 */
	private void clear() {
		Arrays.fill(barrier, false);
		internalIndex = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Barrier [barrier=" + Arrays.toString(barrier) + ", limit="
				+ limit + ", internalIndex=" + internalIndex + "]";
	}
}
