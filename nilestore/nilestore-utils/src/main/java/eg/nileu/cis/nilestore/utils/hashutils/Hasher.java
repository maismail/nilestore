package eg.nileu.cis.nilestore.utils.hashutils;

import eg.nileu.cis.nilestore.cryptography.SHA256d;
import eg.nileu.cis.nilestore.utils.EncodingParam;
import eg.nileu.cis.nilestore.utils.NetString;

public class Hasher {

	public static SHA256d getConvergencehasher(EncodingParam params,
			byte[] convergence) {

		SHA256d _hasher = new SHA256d(Tags.keylen);
		byte[] paramtag = NetString.toNetString(String.format("%d,%d,%d",
				params.getK(), params.getN(), params.getSegmentSize()));
		byte[] conv_netstring = NetString.toNetString(convergence);

		byte[] data = NetString.toNetString(
				Tags.CONVERGENT_ENCRYPTION_TAG.getBytes(), conv_netstring,
				paramtag);
		_hasher.update(data);
		return _hasher;
	}

	public static SHA256d getPlaintexthasher() {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.PLAINTEXT_TAG));
		return _hasher;
	}

	public static SHA256d getCrypttexthasher() {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.CIPHERTEXT_TAG));
		return _hasher;
	}

	public static SHA256d getCrypttextSegmenthasher() {
		SHA256d _hasher = new SHA256d();
		_hasher.update(NetString.toNetString(Tags.CIPHERTEXT_SEGMENT_TAG));
		return _hasher;
	}
}
