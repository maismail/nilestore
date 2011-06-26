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
package eg.nileu.cis.nilestore.connectionfd.port;

import se.sics.kompics.Event;

// TODO: Auto-generated Javadoc
/**
 * The Class CancelNotifyonFailure.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CancelNotifyonFailure extends Event {

	/** The request. */
	private final NotifyonFailure request;

	/**
	 * Instantiates a new cancel notifyon failure.
	 * 
	 * @param request
	 *            the request
	 */
	public CancelNotifyonFailure(NotifyonFailure request) {
		this.request = request;
	}

	/**
	 * Gets the request.
	 * 
	 * @return the request
	 */
	public NotifyonFailure getRequest() {
		return request;
	}
}
