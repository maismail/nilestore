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
package eg.nileu.cis.nilestore.immutable.uploader;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.SecretHolder;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.interfaces.file.IUploadable;

// TODO: Auto-generated Javadoc
/**
 * The Class NsUploaderInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsUploaderInit extends Init {

	/** The self. */
	private final NilestoreAddress self;

	/** The uploadable. */
	private final IUploadable uploadable;

	/** The secret holder. */
	private final SecretHolder secretHolder;

	/**
	 * Instantiates a new ns uploader init.
	 * 
	 * @param self
	 *            the self
	 * @param uploadable
	 *            the uploadable
	 * @param secretholder
	 *            the secretholder
	 */
	public NsUploaderInit(NilestoreAddress self, IUploadable uploadable,
			SecretHolder secretholder) {
		this.self = self;
		this.uploadable = uploadable;
		this.secretHolder = secretholder;
	}

	/**
	 * Gets the uploadable.
	 * 
	 * @return the uploadable
	 */
	public IUploadable getUploadable() {
		return uploadable;
	}

	/**
	 * Gets the secret holder.
	 * 
	 * @return the secret holder
	 */
	public SecretHolder getSecretHolder() {
		return secretHolder;
	}

	/**
	 * Gets the self.
	 * 
	 * @return the self
	 */
	public NilestoreAddress getSelf() {
		return self;
	}
}
