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
package eg.nileu.cis.nilestore.immutable.file;

import java.io.IOException;

import eg.nileu.cis.nilestore.cryptography.AESCipher;
import eg.nileu.cis.nilestore.interfaces.file.IUploadable;
import eg.nileu.cis.nilestore.interfaces.file.UploadResults;
import eg.nileu.cis.nilestore.utils.EncodingParam;

// TODO: Auto-generated Javadoc
/**
 * The Class EncryptFileHandle.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class EncryptFileHandle {

	/** The _original. */
	private IUploadable _original;

	/** The CHUNKSIZE. */
	public final int CHUNKSIZE = 50 * 1024;

	// private byte[] ivBytes = new byte[] {
	// 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
	// 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

	// private IvParameterSpec iv = new IvParameterSpec(ivBytes);
	// private SecretKeySpec _encryptionKey;
	/** The _encryption key. */
	private byte[] _encryptionKey;
	// private Cipher cipher;
	/** The size. */
	private long size;

	/** The cipher. */
	private AESCipher cipher;

	/**
	 * Instantiates a new encrypt file handle.
	 * 
	 * @param original
	 *            the original
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public EncryptFileHandle(IUploadable original) throws IOException {
		_original = original;
		cipher = null;
		size = _original.length();
	}

	/**
	 * Gets the encryptor.
	 * 
	 * @return the encryptor
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void getencryptor() throws IOException {
		if (cipher == null) {
			_encryptionKey = _original.getEncryptionkey();
			cipher = new AESCipher(_encryptionKey, true);
			// cipher = Cipher.getInstance("AES/CTR/NoPadding","BC");
			// cipher.init(Cipher.ENCRYPT_MODE, _encryptionKey,iv);
		}
	}

	/**
	 * Read encrypted.
	 * 
	 * @param length
	 *            the length
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] readEncrypted(int length) throws IOException {

		getencryptor();
		long currPos = _original.pos();
		if (currPos == size) {
			return null;
		}
		length = (int) (length > (size - currPos) ? (size - currPos) : length);
		int remaining = length;
		int ctLength = 0;

		byte[] cipherText = new byte[cipher.getOutputSize(length)];
		while (remaining > 0) {

			int readSize = Math.min(remaining, CHUNKSIZE);
			byte[] plaintext = new byte[readSize];
			int status = _original.read(plaintext);

			if (status == -1) {
				break;
			}
			remaining -= readSize;
			ctLength += cipher.update(plaintext, 0, plaintext.length,
					cipherText, ctLength);

		}

		return cipherText;
	}

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] getStorageIndex() throws IOException {
		return _original.getStorageIndex();
	}

	/**
	 * Gets the encoding param.
	 * 
	 * @return the encoding param
	 */
	public EncodingParam getEncodingParam() {
		return _original.getEncodingParams();
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
	 * Gets the upload results.
	 * 
	 * @return the upload results
	 */
	public UploadResults getUploadResults() {
		return _original.getUploadResults();
	}

	/**
	 * Close.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException {
		_original.close();
	}
}
