package eg.nileu.cis.nilestore.cryptography;

import java.util.Arrays;

import junit.framework.TestCase;

import org.bouncycastle.util.encoders.Hex;

public class AESCipherTest extends TestCase {

	byte[] key = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15 };

	String[] input = new String[] {
			"hello world 12345 this my test hello there is",
			" another hello world",
			" let's try another another example another hello world" };

	String[] output = new String[] {
			"aec4575be8af2ced1d23e54290faeb4d466667fdfcb39473305bc98616800d622cbaeb3cb9efcee991ec5a0113",
			"e6c05558f3e73ef04f27e40ecda7f80e1c347ff1",
			"e6cd5e43a0fc7bf61d36a103cfa7ac11163433f4fbafc0762c099d861d95407a25b3a732f7f4d2e486fb5a0005eddcf299da445c580e" };

	String[] output2 = new String[] {
			"aec4575be8af2ced1d23e54290faeb4d466667fdfcb39473305bc98616800d622cbaeb3cb9efcee991ec5a0113",
			"a1d1f3d6d9434b464aaa5d3c31597eebd88e3a54",
			"43daba7e0ba890f16028f2a707becbf3e7eba9051cee35283b51fdfa65b176a432997209c7c09ea9b75bc7c9277a0ed6125876b4475c" };

	public AESCipherTest(String name) {
		super(name);
	}

	public void testEncryption() {
		for (int i = 0; i < input.length; i++) {
			byte[] in = input[i].getBytes();
			byte[] got = new byte[in.length];
			AESCipher cipher = new AESCipher(key, true);
			cipher.processBytes(in, 0, in.length, got, 0);
			byte[] expected = Hex.decode(output[i]);

			if (!Arrays.equals(got, expected)) {
				fail("got (" + new String(Hex.encode(got)) + ") but expected ("
						+ new String(Hex.encode(expected)) + ")");
			}
		}
	}

	public void testEncryption2() {
		AESCipher cipher = new AESCipher(key, true);
		for (int i = 0; i < input.length; i++) {
			byte[] in = input[i].getBytes();
			byte[] got = new byte[in.length];
			cipher.processBytes(in, 0, in.length, got, 0);
			byte[] expected = Hex.decode(output2[i]);

			if (!Arrays.equals(got, expected)) {
				fail("got (" + new String(Hex.encode(got)) + ") but expected ("
						+ new String(Hex.encode(expected)) + ")");
			}
		}
	}

	public void testDecryption() {
		for (int i = 0; i < input.length; i++) {
			byte[] in = Hex.decode(output[i]);
			byte[] out = new byte[in.length];
			AESCipher cipher = new AESCipher(key, false);
			cipher.processBytes(in, 0, in.length, out, 0);
			String got = new String(out);
			String expected = input[i];

			if (!got.equals(expected)) {
				fail("got (" + got + ") but expected (" + expected + ")");
			}
		}
	}

	public void testDecryption2() {
		AESCipher cipher = new AESCipher(key, false);
		for (int i = 0; i < input.length; i++) {
			byte[] in = Hex.decode(output2[i]);
			byte[] out = new byte[in.length];
			cipher.processBytes(in, 0, in.length, out, 0);
			String got = new String(out);
			String expected = input[i];

			if (!got.equals(expected)) {
				fail("got (" + got + ") but expected (" + expected + ")");
			}
		}
	}

}
