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
package eg.nileu.cis.nilestore.storage;

import java.nio.ByteBuffer;

// TODO: Auto-generated Javadoc
/**
 * The Class LeaseInfo.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class LeaseInfo {

	/** The LEAS e_ size. */
	private final int LEASE_SIZE = 72;

	/** The _owner_num. */
	private int _owner_num;

	/** The _renew_secret. */
	private byte[] _renew_secret;

	/** The _cancel_secret. */
	private byte[] _cancel_secret;

	/** The _expiration_time. */
	private int _expiration_time;

	/**
	 * Instantiates a new lease info.
	 * 
	 * @param owner_num
	 *            the owner_num
	 * @param renew_secret
	 *            the renew_secret
	 * @param cancel_secret
	 *            the cancel_secret
	 * @param expiration_time
	 *            the expiration_time
	 * @param nodeid
	 *            the nodeid
	 */
	public LeaseInfo(int owner_num, byte[] renew_secret, byte[] cancel_secret,
			int expiration_time, String nodeid) {
		_owner_num = owner_num;
		_renew_secret = renew_secret;
		_cancel_secret = cancel_secret;
		_expiration_time = expiration_time;
	}

	/**
	 * To_immutable_data.
	 * 
	 * @return the byte buffer
	 */
	public ByteBuffer to_immutable_data() {
		ByteBuffer _struct = ByteBuffer.allocate(LEASE_SIZE);
		_struct.putInt(_owner_num);
		_struct.put(_renew_secret);
		_struct.put(_cancel_secret);
		_struct.putInt(_expiration_time);

		return _struct;
	}

}
