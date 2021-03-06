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
package eg.nileu.cis.nilestore.immutable.uploader.port;

import se.sics.kompics.Event;
import eg.nileu.cis.nilestore.common.StatusMsg;
import eg.nileu.cis.nilestore.interfaces.file.UploadResults;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadingDone.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class UploadingDone extends Event {

	/** The SI. */
	private final String SI;

	/** The status. */
	private final StatusMsg status;

	/** The upload results. */
	private final UploadResults uploadResults;

	/**
	 * Instantiates a new uploading done.
	 * 
	 * @param SI
	 *            the sI
	 * @param status
	 *            the status
	 * @param uploadResults
	 *            the upload results
	 */
	public UploadingDone(String SI, StatusMsg status,
			UploadResults uploadResults) {
		this.SI = SI;
		this.status = status;
		this.uploadResults = uploadResults;
	}

	/**
	 * Gets the storage index.
	 * 
	 * @return the storage index
	 */
	public String getStorageIndex() {
		return SI;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public StatusMsg getStatus() {
		return status;
	}

	/**
	 * Gets the upload results.
	 * 
	 * @return the upload results
	 */
	public UploadResults getUploadResults() {
		return uploadResults;
	}

}
