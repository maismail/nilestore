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

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.util.encoders.Hex;

// TODO: Auto-generated Javadoc
/**
 * The Class DumpUtils.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class DumpUtils {

	/**
	 * Dumptolog.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String dumptolog(int[] data) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (int e : data) {
			sb.append(e);
			sb.append(",");
		}
		int i = sb.lastIndexOf(",");
		if (i != -1) {
			sb.deleteCharAt(i);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Dumptolog.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String dumptolog(Map<?, ?> data) {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Object e : data.keySet()) {
			sb.append(String.valueOf(e));
			sb.append(":");
			Object val = data.get(e);

			if (val instanceof Set<?>) {
				sb.append(dumptolog((Set<?>) val));
			} else {
				sb.append(String.valueOf(val));
			}

			sb.append(",");
		}
		int i = sb.lastIndexOf(",");
		if (i != -1) {
			sb.deleteCharAt(i);
		}

		sb.append("}");

		return sb.toString();
	}

	/**
	 * Dumptolog.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String dumptolog(Collection<?> data) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object e : data) {
			sb.append(String.valueOf(e));
			sb.append(",");
		}
		int i = sb.lastIndexOf(",");
		if (i != -1) {
			sb.deleteCharAt(i);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * To hex.
	 * 
	 * @param data
	 *            the data
	 * @return the string
	 */
	public static String toHex(byte[] data) {
		return toHex(data, data.length);
	}

	/**
	 * To hex.
	 * 
	 * @param data
	 *            the data
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String toHex(byte[] data, int length) {
		return new String(Hex.encode(data));
	}

	/**
	 * To string.
	 * 
	 * @param bytes
	 *            the bytes
	 * @return the string
	 */
	public static String toString(byte[] bytes) {
		return new String(bytes);
	}

	/**
	 * Date diffto string.
	 * 
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String DateDifftoString(Date date) {
		String res = "";
		long timediff = (System.currentTimeMillis() - date.getTime()) / 1000;
		if (timediff < 60) {
			res += timediff + " seconds ago";
			return res;
		}

		double timediff1 = timediff / 60.0;
		if (timediff1 < 60) {
			res += String.format("%.2f minutes ago", timediff1);
			return res;
		}

		timediff1 = timediff1 / 60.0;

		if (timediff1 < 60) {
			res += String.format("%.2f hours ago", timediff1);
			return res;
		}

		timediff1 = timediff1 / 24.0;

		if (timediff1 < 60) {
			res += String.format("%.2f days ago", timediff1);
			return res;
		}

		timediff1 = timediff1 / 365.0;

		if (timediff1 < 60) {
			res += String.format("%.2f years ago", timediff1);
			return res;
		}
		return res;
	}
}