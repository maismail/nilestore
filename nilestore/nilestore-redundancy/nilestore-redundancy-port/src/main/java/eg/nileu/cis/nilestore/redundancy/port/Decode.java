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
package eg.nileu.cis.nilestore.redundancy.port;

import se.sics.kompics.Request;
import eg.nileu.cis.nilestore.utils.ByteArray;

// TODO: Auto-generated Javadoc
/**
 * The Class Decode.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Decode extends Request {

	/** The chunks. */
	private final ByteArray[] chunks;

	/** The indeces. */
	private final int[] indeces;

	/** The K. */
	private final int K;

	/** The N. */
	private final int N;

	/**
	 * Instantiates a new decode.
	 * 
	 * @param K
	 *            the k
	 * @param N
	 *            the n
	 * @param chunks
	 *            the chunks
	 * @param indeces
	 *            the indeces
	 */
	public Decode(int K, int N, ByteArray[] chunks, int[] indeces) {
		this.K = K;
		this.N = N;
		this.chunks = chunks;
		this.indeces = indeces;
	}

	/**
	 * Gets the indeces.
	 * 
	 * @return the indeces
	 */
	public int[] getIndeces() {
		return indeces;
	}

	/**
	 * Gets the chunks.
	 * 
	 * @return the chunks
	 */
	public ByteArray[] getChunks() {
		return chunks;
	}

	/**
	 * Gets the k.
	 * 
	 * @return the k
	 */
	public int getK() {
		return K;
	}

	/**
	 * Gets the n.
	 * 
	 * @return the n
	 */
	public int getN() {
		return N;
	}

	/**
	 * Gets the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return chunks[0].getLength();
	}

	/**
	 * Gets the replication parameters.
	 * 
	 * @return the replication parameters
	 */
	public RedundancyParameters getReplicationParameters() {
		return new RedundancyParameters(K, N);
	}

}
