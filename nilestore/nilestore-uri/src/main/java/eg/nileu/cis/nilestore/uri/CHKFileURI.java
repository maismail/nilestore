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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.interfaces.uri.IImmutableFileURI;
import eg.nileu.cis.nilestore.interfaces.uri.IURI;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class CHKFileURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CHKFileURI implements IImmutableFileURI {

	/** The Constant BASE_STRING. */
	public static final String BASE_STRING = "URI:CHK:";

	/** The pattern. */
	private final Pattern pattern = Pattern.compile(BASE_STRING
			+ uri.BASE32_CHARS + ":" + uri.BASE32_CHARS + ":" + uri.NUMBERS
			+ ":" + uri.NUMBERS + ":" + uri.NUMBERS);

	/** The size. */
	private final long size;

	/** The key. */
	private final byte[] key;

	/** The UEB hash. */
	private final byte[] UEBHash;

	/** The needed shares. */
	private final int neededShares;

	/** The total shares. */
	private final int totalShares;

	/** The storage_index. */
	private final byte[] storage_index;

	/**
	 * Instantiates a new cHK file uri.
	 * 
	 * @param cap
	 *            the cap
	 * @throws BadURIException
	 *             the bad uri exception
	 */
	public CHKFileURI(String cap) throws BadURIException {
		Matcher matcher = pattern.matcher(cap);
		if (matcher.matches()) {
			key = Base32.decode(matcher.group(1));
			UEBHash = Base32.decode(matcher.group(2));
			neededShares = Integer.parseInt(matcher.group(3));
			totalShares = Integer.parseInt(matcher.group(4));
			size = Long.parseLong(matcher.group(5));
			storage_index = Hash.storage_index_hash(key);
		} else {
			key = null;
			UEBHash = null;
			neededShares = 0;
			totalShares = 0;
			size = 0;
			storage_index = null;
			throw new BadURIException(String.format(
					"Cap (%s) is not an appropriate CHK URI", cap));
		}
	}

	/**
	 * Instantiates a new cHK file uri.
	 * 
	 * @param key
	 *            the key
	 * @param UEBHash
	 *            the uEB hash
	 * @param needed_shares
	 *            the needed_shares
	 * @param total_shares
	 *            the total_shares
	 * @param size
	 *            the size
	 */
	public CHKFileURI(byte[] key, byte[] UEBHash, int needed_shares,
			int total_shares, long size) {
		this.key = key;
		this.UEBHash = UEBHash;
		this.neededShares = needed_shares;
		this.totalShares = total_shares;
		this.size = size;
		storage_index = Hash.storage_index_hash(key);
	}

	/**
	 * Gets the key.
	 * 
	 * @return the key
	 */
	public byte[] getKey() {
		return key;
	}

	/**
	 * Gets the uEB hash.
	 * 
	 * @return the uEB hash
	 */
	public byte[] getUEBHash() {
		return UEBHash;
	}

	/**
	 * Gets the needed shares.
	 * 
	 * @return the needed shares
	 */
	public int getNeededShares() {
		return neededShares;
	}

	/**
	 * Gets the total shares.
	 * 
	 * @return the total shares
	 */
	public int getTotalShares() {
		return totalShares;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getStorageIndex()
	 */
	@Override
	public byte[] getStorageIndex() {
		return storage_index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#isMutable()
	 */
	@Override
	public boolean isMutable() {
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
	public IURI getReadonlyCap() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getVerifyCap()
	 */
	@Override
	public CHKFileVerifierURI getVerifyCap() {
		return new CHKFileVerifierURI(storage_index, UEBHash, neededShares,
				totalShares, size);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IImmutableFileURI#getSize()
	 */
	@Override
	public long getSize() {
		return size;
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
		result = prime * result + Arrays.hashCode(UEBHash);
		result = prime * result + Arrays.hashCode(key);
		result = prime * result + neededShares;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		result = prime * result + Arrays.hashCode(storage_index);
		result = prime * result + totalShares;
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
		CHKFileURI other = (CHKFileURI) obj;
		if (!Arrays.equals(UEBHash, other.UEBHash))
			return false;
		if (!Arrays.equals(key, other.key))
			return false;
		if (neededShares != other.neededShares)
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (size != other.size)
			return false;
		if (!Arrays.equals(storage_index, other.storage_index))
			return false;
		if (totalShares != other.totalShares)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s%s:%s:%d:%d:%d", BASE_STRING,
				Base32.encode(key), Base32.encode(UEBHash), neededShares,
				totalShares, size);
	}

}
