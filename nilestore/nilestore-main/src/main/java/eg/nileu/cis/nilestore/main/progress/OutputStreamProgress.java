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
package eg.nileu.cis.nilestore.main.progress;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class OutputStreamProgress.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class OutputStreamProgress extends FilterOutputStream{
	
	/** The listener. */
	private final ProgressListener listener;
	
	/** The count. */
	private long count;
	
	/** The firstbyte timestamp. */
	private long firstbyteTimestamp;
	
	/**
	 * Instantiates a new output stream progress.
	 * 
	 * @param out
	 *            the out
	 * @param listener
	 *            the listener
	 */
	public OutputStreamProgress(OutputStream out,ProgressListener listener) {
		super(out);
		this.listener = listener;
		this.count=0;
		this.firstbyteTimestamp=0;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[], int, int)
	 */
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		count+=len;
		listener.transfered(count,getRate(len));
		out.write(b, off, len);
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(byte[])
	 */
	@Override
	public void write(byte[] b) throws IOException {
		count+=b.length;
		listener.transfered(count,getRate(b.length));
		out.write(b);
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilterOutputStream#write(int)
	 */
	@Override
	public void write(int b) throws IOException {
		count++;
		listener.transfered(count,getRate(1));
		out.write(b);
	}
	
	/**
	 * Gets the rate.
	 * 
	 * @param bytes
	 *            the bytes
	 * @return the rate
	 */
	private float getRate(long bytes){
		if(firstbyteTimestamp == 0){
			firstbyteTimestamp=System.nanoTime();
			return 0;
		}
		float elapsed = System.nanoTime() - firstbyteTimestamp;
		float rate = (float) ((bytes/1024.0) / (elapsed * 1e-9));
		firstbyteTimestamp=System.nanoTime();
		return rate;
	}
}