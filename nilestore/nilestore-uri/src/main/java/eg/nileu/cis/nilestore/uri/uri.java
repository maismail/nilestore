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
package eg.nileu.cis.nilestore.uri;

import eg.nileu.cis.nilestore.interfaces.uri.IURI;

// TODO: Auto-generated Javadoc
/**
 * The Class uri.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class uri {

	/** The Constant BASE32_CHARS. */
	public static final String BASE32_CHARS = "([a-z2-7]+)";

	/** The Constant NUMBERS. */
	public static final String NUMBERS = "([0-9]+)";

	/**
	 * From_string.
	 * 
	 * @param cap
	 *            the cap
	 * @return the iURI
	 * @throws BadURIException
	 *             the bad uri exception
	 */
	public static IURI from_string(String cap) throws BadURIException {
		if (cap.startsWith(CHKFileURI.BASE_STRING)) {
			return new CHKFileURI(cap);
		} else {
			return null;
		}
	}
}
