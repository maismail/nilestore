package eg.nileu.cis.nilestore.cryptography;

import java.util.Arrays;

import junit.framework.TestCase;

import org.bouncycastle.util.encoders.Hex;

public class SHA256dTest extends TestCase {

	String[] input = new String[] { "helloworld", "helloworld helloworld",
			"helloworld helloworld helloworld" };

	String[] hexoutput = new String[] {
			"364bedd239814071a9e0a50b50fcbde3f6896d8bc6cd4858618c5cece0d36d5e",
			"e3713c3defccdb9b0cd4b5f0d214fd7a8ac4e9c3cc1a5e74790a2c25e1e5d70a",
			"a584a3f4a1e92a8afe178ce01c403185b5108ca74b46a27825e6da92d78a1827" };

	public SHA256dTest(String name) {
		super(name);
	}

	public void testHashing() {
		for (int i = 0; i < input.length; i++) {
			SHA256d hasher = new SHA256d();
			hasher.update(input[i]);
			byte[] got = hasher.digest();
			byte[] expected = Hex.decode(hexoutput[i]);
			if (!Arrays.equals(got, expected)) {
				fail("got (" + new String(Hex.encode(got)) + ") but expected ("
						+ hexoutput[i] + ")");
			}
		}
	}

	public void testHashingTruncated() {
		int truncateto = 15;
		for (int i = 0; i < input.length; i++) {
			SHA256d hasher = new SHA256d(truncateto);
			hasher.update(input[i]);
			byte[] got = hasher.digest();
			byte[] expected = new byte[truncateto];
			System.arraycopy(Hex.decode(hexoutput[i]), 0, expected, 0,
					truncateto);
			if (!Arrays.equals(got, expected)) {
				fail("got (" + new String(Hex.encode(got)) + ") but expected ("
						+ new String(Hex.encode(expected)) + ")");
			}
		}
	}
}
