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
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageStatusView.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class StorageStatusView implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3204799355174318245L;

	/** The status per si. */
	private final Map<String, SIStatusItem> statusPerSI;

	/** The used space. */
	private final long usedSpace;

	/** The countof shares. */
	private final long countofShares;

	/**
	 * Instantiates a new storage status view.
	 * 
	 * @param statusPerSI
	 *            the status per si
	 * @param usedspace
	 *            the usedspace
	 * @param countofshares
	 *            the countofshares
	 */
	public StorageStatusView(Map<String, SIStatusItem> statusPerSI,
			long usedspace, long countofshares) {
		this.statusPerSI = statusPerSI;
		this.usedSpace = usedspace;
		this.countofShares = countofshares;
	}

	/**
	 * Gets the status per si.
	 * 
	 * @return the status per si
	 */
	public Map<String, SIStatusItem> getStatusPerSI() {
		return statusPerSI;
	}

	/**
	 * Gets the used space.
	 * 
	 * @return the used space
	 */
	public long getUsedSpace() {
		return usedSpace;
	}

	/**
	 * Gets the countof shares.
	 * 
	 * @return the countof shares
	 */
	public long getCountofShares() {
		return countofShares;
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
		result = prime * result
				+ (int) (countofShares ^ (countofShares >>> 32));
		result = prime * result
				+ ((statusPerSI == null) ? 0 : statusPerSI.hashCode());
		result = prime * result + (int) (usedSpace ^ (usedSpace >>> 32));
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
		StorageStatusView other = (StorageStatusView) obj;
		if (countofShares != other.countofShares)
			return false;
		if (statusPerSI == null) {
			if (other.statusPerSI != null)
				return false;
		} else if (!statusPerSI.equals(other.statusPerSI))
			return false;
		if (usedSpace != other.usedSpace)
			return false;
		return true;
	}

}
