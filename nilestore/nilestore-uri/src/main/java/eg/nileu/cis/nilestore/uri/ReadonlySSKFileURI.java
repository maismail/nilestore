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
 * The Class ReadonlySSKFileURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ReadonlySSKFileURI implements IMutableFileURI {

	/** The Constant BASE_STRING. */
	public static final String BASE_STRING = "URI:SSK-RO:";

	/** The pattern. */
	private final Pattern pattern = Pattern.compile(BASE_STRING
			+ uri.BASE32_CHARS + ":" + uri.BASE32_CHARS);

	/** The readkey. */
	private final byte[] readkey;

	/** The fingerprint. */
	private final byte[] fingerprint;

	/** The storage index. */
	private final byte[] storageIndex;

	/**
	 * Instantiates a new readonly ssk file uri.
	 * 
	 * @param readkey
	 *            the readkey
	 * @param fingerprint
	 *            the fingerprint
	 */
	public ReadonlySSKFileURI(byte[] readkey, byte[] fingerprint) {

		this.readkey = readkey;
		this.storageIndex = Hash.ssk_storage_index_hash(readkey);
		this.fingerprint = fingerprint;

	}

	/**
	 * Instantiates a new readonly ssk file uri.
	 * 
	 * @param cap
	 *            the cap
	 */
	public ReadonlySSKFileURI(String cap) {
		Matcher matcher = pattern.matcher(cap);
		if (matcher.matches()) {
			this.readkey = Base32.decode(matcher.group(1));
			this.storageIndex = Hash.ssk_storage_index_hash(readkey);
			this.fingerprint = Base32.decode(matcher.group(2));
		} else {
			// FIXME: throws a BADURI Exception
			readkey = null;
			storageIndex = null;
			fingerprint = null;
		}
	}

	/**
	 * Gets the readkey.
	 * 
	 * @return the readkey
	 */
	public byte[] getReadkey() {
		return readkey;
	}

	/**
	 * Gets the fingerprint.
	 * 
	 * @return the fingerprint
	 */
	public byte[] getFingerprint() {
		return fingerprint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#isMutable()
	 */
	@Override
	public boolean isMutable() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#isReadonly()
	 */
	@Override
	public boolean isReadonly() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getReadonlyCap()
	 */
	@Override
	public IMutableFileURI getReadonlyCap() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getVerifyCap()
	 */
	@Override
	public IVerifierURI getVerifyCap() {
		// TODO:
		return null;
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
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s%s:%s", BASE_STRING, Base32.encode(readkey),
				Base32.encode(fingerprint));
	}

}
