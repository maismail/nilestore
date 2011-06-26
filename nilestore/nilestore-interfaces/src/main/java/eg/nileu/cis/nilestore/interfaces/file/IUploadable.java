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
package eg.nileu.cis.nilestore.interfaces.file;

import java.io.IOException;

import eg.nileu.cis.nilestore.utils.EncodingParam;

// TODO: Auto-generated Javadoc
/**
 * The Interface IUploadable.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public interface IUploadable {

	/**
	 * Sets the encoding params.
	 * 
	 * @param params
	 *            the new encoding params
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void setEncodingParams(EncodingParam params) throws IOException;

	/**
	 * Gets the encoding params.
	 * 
	 * @return the encoding params
	 */
	public EncodingParam getEncodingParams();

	/**
	 * Length.
	 * 
	 * @return the long
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public long length() throws IOException;

	/**
	 * Pos.
	 * 
	 * @return the long
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public long pos() throws IOException;

	/**
	 * Gets the encryptionkey.
	 * 
	 * @return the encryptionkey
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] getEncryptionkey() throws IOException;

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] getStorageIndex() throws IOException;

	/**
	 * Gets the upload results.
	 * 
	 * @return the upload results
	 */
	public UploadResults getUploadResults();

	/**
	 * Read.
	 * 
	 * @param data
	 *            the data
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public int read(byte[] data) throws IOException;

	/**
	 * Close.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException;
}
