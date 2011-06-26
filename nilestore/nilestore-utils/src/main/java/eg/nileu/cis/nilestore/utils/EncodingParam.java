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

import java.util.StringTokenizer;

// TODO: Auto-generated Javadoc
/**
 * The Class EncodingParam.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class EncodingParam {

	// FIXME: change types of K,N to byte as it's max value is limited by our
	// use of Galois field (8) which is 256 max
	// TODO: parse encoding params from string
	/** The DEFAUL t_ segmen t_ ma x_ size. */
	private final int DEFAULT_SEGMENT_MAX_SIZE = 128 * 1024;

	/** The DEFAUL t_k. */
	private final int DEFAULT_k = 3;

	/** The DEFAUL t_n. */
	private final int DEFAULT_n = 10;

	/** The k. */
	private int k;

	/** The n. */
	private int n;

	/** The segment size. */
	private long segmentSize;

	/**
	 * Instantiates a new encoding param.
	 */
	public EncodingParam() {
		k = DEFAULT_k;
		n = DEFAULT_n;
		segmentSize = DEFAULT_SEGMENT_MAX_SIZE;
	}

	/**
	 * Instantiates a new encoding param.
	 * 
	 * @param k
	 *            the k
	 * @param n
	 *            the n
	 * @param segmentSize
	 *            the segment size
	 */
	public EncodingParam(int k, int n, long segmentSize) {
		this.k = k;
		this.n = n;
		this.segmentSize = segmentSize;
	}

	/**
	 * Instantiates a new encoding param.
	 * 
	 * @param k
	 *            the k
	 * @param n
	 *            the n
	 */
	public EncodingParam(int k, int n) {
		this.k = k;
		this.n = n;
		this.segmentSize = DEFAULT_SEGMENT_MAX_SIZE;
	}

	/**
	 * Instantiates a new encoding param.
	 * 
	 * @param seralizedParams
	 *            the seralized params
	 */
	public EncodingParam(String seralizedParams) {
		StringTokenizer token = new StringTokenizer(seralizedParams, "-");
		segmentSize = Integer.valueOf(token.nextToken());
		k = Integer.valueOf(token.nextToken());
		n = Integer.valueOf(token.nextToken());
	}

	/**
	 * Sets the k.
	 * 
	 * @param k
	 *            the new k
	 */
	public void setK(int k) {
		this.k = k;
	}

	/**
	 * Gets the k.
	 * 
	 * @return the k
	 */
	public int getK() {
		return k;
	}

	/**
	 * Sets the n.
	 * 
	 * @param n
	 *            the new n
	 */
	public void setN(int n) {
		this.n = n;
	}

	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public int getN() {
		return n;
	}

	/**
	 * Sets the segment size.
	 * 
	 * @param segmentSize
	 *            the new segment size
	 */
	public void setSegmentSize(int segmentSize) {
		this.segmentSize = segmentSize;
	}

	/**
	 * Gets the segment size.
	 * 
	 * @return the segment size
	 */
	public long getSegmentSize() {
		return segmentSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%d-%d-%d", segmentSize, k, n);
	}
}
