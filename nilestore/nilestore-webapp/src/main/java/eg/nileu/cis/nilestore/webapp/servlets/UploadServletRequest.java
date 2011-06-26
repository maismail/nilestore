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
package eg.nileu.cis.nilestore.webapp.servlets;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import eg.nileu.cis.nilestore.webserver.port.ServletRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadServletRequest.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class UploadServletRequest extends ServletRequest {

	/** The file name. */
	private String fileName;

	/** The file path. */
	private String filePath;

	/** The return type. */
	private String returnType = "html";

	/** The is file found. */
	private boolean isFileFound;

	/**
	 * Instantiates a new upload servlet request.
	 * 
	 * @param request
	 *            the request
	 * @throws Exception
	 *             the exception
	 */
	public UploadServletRequest(HttpServletRequest request) throws Exception {
		super(request);
		init_ajaxUpload();
	}

	/**
	 * Checks if is file founded.
	 * 
	 * @return true, if is file founded
	 */
	public boolean isFileFounded() {
		return isFileFound;
	}

	/**
	 * Gets the file name.
	 * 
	 * @return the file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Gets the file path.
	 * 
	 * @return the file path
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * Checks if is file upload request.
	 * 
	 * @param request
	 *            the request
	 * @return true, if is file upload request
	 */
	public static boolean isFileUploadRequest(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}

	/**
	 * Init_ajax upload.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@SuppressWarnings("unchecked")
	private void init_ajaxUpload() throws Exception {

		ServletFileUpload uploadHandler = new ServletFileUpload(
				new DiskFileItemFactory());
		List<FileItem> items = uploadHandler.parseRequest(request);
		for (FileItem item : items) {
			if (!item.isFormField()) {
				File f = File.createTempFile("upfile", ".tmp");
				f.deleteOnExit();
				item.write(f);
				fileName = item.getName();
				if (fileName.contains(File.separator)) {
					fileName = fileName.substring(fileName
							.lastIndexOf(File.separator) + 1);
				}
				filePath = f.getAbsolutePath();
				isFileFound = true;
				// Here we handle only one file at once
				break;
			} else {
				if (item.getFieldName().equals(DEST_FIELD_NAME)) {
					dest = Integer.valueOf(item.getString());
				}
			}
		}
		String t = getParameter("t");
		if (t != null) {
			returnType = t;
		}
	}

	/**
	 * Gets the return type.
	 * 
	 * @return the return type
	 */
	public String getReturnType() {
		return returnType;
	}
}
