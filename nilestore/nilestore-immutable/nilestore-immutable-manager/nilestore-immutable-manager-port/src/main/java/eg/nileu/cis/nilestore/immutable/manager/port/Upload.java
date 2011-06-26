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
package eg.nileu.cis.nilestore.immutable.manager.port;

import eg.nileu.cis.nilestore.common.Agent;
import eg.nileu.cis.nilestore.common.TaggedRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class Upload.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Upload extends TaggedRequest {

	/** The filename. */
	private final String filename;

	/** The filepath. */
	private final String filepath;

	/**
	 * Instantiates a new upload.
	 * 
	 * @param filepath
	 *            the filepath
	 * @param filename
	 *            the filename
	 * @param requestagent
	 *            the requestagent
	 */
	public Upload(String filepath, String filename, Agent requestagent) {
		super(requestagent);
		this.filepath = filepath;
		this.filename = filename;
	}

	/**
	 * Gets the filepath.
	 * 
	 * @return the filepath
	 */
	public String getFilepath() {
		return filepath;
	}

	/**
	 * Gets the filename.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
}
