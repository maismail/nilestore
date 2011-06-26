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

// TODO: Auto-generated Javadoc
/**
 * The Class Tuple.
 * 
 * @param <Tl>
 *            the generic type of the left object
 * @param <Tr>
 *            the generic type of the right object
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class Tuple<Tl, Tr> {

	/** The left object. */
	private Tl left;

	/** The right object. */
	private Tr right;

	/**
	 * Instantiates a new tuple.
	 * 
	 * @param left
	 *            the left object
	 * @param right
	 *            the right object
	 */
	public Tuple(Tl left, Tr right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Instantiates a new tuple.
	 */
	public Tuple() {

	}

	/**
	 * Gets the left.
	 * 
	 * @return the left
	 */
	public Tl getLeft() {
		return left;
	}

	/**
	 * Sets the left.
	 * 
	 * @param left
	 *            the new left
	 */
	public void setLeft(Tl left) {
		this.left = left;
	}

	/**
	 * Gets the right.
	 * 
	 * @return the right
	 */
	public Tr getRight() {
		return right;
	}

	/**
	 * Sets the right.
	 * 
	 * @param right
	 *            the new right
	 */
	public void setRight(Tr right) {
		this.right = right;
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
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<Tl, Tr> other = (Tuple<Tl, Tr>) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + left + ", " + right + ")";
	}

}
