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
package eg.nileu.cis.nilestore.cryptography;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

// TODO: Auto-generated Javadoc
/**
 * The Class AESCipher.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class AESCipher {

	/** The cipher. */
	private SICBlockCipher cipher;

	/** The prev_padding. */
	private int prev_padding;

	/** The block size. */
	private final int blockSize;

	// TODO: write test cases for that code
	// TODO: implement random access encryption/decryption
	/**
	 * Instantiates a new aES cipher.
	 * 
	 * @param encryptionKey
	 *            the encryption key
	 * @param forEncryption
	 *            the for encryption
	 */
	public AESCipher(byte[] encryptionKey, boolean forEncryption) {
		byte[] ivBytes = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		AESFastEngine baseengine = new AESFastEngine();
		cipher = new SICBlockCipher(baseengine);
		cipher.init(forEncryption, new ParametersWithIV(new KeyParameter(
				encryptionKey), ivBytes, 0, ivBytes.length));

		prev_padding = 0;
		blockSize = cipher.getBlockSize();
	}

	/**
	 * Instantiates a new aES cipher.
	 * 
	 * @param encryptionKey
	 *            the encryption key
	 * @param IV
	 *            the iV
	 * @param forEncryption
	 *            the for encryption
	 */
	public AESCipher(byte[] encryptionKey, byte[] IV, boolean forEncryption) {
		AESFastEngine baseengine = new AESFastEngine();
		cipher = new SICBlockCipher(baseengine);
		cipher.init(forEncryption, new ParametersWithIV(new KeyParameter(
				encryptionKey), IV, 0, IV.length));

		prev_padding = 0;
		blockSize = cipher.getBlockSize();
	}

	/**
	 * Process bytes.
	 * 
	 * @param in
	 *            the in
	 * @param inOff
	 *            the in off
	 * @param len
	 *            the len
	 * @param out
	 *            the out
	 * @param outOff
	 *            the out off
	 * @return the int
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws DataLengthException
	 *             the data length exception
	 */
	public int processBytes(byte[] in, int inOff, int len, byte[] out,
			int outOff) throws IllegalArgumentException, DataLengthException {
		if (len < 0) {
			throw new IllegalArgumentException(
					"Can't have a negative input length!");
		}

		if (len > 0) {
			if ((outOff + len) > out.length) {
				throw new DataLengthException("output buffer too short");
			}
		}

		int padding = 0;
		int _len = len;
		int _inoff = inOff;
		int _outoff = outOff;

		if (prev_padding != 0) {
			int prefix_length = blockSize - prev_padding;
			_inoff += prefix_length;
			_outoff += prefix_length;
			_len -= prefix_length;

			byte[] padded_prefix = new byte[blockSize];
			System.arraycopy(in, inOff, padded_prefix, prev_padding,
					prefix_length);
			cipher.Countermm();

			cipher.processBlock(padded_prefix, 0, padded_prefix, 0);
			System.arraycopy(padded_prefix, prev_padding, out, outOff,
					prefix_length);
		}

		padding = _len % blockSize;

		if (padding == 0) {
			processBlocks(in, _inoff, _len, out, _outoff);
		} else {
			int new_len = _len - padding;
			processBlocks(in, _inoff, new_len, out, _outoff);
			_outoff += new_len;
			_inoff += new_len;

			byte[] padded_suffix = new byte[blockSize];
			System.arraycopy(in, _inoff, padded_suffix, 0, padding);
			cipher.processBlock(padded_suffix, 0, padded_suffix, 0);
			System.arraycopy(padded_suffix, 0, out, _outoff, padding);
		}
		prev_padding = padding;
		return len;
	}

	/**
	 * Do final.
	 * 
	 * @param out
	 *            the out
	 * @param off
	 *            the off
	 * @return the int
	 */
	public int doFinal(byte[] out, int off) {
		reset();
		return out.length;
	}

	/**
	 * Update.
	 * 
	 * @param in
	 *            the in
	 * @param inOff
	 *            the in off
	 * @param len
	 *            the len
	 * @param out
	 *            the out
	 * @param outOff
	 *            the out off
	 * @return the int
	 */
	public int update(byte[] in, int inOff, int len, byte[] out, int outOff) {
		return processBytes(in, inOff, len, out, outOff);
	}

	/**
	 * Gets the output size.
	 * 
	 * @param length
	 *            the length
	 * @return the output size
	 */
	public int getOutputSize(int length) {
		return length;
	}

	/**
	 * Process blocks.
	 * 
	 * @param in
	 *            the in
	 * @param inOff
	 *            the in off
	 * @param len
	 *            the len
	 * @param out
	 *            the out
	 * @param outOff
	 *            the out off
	 */
	private void processBlocks(byte[] in, int inOff, int len, byte[] out,
			int outOff) {
		int resultLen = 0;
		if (len % blockSize != 0) {
			throw new DataLengthException(
					"input array length should be multiple of the blocksize");
		}

		int num_blocks = len / blockSize;

		while (num_blocks > 0) {
			resultLen += cipher
					.processBlock(in, inOff, out, outOff + resultLen);
			len -= blockSize;
			inOff += blockSize;
			num_blocks--;
		}
	}

	/**
	 * Reset.
	 */
	public void reset() {
		prev_padding = 0;
		cipher.reset();
	}
}
