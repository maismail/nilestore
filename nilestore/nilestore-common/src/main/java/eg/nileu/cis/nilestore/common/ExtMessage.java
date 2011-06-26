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
package eg.nileu.cis.nilestore.common;

import se.sics.kompics.network.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class ExtMessage.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ExtMessage extends Message {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 860486573289277757L;

	/** The dest id. */
	private final String destId;

	/** The src id. */
	private final String srcId;

	/**
	 * Instantiates a new ext message.
	 * 
	 * @param source
	 *            the source
	 * @param destination
	 *            the destination
	 */
	public ExtMessage(ComponentAddress source, ComponentAddress destination) {
		super(source.getAddress(), destination.getAddress());
		this.srcId = source.getId();
		this.destId = destination.getId();
	}

	/**
	 * Gets the dest id.
	 * 
	 * @return the dest id
	 */
	public String getDestId() {
		return destId;
	}

	/**
	 * Gets the src id.
	 * 
	 * @return the src id
	 */
	public String getSrcId() {
		return srcId;
	}
}
