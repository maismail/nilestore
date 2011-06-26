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

// TODO: Auto-generated Javadoc
/**
 * The Class StatusMsg.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class StatusMsg {

	/** The status. */
	private final Status status;

	/** The message. */
	private final String message;

	/**
	 * Instantiates a new status msg.
	 * 
	 * @param status
	 *            the status
	 * @param message
	 *            the message
	 */
	public StatusMsg(Status status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Checks if is succeeded.
	 * 
	 * @return true, if is succeeded
	 */
	public boolean isSucceeded() {
		return status.equals(Status.Succeeded);
	}

	/**
	 * Gets the message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StatusMsg [status=" + status + ", message=" + message + "]";
	}

}
