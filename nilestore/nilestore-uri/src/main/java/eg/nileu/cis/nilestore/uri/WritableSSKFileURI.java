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
package eg.nileu.cis.nilestore.uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.interfaces.uri.IMutableFileURI;
import eg.nileu.cis.nilestore.interfaces.uri.IVerifierURI;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class WritableSSKFileURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class WritableSSKFileURI implements IMutableFileURI {

	/** The Constant BASE_STRING. */
	public static final String BASE_STRING = "URI:SSK:";

	/** The pattern. */
	private final Pattern pattern = Pattern.compile(BASE_STRING
			+ uri.BASE32_CHARS + ":" + uri.BASE32_CHARS);

	/** The writekey. */
	private final byte[] writekey;

	/** The readkey. */
	private final byte[] readkey;

	/** The fingerprint. */
	private final byte[] fingerprint;

	/** The storage index. */
	private final byte[] storageIndex;

	/**
	 * Instantiates a new writable ssk file uri.
	 * 
	 * @param writekey
	 *            the writekey
	 * @param fingerprint
	 *            the fingerprint
	 */
	public WritableSSKFileURI(byte[] writekey, byte[] fingerprint) {

		this.writekey = writekey;
		this.readkey = Hash.ssk_readkey_hash(writekey);
		this.storageIndex = Hash.ssk_storage_index_hash(readkey);
		this.fingerprint = fingerprint;

	}

	/**
	 * Instantiates a new writable ssk file uri.
	 * 
	 * @param cap
	 *            the cap
	 */
	public WritableSSKFileURI(String cap) {
		Matcher matcher = pattern.matcher(cap);
		if (matcher.matches()) {
			this.writekey = Base32.decode(matcher.group(1));

			this.readkey = Hash.ssk_readkey_hash(writekey);
			this.storageIndex = Hash.ssk_storage_index_hash(readkey);
			this.fingerprint = Base32.decode(matcher.group(2));
		} else {
			// FIXME: throws a BADURI Exception
			writekey = null;
			readkey = null;
			storageIndex = null;
			fingerprint = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#isMutable()
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#isReadonly()
	 */
	@Override
	public boolean isReadonly() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getStorageIndex()
	 */
	@Override
	public byte[] getStorageIndex() {
		return storageIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getReadonlyCap()
	 */
	@Override
	public ReadonlySSKFileURI getReadonlyCap() {
		return new ReadonlySSKFileURI(readkey, fingerprint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getVerifyCap()
	 */
	@Override
	public IVerifierURI getVerifyCap() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s%s:%s", BASE_STRING, Base32.encode(writekey),
				Base32.encode(fingerprint));
	}

}
