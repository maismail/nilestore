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
package eg.nileu.cis.nilestore.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class FileUtils.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class FileUtils {

	/** The Constant newLine. */
	public static final String newLine = System.getProperty("line.separator");

	/**
	 * Gets the free space.
	 * 
	 * @param dir
	 *            the dir
	 * @return the free space
	 */
	public static long getFreeSpace(String dir) {
		File f = new File(dir);
		return f.getFreeSpace();
	}

	/**
	 * Gets the size.
	 * 
	 * @param file
	 *            the file
	 * @return the size
	 */
	public static long getSize(String file) {
		File f = new File(file);
		return f.length();
	}

	/**
	 * Join path.
	 * 
	 * @param s1
	 *            the s1
	 * @param s2
	 *            the s2
	 * @return the string
	 */
	public static String JoinPath(String s1, String s2) {
		return s1 + File.separator + s2;
	}

	/**
	 * Join path.
	 * 
	 * @param s1
	 *            the s1
	 * @param s2
	 *            the s2
	 * @param s3
	 *            the s3
	 * @return the string
	 */
	public static String JoinPath(String s1, String s2, String s3) {
		String s = JoinPath(s1, s2);
		return JoinPath(s, s3);
	}

	/**
	 * Gets the abs path.
	 * 
	 * @param dir
	 *            the dir
	 * @return the abs path
	 */
	public static String getAbsPath(String dir) {
		File f = new File(dir);
		return f.getAbsolutePath();
	}

	/**
	 * Mkdirs.
	 * 
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	public static boolean mkdirs(String s) {
		File f = new File(s);
		return f.mkdirs();
	}

	/**
	 * Exists.
	 * 
	 * @param s
	 *            the s
	 * @return true, if successful
	 */
	public static boolean exists(String s) {
		if (s == null) {
			return false;
		}
		File f = new File(s);
		return f.exists();
	}

	/**
	 * Mkdirsifnot exists.
	 * 
	 * @param s
	 *            the s
	 */
	public static void mkdirsifnotExists(String s) {
		if (s == null) {
			return;
		}
		File f = new File(s);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	/**
	 * Mvfile.
	 * 
	 * @param src
	 *            the src
	 * @param dest
	 *            the dest
	 * @return true, if successful
	 */
	public static boolean mvfile(String src, String dest) {
		File f = new File(src);
		return f.renameTo(new File(dest));
	}

	/**
	 * Rmdir2.
	 * 
	 * @param dir
	 *            the dir
	 * @param levels
	 *            the levels
	 */
	public static void rmdir2(String dir, int levels) {
		File f = new File(dir);
		f.delete();
		for (int i = 1; i < levels; i++) {
			f = f.getParentFile();
			f.delete();
		}
	}

	/**
	 * Rmdir.
	 * 
	 * @param dir
	 *            the dir
	 * @param levels
	 *            the levels
	 */
	public static void rmdir(String dir, int levels) {
		File f = new File(dir);
		File[] children = f.listFiles();
		for (File child : children) {
			child.delete();
		}
		f.delete();
		for (int i = 1; i < levels; i++) {
			f = f.getParentFile();
			f.delete();
		}
	}

	/**
	 * Rmdir.
	 * 
	 * @param dir
	 *            the dir
	 * @param includeParent
	 *            the include parent
	 */
	public static void rmdir(String dir, boolean includeParent) {
		File f = new File(dir);
		if (f.exists()) {
			for (File child : f.listFiles()) {
				if (child.isDirectory()) {
					rmdir(child.getAbsolutePath());
				}
				child.delete();
			}
			if (includeParent) {
				f.delete();
			}
		}
	}

	/**
	 * Rmdir.
	 * 
	 * @param dir
	 *            the dir
	 */
	public static void rmdir(String dir) {
		rmdir(dir, true);
	}

	/**
	 * Write line.
	 * 
	 * @param line
	 *            the line
	 * @param filepath
	 *            the filepath
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void writeLine(String line, String filepath)
			throws IOException {
		FileWriter writer = new FileWriter(filepath);
		writer.write(line);
		writer.write(newLine);
		writer.close();
	}

	/**
	 * Read line.
	 * 
	 * @param filepath
	 *            the filepath
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String readLine(String filepath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line = reader.readLine();
		reader.close();
		return line;
	}
}
