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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.bitpedia.util.Base32;

import eg.nileu.cis.nilestore.uri.BadURIException;
import eg.nileu.cis.nilestore.uri.CHKFileURI;
import eg.nileu.cis.nilestore.utils.DumpUtils;
import eg.nileu.cis.nilestore.utils.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class CapsStore.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class CapsStore {

	/**
	 * The Class CapsStoreItem.
	 * 
	 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
	 */
	class CapsStoreItem {

		/** The filename. */
		private final String filename;

		/** The cap. */
		private final CHKFileURI cap;

		/** The upload date. */
		private final Date uploadDate;

		/**
		 * Instantiates a new caps store item.
		 * 
		 * @param filename
		 *            the filename
		 * @param cap
		 *            the cap
		 * @param uploadDate
		 *            the upload date
		 */
		public CapsStoreItem(String filename, CHKFileURI cap, Date uploadDate) {
			this.filename = filename;
			this.cap = cap;
			this.uploadDate = uploadDate;
		}

		/**
		 * Instantiates a new caps store item.
		 * 
		 * @param filename
		 *            the filename
		 * @param cap
		 *            the cap
		 * @param uploadDate
		 *            the upload date
		 * @throws BadURIException
		 *             the bad uri exception
		 */
		public CapsStoreItem(String filename, String cap, long uploadDate)
				throws BadURIException {
			this.filename = filename;
			this.cap = new CHKFileURI(cap);
			this.uploadDate = new Date(uploadDate);
		}

		/**
		 * Gets the filename.
		 * 
		 * @return the filename
		 */
		public String getFilename() {
			return filename;
		}

		/**
		 * Gets the cap.
		 * 
		 * @return the cap
		 */
		public CHKFileURI getCap() {
			return cap;
		}

		/**
		 * Gets the upload date.
		 * 
		 * @return the upload date
		 */
		public Date getUploadDate() {
			return uploadDate;
		}

		/*
		 * public String getUploadDateString() { return uploadDate.toString(); }
		 */
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {

			return filename + "," + cap.toString() + "," + uploadDate.getTime();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((cap == null) ? 0 : cap.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CapsStoreItem other = (CapsStoreItem) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (cap == null) {
				if (other.cap != null)
					return false;
			} else if (!cap.equals(other.cap))
				return false;
			return true;
		}

		/**
		 * Gets the outer type.
		 * 
		 * @return the outer type
		 */
		private CapsStore getOuterType() {
			return CapsStore.this;
		}
	}

	/** The cap store items. */
	private final Set<CapsStoreItem> capStoreItems;

	/** The cap store file. */
	private final String capStoreFile = "cap.store";

	/** The path. */
	private final String path;

	/**
	 * Instantiates a new caps store.
	 * 
	 * @param homedir
	 *            the homedir
	 * @throws BadURIException
	 *             the bad uri exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public CapsStore(String homedir) throws BadURIException, IOException {
		path = FileUtils.JoinPath(homedir, capStoreFile);
		boolean append = FileUtils.exists(path);
		capStoreItems = new HashSet<CapsStore.CapsStoreItem>();

		if (append) {
			load();
		}
	}

	/**
	 * Adds the cap store item.
	 * 
	 * @param filename
	 *            the filename
	 * @param cap
	 *            the cap
	 * @param uploadDate
	 *            the upload date
	 */
	public void addCapStoreItem(String filename, CHKFileURI cap, Date uploadDate) {
		capStoreItems.add(new CapsStoreItem(filename, cap, uploadDate));
	}

	/**
	 * Adds the cap store item.
	 * 
	 * @param filename
	 *            the filename
	 * @param cap
	 *            the cap
	 * @param uploadDate
	 *            the upload date
	 * @throws BadURIException
	 *             the bad uri exception
	 */
	public void addCapStoreItem(String filename, String cap, long uploadDate)
			throws BadURIException {
		capStoreItems.add(new CapsStoreItem(filename, cap, uploadDate));
	}

	/**
	 * Dumpto html.
	 * 
	 * @param dest
	 *            the dest
	 * @return the string
	 */
	public String dumptoHtml(String dest) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"ft-table\">");
		sb.append("<thead class=\"ft-thead\"> <tr><th>FileName</th><th>Storage Index</th><th>View</th><th>Save</th><th>Created</th></tr> </thead> <tbody class=\"ft-tbody\">");
		String row = "<tr><td>%s</td> <td>%s</td> <td>%s</td> <td>%s</td> <td>%s</td></tr>";

		for (CapsStoreItem item : capStoreItems) {
			String saveArg = dest.isEmpty() ? "?save=true" : "&save=true";
			String save = String.format(
					"<a href=%s target=\"_blank\"> save </a>", "/download/"
							+ item.getCap().toString() + dest + saveArg);
			String view = String.format(
					"<a href=%s target=\"_blank\"> view </a>", "/download/"
							+ item.getCap().toString() + dest);

			sb.append(String.format(row, item.getFilename(),
					Base32.encode(item.getCap().getStorageIndex()), view, save,
					DumpUtils.DateDifftoString(item.getUploadDate())));
		}
		sb.append("</tbody></table>");
		return sb.toString();
	}

	/**
	 * Save.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void Save() throws IOException {
		FileWriter writer = new FileWriter(path);
		for (CapsStoreItem item : capStoreItems) {
			writer.write(item.toString() + "\n");
		}
		writer.close();

	}

	/**
	 * Load.
	 * 
	 * @throws BadURIException
	 *             the bad uri exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void load() throws BadURIException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line;
		line = reader.readLine();
		while (line != null) {
			StringTokenizer tokenizer = new StringTokenizer(line.trim(), ",");
			String filename = tokenizer.nextToken();
			String cap = tokenizer.nextToken();
			String date = tokenizer.nextToken();

			addCapStoreItem(filename, cap, Long.valueOf(date));

			line = reader.readLine();
		}

		reader.close();
	}
}
