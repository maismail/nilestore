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
package eg.nileu.cis.nilestore.storage.immutable.reader;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.common.ComponentAddress;
import eg.nileu.cis.nilestore.storage.AbstractStorageServer;

// TODO: Auto-generated Javadoc
/**
 * The Class NsBucketReaderInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsBucketReaderInit extends Init {

	/** The self. */
	private final ComponentAddress self;

	/** The dest. */
	private final ComponentAddress dest;

	/** The filepath. */
	private final String filepath;

	/** The filename. */
	private final String filename;
	// TODO: check if we can use events instead or not
	/** The parent. */
	private final AbstractStorageServer parent;

	/**
	 * Instantiates a new ns bucket reader init.
	 * 
	 * @param parent
	 *            the parent
	 * @param self
	 *            the self
	 * @param dest
	 *            the dest
	 * @param filepath
	 *            the filepath
	 * @param filename
	 *            the filename
	 */
	public NsBucketReaderInit(AbstractStorageServer parent,
			ComponentAddress self, ComponentAddress dest, String filepath,
			String filename) {
		this.parent = parent;
		this.self = self;
		this.dest = dest;
		this.filepath = filepath;
		this.filename = filename;
	}

	/**
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
	public AbstractStorageServer getParent() {
		return parent;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public ComponentAddress getSelf() {
		return self;
	}

	/**
	 * Gets the dest.
	 * 
	 * @return the dest
	 */
	public ComponentAddress getDest() {
		return dest;
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
