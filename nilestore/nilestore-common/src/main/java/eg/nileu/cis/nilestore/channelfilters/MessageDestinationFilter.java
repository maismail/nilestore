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
package eg.nileu.cis.nilestore.channelfilters;

import se.sics.kompics.ChannelFilter;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;

// TODO: Auto-generated Javadoc
/**
 * The Class MessageDestinationFilter.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class MessageDestinationFilter extends
		ChannelFilter<Message, Address> {

	/**
	 * Instantiates a new message destination filter.
	 * 
	 * @param address
	 *            the address
	 */
	public MessageDestinationFilter(Address address) {
		super(Message.class, address, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.sics.kompics.ChannelFilter#getValue(se.sics.kompics.Event)
	 */
	@Override
	public Address getValue(Message event) {
		return event.getDestination();
	}
}