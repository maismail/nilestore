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
package eg.nileu.cis.nilestore.immutable.manager;

import se.sics.kompics.Init;
import eg.nileu.cis.nilestore.SecretHolder;
import eg.nileu.cis.nilestore.common.NilestoreAddress;
import eg.nileu.cis.nilestore.utils.EncodingParam;

// TODO: Auto-generated Javadoc
/**
 * The Class NsImmutableManagerInit.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class NsImmutableManagerInit extends Init {

	/** The self. */
	private final NilestoreAddress self;

	/** The secret holder. */
	private final SecretHolder secretHolder;

	/** The home dir. */
	private final String homeDir;

	/** The encoding param. */
	private final EncodingParam encodingParam;

	/**
	 * Instantiates a new ns immutable manager init.
	 * 
	 * @param self
	 *            the self
	 * @param secretHolder
	 *            the secret holder
	 * @param homeDir
	 *            the home dir
	 * @param encodingParam
	 *            the encoding param
	 */
	public NsImmutableManagerInit(NilestoreAddress self,
			SecretHolder secretHolder, String homeDir,
			EncodingParam encodingParam) {
		this.self = self;
		this.secretHolder = secretHolder;
		this.homeDir = homeDir;
		this.encodingParam = encodingParam;
	}

	/**
	 * Gets the encoding param.
	 * 
	 * @return the encoding param
	 */
	public EncodingParam getEncodingParam() {
		return encodingParam;
	}

	/**
	 * Gets the home dir.
	 * 
	 * @return the home dir
	 */
	public String getHomeDir() {
		return homeDir;
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
