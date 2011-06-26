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
package eg.nileu.cis.nilestore.storage.immutable;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import eg.nileu.cis.nilestore.storage.LeaseInfo;
import eg.nileu.cis.nilestore.utils.FileUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class ImmutableShareFile.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class ImmutableShareFile {

	/** The Constant HEADER_SIZE. */
	public static final int HEADER_SIZE = 9 * 4; // ">LLLLLLLLL"

	/** The LEAS e_ size. */
	private final int LEASE_SIZE = 72; // ">L32s32sL"

	/** The _num_leases. */
	private int _num_leases;

	/** The _lease_offset. */
	private final long _lease_offset;

	/** The _data_offset. */
	private final byte _data_offset;

	/** The _max_size. */
	private final long _max_size;

	/** The home. */
	private final String home;

	// TODO: change to sharefile2
	// TODO: add_or_renew_leases method

	/**
	 * Instantiates a new immutable share file.
	 * 
	 * @param filename
	 *            the filename
	 * @param max_size
	 *            the max_size
	 * @param create
	 *            the create
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws UnkownImmutableContainerVersionError
	 *             the unkown immutable container version error
	 */
	public ImmutableShareFile(String filename, long max_size, boolean create)
			throws IOException, UnkownImmutableContainerVersionError {
		_max_size = max_size;
		home = filename;

		if (create) {

			RandomAccessFile f = new RandomAccessFile(home, "rw");

			ByteBuffer buffer = ByteBuffer.allocate(12);
			buffer.putInt(1);
			buffer.putInt((int) Math.min(max_size, (Math.pow(2, 32) - 1)));
			buffer.putInt(0);

			f.write(buffer.array());

			_num_leases = 0;
			_lease_offset = max_size + 0x0c;
			f.close();
		} else {
			RandomAccessFile f = new RandomAccessFile(home, "r");
			byte[] b = new byte[12];
			f.read(b);
			f.close();

			ByteBuffer buffer = ByteBuffer.wrap(b);
			int version = buffer.getInt();
			if (version != 1) {
				throw new UnkownImmutableContainerVersionError(String.format(
						"sharefile %s had version %d but we wanted 1",
						filename, version));
			}
			_num_leases = buffer.getInt(2);
			long filesize = FileUtils.getSize(filename);
			_lease_offset = filesize - (_num_leases * LEASE_SIZE);
		}

		_data_offset = 0x0c;
	}

	/**
	 * Read_share_data.
	 * 
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public byte[] read_share_data(int offset, int length) throws IOException {
		long seekpos = _data_offset + offset;
		long fsize = FileUtils.getSize(home);
		long actuallength = Math.max(0, Math.min(length, fsize - seekpos));
		if (actuallength == 0) {
			return null;
		}
		RandomAccessFile f = new RandomAccessFile(home, "r");
		f.seek(seekpos);
		byte[] data = new byte[length];
		f.read(data);
		f.close();
		return data;
	}

	/**
	 * Write_share_data.
	 * 
	 * @param offset
	 *            the offset
	 * @param data
	 *            the data
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void write_share_data(long offset, byte[] data) throws IOException {
		int length = data.length;
		if (_max_size != 0 && (offset + length) > _max_size) {
			// TODO: throws DataTooLargeError
		}

		RandomAccessFile f = new RandomAccessFile(home, "rw");
		long real_offset = _data_offset + offset;
		f.seek(real_offset);
		f.write(data);
		f.close();
	}

	/**
	 * Add_lease.
	 * 
	 * @param _info
	 *            the _info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void add_lease(LeaseInfo _info) throws IOException {
		RandomAccessFile f = new RandomAccessFile(home, "rw");
		_num_leases = read_num_leases(f);
		write_lease_record(f, _info);
		write_num_leases(f);
		f.close();
	}

	/**
	 * Read_num_leases.
	 * 
	 * @param f
	 *            the f
	 * @return the int
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private int read_num_leases(RandomAccessFile f) throws IOException {
		f.seek(0x08);
		return f.readInt();
	}

	/**
	 * Write_num_leases.
	 * 
	 * @param f
	 *            the f
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void write_num_leases(RandomAccessFile f) throws IOException {
		f.seek(0x08);
		_num_leases++;
		f.writeInt(_num_leases);
	}

	/**
	 * Write_lease_record.
	 * 
	 * @param f
	 *            the f
	 * @param info
	 *            the info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void write_lease_record(RandomAccessFile f, LeaseInfo info)
			throws IOException {
		long offset = _lease_offset + _num_leases * LEASE_SIZE;
		f.seek(offset);
		ByteBuffer buffer = info.to_immutable_data();
		f.write(buffer.array());
	}
}
