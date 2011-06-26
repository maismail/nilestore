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
package eg.nileu.cis.nilestore.interfaces.uri;

// TODO: Auto-generated Javadoc
/**
 * The Interface IURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public interface IURI {
	// FIXME: add init_from_string
	/**
	 * Checks if is mutable.
	 * 
	 * @return true, if is mutable
	 */
	public boolean isMutable();

	/**
	 * Checks if is readonly.
	 * 
	 * @return true, if is readonly
	 */
	public boolean isReadonly();

	/**
	 * Gets the readonly cap.
	 * 
	 * @return the readonly cap
	 */
	public IURI getReadonlyCap();

	/**
	 * Gets the verify cap.
	 * 
	 * @return the verify cap
	 */
	public IVerifierURI getVerifyCap();

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 */
	public byte[] getStorageIndex();
}
