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
package eg.nileu.cis.nilestore.redundancy.onion;

import java.util.HashMap;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;

import com.onionnetworks.fec.Pure16Code;
import com.onionnetworks.fec.PureCode;
import com.onionnetworks.util.Buffer;

import eg.nileu.cis.nilestore.redundancy.port.Decode;
import eg.nileu.cis.nilestore.redundancy.port.DecodeResponse;
import eg.nileu.cis.nilestore.redundancy.port.Encode;
import eg.nileu.cis.nilestore.redundancy.port.EncodeResponse;
import eg.nileu.cis.nilestore.redundancy.port.Redundancy;
import eg.nileu.cis.nilestore.redundancy.port.RedundancyParameters;
import eg.nileu.cis.nilestore.utils.ByteArray;

// TODO: Auto-generated Javadoc
/**
 * The Class NsReedSolomonCodes.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsReedSolomonCodes extends ComponentDefinition {

	/** The replication port. */
	Negative<Redundancy> replicationPort = provides(Redundancy.class);

	/** The active coders. */
	private HashMap<RedundancyParameters, PureCode> activeCoders;

	/**
	 * Instantiates a new ns reed solomon codes.
	 */
	public NsReedSolomonCodes() {

		activeCoders = new HashMap<RedundancyParameters, PureCode>();
		subscribe(handleEncode, replicationPort);
		subscribe(handlDecode, replicationPort);
	}

	/** The handle encode. */
	Handler<Encode> handleEncode = new Handler<Encode>() {
		@Override
		public void handle(Encode event) {
			RedundancyParameters repParams = event.getReplicationParameters();
			PureCode encoder = get(repParams);
			if (encoder == null) {
				encoder = addCoder(repParams);
			}

			Buffer[] input = byteArray2BufferArray(event.getChunks());
			Buffer[] output = new Buffer[event.getN()];
			int[] indeces = new int[event.getN()];
			fillOutputBuffers(output, indeces, event.getSize());

			synchronized (encoder) {
				encoder.encode(input, output, indeces);
			}

			ByteArray[] out = bufferArray2ByteArray(output);
			trigger(new EncodeResponse(event, out), replicationPort);
		}
	};

	/** The handl decode. */
	Handler<Decode> handlDecode = new Handler<Decode>() {
		@Override
		public void handle(Decode event) {
			RedundancyParameters repParams = event.getReplicationParameters();
			PureCode decoder = get(repParams);
			if (decoder == null) {
				decoder = addCoder(repParams);
			}

			Buffer[] input = byteArray2BufferArray(event.getChunks());
			// TODO: check indeces
			int[] indeces = event.getIndeces();

			synchronized (decoder) {
				decoder.decode(input, indeces);
			}

			ByteArray[] out = bufferArray2ByteArray(input);
			trigger(new DecodeResponse(event, out), replicationPort);
		}
	};

	/**
	 * Gets the.
	 * 
	 * @param replicationParameters
	 *            the replication parameters
	 * @return the pure code
	 */
	private PureCode get(RedundancyParameters replicationParameters) {
		synchronized (activeCoders) {
			return activeCoders.get(replicationParameters);
		}
	}

	/**
	 * Adds the coder.
	 * 
	 * @param repParams
	 *            the rep params
	 * @return the pure code
	 */
	private PureCode addCoder(RedundancyParameters repParams) {
		PureCode coder = repParams.is16Bit() ? new Pure16Code(repParams.getK(),
				repParams.getN()) : new PureCode(repParams.getK(),
				repParams.getN());
		synchronized (activeCoders) {
			activeCoders.put(repParams, coder);
		}
		return coder;
	}

	/**
	 * Fill output buffers.
	 * 
	 * @param output
	 *            the output
	 * @param indeces
	 *            the indeces
	 * @param size
	 *            the size
	 */
	private void fillOutputBuffers(Buffer[] output, int[] indeces, int size) {
		for (int i = 0; i < output.length; i++) {
			output[i] = new Buffer(new byte[size]);
			indeces[i] = i;
		}
	}

	/**
	 * Byte array2 buffer array.
	 * 
	 * @param chunks
	 *            the chunks
	 * @return the buffer[]
	 */
	private Buffer[] byteArray2BufferArray(ByteArray[] chunks) {
		Buffer[] chunksBuffers = new Buffer[chunks.length];
		int size = chunks[0].getLength();
		for (int i = 0; i < chunks.length; i++) {
			assert chunks[i].getLength() == size;
			chunksBuffers[i] = new Buffer(chunks[i].getBytes());
		}
		return chunksBuffers;
	}

	/**
	 * Buffer array2 byte array.
	 * 
	 * @param buffers
	 *            the buffers
	 * @return the byte array[]
	 */
	private ByteArray[] bufferArray2ByteArray(Buffer[] buffers) {
		int size = buffers[0].len;
		int num = buffers.length;
		ByteArray[] bs = new ByteArray[num];
		for (int i = 0; i < num; i++) {
			assert buffers[i].len == size;
			bs[i] = new ByteArray(buffers[i].b);
		}
		return bs;
	}

}
