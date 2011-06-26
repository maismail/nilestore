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
package eg.nileu.cis.nilestore.immutable.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Random;

import eg.nileu.cis.nilestore.cryptography.SHA256d;
import eg.nileu.cis.nilestore.interfaces.file.IUploadable;
import eg.nileu.cis.nilestore.interfaces.file.UploadResults;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.MathUtils;
import eg.nileu.cis.nilestore.utils.hashutils.Hash;
import eg.nileu.cis.nilestore.utils.hashutils.Hasher;
import eg.nileu.cis.nilestore.utils.hashutils.Tags;

// TODO: Auto-generated Javadoc
//TODO: implement it as a component instead 
/**
 * The Class FileHandle.
 * 
 * @author Mahmoud Ismail <mahmoudahmedismail@gmail.com>
 */
public class FileHandle implements IUploadable {

	/** The _filehandle. */
	private RandomAccessFile _filehandle;

	/** The _convergence. */
	private byte[] _convergence;

	/** The _encrypt key. */
	private byte[] _encryptKey;

	/** The _params. */
	private EncodingParam _params;

	/** The upload results. */
	private UploadResults uploadResults;

	/**
	 * Instantiates a new file handle.
	 * 
	 * @param filename
	 *            the filename
	 * @param convergence_secret
	 *            the convergence_secret
	 * @param params
	 *            the params
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public FileHandle(String filename, byte[] convergence_secret,
			EncodingParam params) throws IOException {
		_filehandle = new RandomAccessFile(filename, "r");
		_convergence = convergence_secret;
		_encryptKey = null;

		if (params == null) {
			params = new EncodingParam();
		}
		setEncodingParams(params);
		uploadResults = new UploadResults();
	}

	/**
	 * Gets the encryptionkey_convergent.
	 * 
	 * @return the encryptionkey_convergent
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private byte[] getEncryptionkey_convergent() throws IOException {
		if (_encryptKey == null) {
			int BLOCK_SIZE = 64 * 1024;
			SHA256d _hasher = Hasher
					.getConvergencehasher(_params, _convergence);
			_filehandle.seek(0);

			byte[] data = new byte[BLOCK_SIZE];

			while (true) {
				int status = _filehandle.read(data);
				if (status == -1)
					break;
				byte[] tmp = Arrays.copyOf(data, status);
				_hasher.update(tmp);
			}

			_filehandle.seek(0);
			_encryptKey = _hasher.digest();
		}
		return _encryptKey;
	}

	/**
	 * Gets the encryptionkey_random.
	 * 
	 * @return the encryptionkey_random
	 */
	private byte[] getEncryptionkey_random() {
		if (_encryptKey == null) {
			Random rand = new Random();
			_encryptKey = new byte[Tags.keylen];
			rand.nextBytes(_encryptKey);
		}
		return _encryptKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.interfaces.file.IUploadable#getEncryptionkey()
	 */
	@Override
	public byte[] getEncryptionkey() throws IOException {
		if (_convergence != null) {
			return getEncryptionkey_convergent();
		} else {
			return getEncryptionkey_random();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.file.IUploadable#read(byte[])
	 */
	@Override
	public int read(byte[] data) throws IOException {
		return _filehandle.read(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.file.IUploadable#length()
	 */
	@Override
	public long length() throws IOException {
		return _filehandle.length();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.file.IUploadable#pos()
	 */
	@Override
	public long pos() throws IOException {
		return _filehandle.getChannel().position();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.interfaces.file.IUploadable#getEncodingParams()
	 */
	@Override
	public EncodingParam getEncodingParams() {
		return _params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.file.IUploadable#close()
	 */
	@Override
	public void close() throws IOException {
		_filehandle.close();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.interfaces.file.IUploadable#setEncodingParams(
	 * eg.nileu.cis.nilestore.utils.EncodingParam)
	 */
	@Override
	public void setEncodingParams(EncodingParam params) throws IOException {
		_params = params;
		long filesize = _filehandle.length();
		long segmentsize = Math.min(filesize, _params.getSegmentSize());
		segmentsize = MathUtils.next_multiple(segmentsize, _params.getK());
		_params.setSegmentSize((int) segmentsize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eg.nileu.cis.nilestore.interfaces.file.IUploadable#getStorageIndex()
	 */
	public byte[] getStorageIndex() throws IOException {
		byte[] SI;
		if (_encryptKey == null) {
			_encryptKey = getEncryptionkey();
		}
		SI = Hash.storage_index_hash(_encryptKey);
		return SI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eg.nileu.cis.nilestore.interfaces.file.IUploadable#getUploadResults()
	 */
	public UploadResults getUploadResults() {
		return uploadResults;
	}
}
