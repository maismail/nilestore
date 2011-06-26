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
package eg.nileu.cis.nilestore;

import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class SecretHolder.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class SecretHolder {

	/** The _convergence_s. */
	private byte[] _convergence_s;

	/** The _lease_secret. */
	private byte[] _lease_secret;

	/**
	 * Instantiates a new secret holder.
	 * 
	 * @param convergence_s
	 *            the convergence_s
	 * @param lease_secret
	 *            the lease_secret
	 */
	public SecretHolder(byte[] convergence_s, byte[] lease_secret) {
		_convergence_s = convergence_s;
		_lease_secret = lease_secret;
	}

	/**
	 * Gets the _renewal_secret.
	 * 
	 * @return the _renewal_secret
	 */
	public byte[] get_renewal_secret() {
		return Hash.my_renewal_secret_hash(_lease_secret);
	}

	/**
	 * Gets the _cancel_secret.
	 * 
	 * @return the _cancel_secret
	 */
	public byte[] get_cancel_secret() {
		return Hash.my_cancel_secret_hash(_lease_secret);
	}

	/**
	 * Gets the _convergence_s.
	 * 
	 * @return the _convergence_s
	 */
	public byte[] get_convergence_s() {
		return _convergence_s;
	}
}
