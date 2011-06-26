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
package eg.nileu.cis.nilestore.immutable.common;

import se.sics.kompics.ChannelFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class BucketProxyDestinationFilter.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public final class BucketProxyDestinationFilter extends
		ChannelFilter<PutGetData, String> {

	/**
	 * Instantiates a new bucket proxy destination filter.
	 * 
	 * @param destid
	 *            the destid
	 */
	public BucketProxyDestinationFilter(String destid) {
		super(PutGetData.class, destid, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see se.sics.kompics.ChannelFilter#getValue(se.sics.kompics.Event)
	 */
	@Override
	public String getValue(PutGetData event) {
		return event.getDestID();
	}
}