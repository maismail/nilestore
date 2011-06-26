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
package eg.nileu.cis.nilestore.utils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eg.nileu.cis.nilestore.utils.hashutils.Hash;

// TODO: Auto-generated Javadoc
/**
 * The Class DataUtils.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class DataUtils {

	/**
	 * Join.
	 * 
	 * @param bs
	 *            the bs
	 * @return the byte[]
	 */
	public static byte[] join(byte[]... bs) {
		int len = 0;
		for (byte[] b : bs) {
			len += b.length;
		}

		byte[] out = new byte[len];
		int offset = 0;
		for (byte[] b : bs) {
			System.arraycopy(b, 0, out, offset, b.length);
			offset += b.length;
		}
		return out;
	}

	/**
	 * Join.
	 * 
	 * @param bs
	 *            the bs
	 * @return the byte[]
	 */
	public static byte[] join(List<byte[]> bs) {
		int len = 0;
		for (byte[] b : bs) {
			len += b.length;
		}
		byte[] out = new byte[len];
		int offset = 0;
		for (byte[] b : bs) {
			System.arraycopy(b, 0, out, offset, b.length);
			offset += b.length;
		}
		return out;
	}

	/**
	 * Pack hashes list.
	 * 
	 * @param hashes
	 *            the hashes
	 * @return the byte[]
	 */
	public static byte[] packHashesList(List<ByteArray> hashes) {
		int len = hashes.size() * Hash.HASH_SIZE;
		return packHashesList(hashes, len);
	}

	/**
	 * Pack hashes list.
	 * 
	 * @param hashes
	 *            the hashes
	 * @param len
	 *            the len
	 * @return the byte[]
	 */
	public static byte[] packHashesList(List<ByteArray> hashes, int len) {
		ByteBuffer buffer = ByteBuffer.allocate(len);
		for (ByteArray b : hashes) {
			buffer.put(b.getBytes());
		}
		return buffer.array();
	}

	/**
	 * Unpack hashes list.
	 * 
	 * @param data
	 *            the data
	 * @return the map
	 */
	public static Map<Integer, ByteArray> unpackHashesList(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		Map<Integer, ByteArray> hashes = new HashMap<Integer, ByteArray>();
		assert data.length % Hash.HASH_SIZE == 0;
		int numHashes = data.length / Hash.HASH_SIZE;
		for (int i = 0; i < numHashes; i++) {
			byte[] hash = new byte[Hash.HASH_SIZE];
			buffer.get(hash);
			hashes.put(i, new ByteArray(hash));
		}
		return hashes;
	}

	/**
	 * Pack share hashes.
	 * 
	 * @param sharehashes
	 *            the sharehashes
	 * @return the byte[]
	 */
	public static byte[] packShareHashes(Map<Short, ByteArray> sharehashes) {
		int len = (Hash.HASH_SIZE + 2) * sharehashes.size();
		ByteBuffer buffer = ByteBuffer.allocate(len);

		for (short shareid : sharehashes.keySet()) {
			buffer.putShort(shareid);
			buffer.put(sharehashes.get(shareid).getBytes());
		}
		return buffer.array();
	}

	/**
	 * Unpack share hashes.
	 * 
	 * @param data
	 *            the data
	 * @return the map
	 */
	public static Map<Integer, ByteArray> unpackShareHashes(byte[] data) {
		Map<Integer, ByteArray> sharehases = new HashMap<Integer, ByteArray>();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		assert data.length % (Hash.HASH_SIZE + 2) == 0;
		int numHashes = data.length / (Hash.HASH_SIZE + 2);
		for (int i = 0; i < numHashes; i++) {
			int key = buffer.getShort();
			byte[] hash = new byte[Hash.HASH_SIZE];
			buffer.get(hash);
			sharehases.put(key, new ByteArray(hash));
		}
		return sharehases;
	}
}
