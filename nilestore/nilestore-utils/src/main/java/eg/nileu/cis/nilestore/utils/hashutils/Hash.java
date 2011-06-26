package eg.nileu.cis.nilestore.utils.hashutils;

import org.bouncycastle.crypto.digests.SHA1Digest;

import eg.nileu.cis.nilestore.cryptography.SHA256d;
import eg.nileu.cis.nilestore.utils.NetString;

public class Hash {

	public static final int HASH_SIZE = 32;

	public static byte[] storage_index_hash(byte[] key) {
		SHA256d _hasher = new SHA256d(Tags.keylen);
		_hasher.update(NetString.toNetString(Tags.STORAGE_INDEX_TAG));
		_hasher.update(key);
		return _hasher.digest();
	}

	public static byte[] block_hash(byte[] data) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.BLOCK_TAG));
		_hasher.update(data);
		return _hasher.digest();
	}

	public static byte[] empty_leaf_hash(int val) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.EMPTY_LEAF_HASH));
		_hasher.update(String.format("%d", val));
		return _hasher.digest();
	}

	public static byte[] pair_hash(byte[] a, byte[] b) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.PAIR_HASH_TAG));
		_hasher.update(NetString.toNetString(a));
		_hasher.update(NetString.toNetString(b));

		return _hasher.digest();
	}

	public static byte[] my_renewal_secret_hash(byte[] secret) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.CLIENT_RENEWAL_TAG));
		_hasher.update(secret);
		return _hasher.digest();
	}

	public static byte[] my_cancel_secret_hash(byte[] secret) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.CLIENT_CANCEL_TAG));
		_hasher.update(secret);
		return _hasher.digest();
	}

	public static byte[] file_renewal_secret(byte[] client_renewal_secret,
			byte[] storage_index) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.FILE_RENEWAL_TAG));
		_hasher.update(NetString.toNetString(client_renewal_secret));
		_hasher.update(NetString.toNetString(storage_index));

		return _hasher.digest();
	}

	public static byte[] file_cancel_secret(byte[] client_cancel_secret,
			byte[] storage_index) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.FILE_CANCEL_TAG));
		_hasher.update(NetString.toNetString(client_cancel_secret));
		_hasher.update(NetString.toNetString(storage_index));

		return _hasher.digest();
	}

	public static byte[] bucket_renewal_secret(byte[] file_renewal_secret,
			byte[] peerid) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.BUCKET_RENEWAL_TAG));
		_hasher.update(NetString.toNetString(file_renewal_secret));
		_hasher.update(NetString.toNetString(peerid));

		return _hasher.digest();
	}

	public static byte[] bucket_cancel_secret(byte[] file_cancel_secret,
			byte[] peerid) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.BUCKET_CANCEL_TAG));
		_hasher.update(NetString.toNetString(file_cancel_secret));
		_hasher.update(NetString.toNetString(peerid));

		return _hasher.digest();
	}

	public static byte[] uri_extension_hash(byte[] data) {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.UEB_TAG));
		_hasher.update(data);
		return _hasher.digest();
	}

	public static byte[] ssk_writekey_hash(byte[] key) {
		SHA256d _hasher = new SHA256d(Tags.keylen);

		_hasher.update(NetString.toNetString(Tags.MUTABLE_WRITEKEY_TAG));
		_hasher.update(key);
		return _hasher.digest();

	}

	public static byte[] ssk_readkey_hash(byte[] writekey) {
		SHA256d _hasher = new SHA256d(Tags.keylen);

		_hasher.update(NetString.toNetString(Tags.MUTABLE_READKEY_TAG));
		_hasher.update(writekey);
		return _hasher.digest();

	}

	public static byte[] ssk_storage_index_hash(byte[] key) {
		SHA256d _hasher = new SHA256d(Tags.keylen);

		_hasher.update(NetString.toNetString(Tags.MUTABLE_STORAGEINDEX_TAG));
		_hasher.update(key);
		return _hasher.digest();

	}

	public static byte[] ssk_pubkey_fingerprint_hash(byte[] pubkey) {
		SHA256d _hasher = new SHA256d();

		_hasher.update(NetString.toNetString(Tags.MUTABLE_PUBKEY_TAG));
		_hasher.update(pubkey);
		return _hasher.digest();

	}

	public static byte[] ssk_readkey_data_hash(byte[] IV, byte[] readkey) {
		SHA256d _hasher = new SHA256d(Tags.keylen);

		_hasher.update(NetString.toNetString(Tags.MUTABLE_DATAKEY_TAG));
		_hasher.update(NetString.toNetString(IV));
		_hasher.update(NetString.toNetString(readkey));

		return _hasher.digest();

	}

	public static byte[] sha1_hash(byte[] arg0, byte[] arg1) {

		SHA1Digest hasher = new SHA1Digest();
		hasher.update(arg0, 0, arg0.length);
		hasher.update(arg1, 0, arg1.length);
		byte[] out = new byte[hasher.getDigestSize()];
		hasher.doFinal(out, 0);
		return out;
	}
}
