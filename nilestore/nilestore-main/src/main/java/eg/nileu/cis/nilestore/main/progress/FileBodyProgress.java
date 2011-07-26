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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.FileBody;

// TODO: Auto-generated Javadoc
/**
 * The Class FileBodyProgress.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class FileBodyProgress extends FileBody {
	
	/** The listener. */
	private final ProgressListener listener;
	
	/**
	 * Instantiates a new file body progress.
	 * 
	 * @param file
	 *            the file
	 * @param listener
	 *            the listener
	 */
	public FileBodyProgress(File file, ProgressListener listener) {
		super(file);
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.entity.mime.content.FileBody#writeTo(java.io.OutputStream)
	 */
	@Override
	public void writeTo(OutputStream out) throws IOException {
		OutputStreamProgress pout = new OutputStreamProgress(out, listener);
		super.writeTo(pout);
	}
	
}
