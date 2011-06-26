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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.utils.ByteArray;
import eg.nileu.cis.nilestore.utils.DataUtils;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.NetString;
import eg.nileu.cis.nilestore.utils.hashtree.BadHashError;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class UEB.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class UEB {

	/** The uri_extension_data. */
	private Map<String, String> uri_extension_data;

	/**
	 * Instantiates a new uEB.
	 */
	public UEB() {
		this.uri_extension_data = new HashMap<String, String>();
	}

	/**
	 * Instantiates a new uEB.
	 * 
	 * @param data
	 *            the data
	 */
	public UEB(byte[] data) {
		this.uri_extension_data = new HashMap<String, String>();
		unpack(data);
	}

	/**
	 * Instantiates a new uEB.
	 * 
	 * @param data
	 *            the data
	 * @param hash
	 *            the hash
	 * @throws BadHashError
	 *             the bad hash error
	 */
	public UEB(byte[] data, byte[] hash) throws BadHashError {
		byte[] h = Hash.uri_extension_hash(data);
		if (!Arrays.equals(h, hash)) {
			throw new BadHashError(
					"URI Extension Hash doesn't match share isn't correct");
		}
		this.uri_extension_data = new HashMap<String, String>();
		unpack(data);
	}

	/**
	 * Adds the key value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addKeyValue(String key, String value) {
		this.uri_extension_data.put(key, value);
	}

	/**
	 * Adds the key value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addKeyValue(String key, ByteArray value) {
		addKeyValue(key, Base32.encode(value.getBytes()));
	}

	/**
	 * Adds the key value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addKeyValue(String key, byte[] value) {
		addKeyValue(key, Base32.encode(value));
	}

	/**
	 * Adds the key value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addKeyValue(String key, long value) {
		addKeyValue(key, String.valueOf(value));
	}

	/**
	 * Gets the value.
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String getValue(String key) {
		return uri_extension_data.get(key);
	}

	/**
	 * Gets the keys.
	 * 
	 * @return the keys
	 */
	public Set<String> getKeys() {
		return uri_extension_data.keySet();
	}

	/**
	 * Gets the file size.
	 * 
	 * @return the file size
	 */
	public long getFileSize() {
		String val = uri_extension_data.get("size");
		if (val == null) {
			return 0;
		}
		return Long.valueOf(val);
	}

	/**
	 * Gets the num segements.
	 * 
	 * @return the num segements
	 */
	public int getNumSegements() {
		return getIntegerVal("num_segments");
	}

	/**
	 * Gets the segment size.
	 * 
	 * @return the segment size
	 */
	public int getSegmentSize() {
		return getIntegerVal("segment_size");
	}

	/**
	 * Gets the needed shares.
	 * 
	 * @return the needed shares
	 */
	public int getNeededShares() {
		return getIntegerVal("needed_shares");
	}

	/**
	 * Gets the codec params.
	 * 
	 * @return the codec params
	 */
	public EncodingParam getCodecParams() {
		String p = uri_extension_data.get("codec_params");
		return new EncodingParam(p);
	}

	/**
	 * Gets the tail codec params.
	 * 
	 * @return the tail codec params
	 */
	public EncodingParam getTailCodecParams() {
		String p = uri_extension_data.get("tail_codec_params");
		return new EncodingParam(p);
	}

	/**
	 * Gets the cipher text root hash.
	 * 
	 * @return the cipher text root hash
	 */
	public Map<Integer, ByteArray> getCipherTextRootHash() {
		return getRootHash("crypttext_root_hash");
	}

	/**
	 * Gets the shares root hash.
	 * 
	 * @return the shares root hash
	 */
	public Map<Integer, ByteArray> getSharesRootHash() {
		return getRootHash("share_root_hash");
	}

	/**
	 * Gets the integer val.
	 * 
	 * @param key
	 *            the key
	 * @return the integer val
	 */
	private int getIntegerVal(String key) {
		String val = uri_extension_data.get(key);
		if (val == null) {
			return 0;
		}
		return Integer.valueOf(val);
	}

	/**
	 * Gets the root hash.
	 * 
	 * @param key
	 *            the key
	 * @return the root hash
	 */
	private Map<Integer, ByteArray> getRootHash(String key) {
		Map<Integer, ByteArray> root = new HashMap<Integer, ByteArray>();
		String hash = uri_extension_data.get(key);
		if (hash != null) {
			byte[] h = Base32.decode(hash);
			root.put(0, new ByteArray(h));
		}
		return root;
	}

	/**
	 * Gets the packed ueb.
	 * 
	 * @return the packed ueb
	 */
	public byte[] getPackedUEB() {
		int total_len = 0;
		List<String> keys = new ArrayList<String>(uri_extension_data.keySet());
		Collections.sort(keys);

		List<ByteArray> pieces = new ArrayList<ByteArray>();
		for (String key : keys) {
			byte[] key_bytes = key.getBytes();
			String value = uri_extension_data.get(key);
			byte[] value_bytes;
			if (key.contains("hash")) {
				value_bytes = Base32.decode(value);
			} else {
				value_bytes = value.getBytes();
			}

			byte[] value_netstring = NetString.toNetString(value_bytes);
			int len = value_netstring.length + key_bytes.length + 1;
			total_len += len;

			byte[] piece = new byte[len];

			System.arraycopy(key_bytes, 0, piece, 0, key_bytes.length);
			piece[key_bytes.length] = NetString.COLON;
			System.arraycopy(value_netstring, 0, piece, key_bytes.length + 1,
					value_netstring.length);
			pieces.add(new ByteArray(piece));
		}

		return DataUtils.packHashesList(pieces, total_len);
	}

	/**
	 * Unpack.
	 * 
	 * @param data
	 *            the data
	 */
	private void unpack(byte[] data) {

		String tmp = new String(data);
		int index = 0;
		while (!tmp.isEmpty()) {
			int colon = tmp.indexOf(":");
			index += colon;
			String key = tmp.substring(0, colon);
			tmp = tmp.substring(colon + 1);
			index += 1;

			colon = tmp.indexOf(":");
			index += colon;
			String num = tmp.substring(0, colon);
			int len = Integer.valueOf(num);
			tmp = tmp.substring(colon + 1);
			index += 1;

			String val = tmp.substring(0, len);
			if (key.contains("hash")) {
				byte[] hash = Arrays.copyOfRange(data, index, index + len);
				val = Base32.encode(hash);
				tmp = new String(Arrays.copyOfRange(data, index + len + 1,
						data.length));
			} else {
				tmp = tmp.substring(len + 1);
			}
			index += len + 1;

			uri_extension_data.put(key, val);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UEB [uri_extension_data=" + uri_extension_data + "]";
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		byte[] data = new byte[] { 99, 111, 100, 101, 99, 95, 110, 97, 109,
				101, 58, 51, 58, 99, 114, 115, 44, 99, 111, 100, 101, 99, 95,
				112, 97, 114, 97, 109, 115, 58, 49, 49, 58, 49, 51, 49, 48, 55,
				51, 45, 51, 45, 49, 48, 44, 99, 114, 121, 112, 116, 116, 101,
				120, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58, -18, 78, -9,
				-75, 27, 29, -45, -42, -70, 35, 75, -11, 58, 40, -1, -126,
				-120, 89, -119, -111, 20, -18, 86, -76, -56, -53, -8, 6, -15,
				26, 111, -45, 44, 99, 114, 121, 112, 116, 116, 101, 120, 116,
				95, 114, 111, 111, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58,
				-7, 52, 3, -123, -50, 56, 72, -21, -17, -59, 34, 70, -55, 43,
				91, -122, 66, -38, 82, -62, -6, 17, 81, 2, -51, 66, 115, 121,
				1, -46, -68, -5, 44, 110, 101, 101, 100, 101, 100, 95, 115,
				104, 97, 114, 101, 115, 58, 49, 58, 51, 44, 110, 117, 109, 95,
				115, 101, 103, 109, 101, 110, 116, 115, 58, 50, 58, 50, 53, 44,
				115, 101, 103, 109, 101, 110, 116, 95, 115, 105, 122, 101, 58,
				54, 58, 49, 51, 49, 48, 55, 51, 44, 115, 104, 97, 114, 101, 95,
				114, 111, 111, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58, 25,
				44, -36, -49, -104, -14, -92, -57, 81, -49, 74, 2, 99, 82, -43,
				-26, -71, 113, 67, 46, 66, -106, -36, -10, 36, 88, -71, 58, -2,
				-93, -51, 99, 44, 115, 105, 122, 101, 58, 55, 58, 51, 49, 57,
				49, 48, 56, 52, 44, 116, 97, 105, 108, 95, 99, 111, 100, 101,
				99, 95, 112, 97, 114, 97, 109, 115, 58, 49, 48, 58, 52, 53, 51,
				51, 51, 45, 51, 45, 49, 48, 44, 116, 111, 116, 97, 108, 95,
				115, 104, 97, 114, 101, 115, 58, 50, 58, 49, 48, 44 };

		byte[] data2 = new byte[] { 99, 111, 100, 101, 99, 95, 110, 97, 109,
				101, 58, 51, 58, 99, 114, 115, 44, 99, 111, 100, 101, 99, 95,
				112, 97, 114, 97, 109, 115, 58, 49, 49, 58, 49, 51, 49, 48, 55,
				51, 45, 51, 45, 49, 48, 44, 99, 114, 121, 112, 116, 116, 101,
				120, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58, 120, 20, 32,
				-66, 81, 84, 74, 83, -32, -57, 95, -111, -82, -38, 21, 85, 18,
				-118, 112, -95, -81, 51, 61, -40, 101, 78, 33, 95, 83, 59, 25,
				-94, 44, 99, 114, 121, 112, 116, 116, 101, 120, 116, 95, 114,
				111, 111, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58, 47, -81,
				117, -68, -76, -37, -14, -93, 57, -21, 30, 24, -110, -117, 2,
				123, -45, -128, 93, -19, -33, 58, -124, 34, 76, -44, -95, -41,
				19, 102, -97, -54, 44, 110, 101, 101, 100, 101, 100, 95, 115,
				104, 97, 114, 101, 115, 58, 49, 58, 51, 44, 110, 117, 109, 95,
				115, 101, 103, 109, 101, 110, 116, 115, 58, 51, 58, 49, 49, 56,
				44, 115, 101, 103, 109, 101, 110, 116, 95, 115, 105, 122, 101,
				58, 54, 58, 49, 51, 49, 48, 55, 51, 44, 115, 104, 97, 114, 101,
				95, 114, 111, 111, 116, 95, 104, 97, 115, 104, 58, 51, 50, 58,
				-92, -64, 82, -119, -2, -96, 53, -6, -78, -120, 25, -45, -85,
				74, -67, -49, -28, 112, -5, -42, -9, 92, -47, 37, -111, 108,
				22, 117, 49, 0, 114, -113, 44, 115, 105, 122, 101, 58, 56, 58,
				49, 53, 51, 52, 48, 49, 54, 50, 44, 116, 97, 105, 108, 95, 99,
				111, 100, 101, 99, 95, 112, 97, 114, 97, 109, 115, 58, 57, 58,
				52, 54, 50, 51, 45, 51, 45, 49, 48, 44, 116, 111, 116, 97, 108,
				95, 115, 104, 97, 114, 101, 115, 58, 50, 58, 49, 48, 44 };

		System.out.println(new String(data2));

		String h = "6ugju473hlehybhdxovidtcdhm4nxui3s7jpwngd2w4l2id32wbq";
		byte[] hash = Base32.decode(h);
		try {
			UEB x = new UEB(data, hash);
			System.out.println(x);
		} catch (BadHashError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
