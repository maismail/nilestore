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

// TODO: Auto-generated Javadoc
/**
 * The Class MathUtils.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class MathUtils {

	/**
	 * Div_ceil.
	 * 
	 * @param n
	 *            the n
	 * @param d
	 *            the d
	 * @return the long
	 */
	public static long div_ceil(long n, long d) {
		long v = (long) ((n / d) + (n % d != 0 ? 1 : 0));
		return v;
	}

	/**
	 * Next_multiple.
	 * 
	 * @param n
	 *            the n
	 * @param k
	 *            the k
	 * @return the long
	 */
	public static long next_multiple(long n, long k) {
		long v = div_ceil(n, k);
		return (v * k);
	}

	/**
	 * Next_power_of_k.
	 * 
	 * @param n
	 *            the n
	 * @param k
	 *            the k
	 * @return the int
	 */
	public static int next_power_of_k(int n, int k) {
		int x = 0;
		if (n != 0) {
			x = (int) ((Math.log(n) / Math.log(k)) + 0.5);
		}

		if (Math.pow(k, x) < n) {
			return (int) (Math.pow(k, (x + 1)));
		} else {
			return (int) (Math.pow(k, x));
		}

	}

	/**
	 * Pad_size.
	 * 
	 * @param n
	 *            the n
	 * @param k
	 *            the k
	 * @return the int
	 */
	public static int pad_size(long n, int k) {
		if (n % k != 0) {
			return (int) (k - n % k);

		} else {
			return 0;
		}
	}

	/**
	 * Round_pow2.
	 * 
	 * @param x
	 *            the x
	 * @return the int
	 */
	public static int round_pow2(int x) {

		int ans = 1;
		while (ans < x) {
			ans *= 2;
		}
		return ans;
	}

	/**
	 * Log floor.
	 * 
	 * @param n
	 *            the n
	 * @param b
	 *            the b
	 * @return the int
	 */
	public static int logFloor(int n, int b) {
		int p = 1;
		int k = 0;
		while (p <= n) {
			p *= b;
			k++;
		}
		return k - 1;
	}
}
