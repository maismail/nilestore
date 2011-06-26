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
package eg.nileu.cis.nilestore.webserver.servlets;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// TODO: Auto-generated Javadoc
/**
 * The Class ResourceServlet.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ResourceServlet extends HttpServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1081888906905781054L;

	/** The resource location. */
	private final String resourceLocation;

	/** The content type. */
	private final String contentType;

	/**
	 * Instantiates a new resource servlet.
	 * 
	 * @param resourceLocation
	 *            the resource location
	 * @param contentType
	 *            the content type
	 */
	public ResourceServlet(String resourceLocation, String contentType) {

		this.resourceLocation = resourceLocation;
		this.contentType = contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType(contentType);
		InputStream in = getClass().getResourceAsStream(resourceLocation);
		byte[] buffer = new byte[1024];
		int numbytes = in.read(buffer);
		while (numbytes != -1) {
			resp.getOutputStream().write(buffer, 0, numbytes);
			resp.getOutputStream().flush();
			numbytes = in.read(buffer);
		}
		in.close();
	}

}
