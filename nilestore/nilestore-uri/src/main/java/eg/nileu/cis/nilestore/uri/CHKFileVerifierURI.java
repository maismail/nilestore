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

import eg.nileu.cis.nilestore.interfaces.uri.IURI;
import eg.nileu.cis.nilestore.interfaces.uri.IVerifierURI;

// TODO: Auto-generated Javadoc
/**
 * The Class CHKFileVerifierURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CHKFileVerifierURI implements IVerifierURI {

	/** The Constant BASE_STRING. */
	public static final String BASE_STRING = "URI:CHK-Verifier:";

	/** The pattern. */
	private final Pattern pattern = Pattern.compile(BASE_STRING
			+ uri.BASE32_CHARS + ":" + uri.BASE32_CHARS + ":" + uri.NUMBERS
			+ ":" + uri.NUMBERS + ":" + uri.NUMBERS);

	/** The size. */
	private final long size;

	/** The UEB hash. */
	private final byte[] UEBHash;

	/** The needed shares. */
	private final int neededShares;

	/** The total shares. */
	private final int totalShares;

	/** The storage_index. */
	private final byte[] storage_index;

	/**
	 * Instantiates a new cHK file verifier uri.
	 * 
	 * @param cap
	 *            the cap
	 */
	public CHKFileVerifierURI(String cap) {
		Matcher matcher = pattern.matcher(cap);
		if (matcher.matches()) {
			storage_index = Base32.decode(matcher.group(1));
			UEBHash = Base32.decode(matcher.group(2));
			neededShares = Integer.parseInt(matcher.group(3));
			totalShares = Integer.parseInt(matcher.group(4));
			size = Long.parseLong(matcher.group(5));
		} else {
			// FIXME: throws a BADURI Exception

			storage_index = null;
			UEBHash = null;
			neededShares = 0;
			totalShares = 0;
			size = 0;

		}
	}

	/**
	 * Instantiates a new cHK file verifier uri.
	 * 
	 * @param storage_index
	 *            the storage_index
	 * @param UEBHash
	 *            the uEB hash
	 * @param needed_shares
	 *            the needed_shares
	 * @param total_shares
	 *            the total_shares
	 * @param size
	 *            the size
	 */
	public CHKFileVerifierURI(byte[] storage_index, byte[] UEBHash,
			int needed_shares, int total_shares, long size) {
		this.storage_index = storage_index;
		this.UEBHash = UEBHash;
		this.neededShares = needed_shares;
		this.totalShares = total_shares;
		this.size = size;
	}

	/**
	 * Instantiates a new cHK file verifier uri.
	 * 
	 * @param storage_index
	 *            the storage_index
	 * @param UEBHash
	 *            the uEB hash
	 * @param needed_shares
	 *            the needed_shares
	 * @param total_shares
	 *            the total_shares
	 * @param size
	 *            the size
	 */
	public CHKFileVerifierURI(String storage_index, byte[] UEBHash,
			int needed_shares, int total_shares, long size) {
		this(Base32.decode(storage_index), UEBHash, needed_shares,
				total_shares, size);
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
		// TODO Auto-generated method stub
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
	public IVerifierURI getVerifyCap() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s%s:%s:%d:%d:%d", BASE_STRING,
				Base32.encode(storage_index), Base32.encode(UEBHash),
				neededShares, totalShares, size);
	}

}
