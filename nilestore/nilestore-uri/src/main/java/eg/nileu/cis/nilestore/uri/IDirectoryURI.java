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

import eg.nileu.cis.nilestore.interfaces.uri.IDirnodeURI;
import eg.nileu.cis.nilestore.interfaces.uri.IMutableFileURI;
import eg.nileu.cis.nilestore.interfaces.uri.IURI;
import eg.nileu.cis.nilestore.interfaces.uri.IVerifierURI;

// TODO: Auto-generated Javadoc
/**
 * The Class IDirectoryURI.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class IDirectoryURI implements IDirnodeURI {

	/** The BAS e_ string. */
	private final String BASE_STRING = "URI:DIR2:";

	/** The filenode. */
	private final IMutableFileURI filenode;

	/**
	 * Instantiates a new i directory uri.
	 * 
	 * @param filenode
	 *            the filenode
	 */
	public IDirectoryURI(IMutableFileURI filenode) {
		assert !filenode.isReadonly();
		this.filenode = filenode;
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
		// TODO Auto-generated method stub
		return filenode.getStorageIndex();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.uri.IURI#getReadonlyCap()
	 */
	@Override
	public IURI getReadonlyCap() {
		// TODO Auto-generated method stub
		return null;
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
		return filenode.toString().replaceFirst(WritableSSKFileURI.BASE_STRING,
				BASE_STRING);
	}

}
