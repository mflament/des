package org.mf.tools.des;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.Test;

public class DESTest {

	private static final DESKey KEY = DESKey.create(0x133457799BBCDFF1L);

	private final DES des = new DES(KEY);
	
	@Test
	public void test_block() throws Exception {
		byte[] input = BitsUtils.asBytes(0x0123456789ABCDEFl);
		byte[] encrypted = des.encrypt(input);
		byte[] decrypted = des.decrypt(encrypted);
		assertArrayEquals(input, decrypted);
	}

	@Test
	public void test_message() throws Exception {
		String message = "This a really secret message, please do not read it !";
		byte[] clear = message.getBytes();
		byte[] encrypted = des.encrypt(clear);
		byte[] decrypted = des.decrypt(encrypted);
		assertEquals(message, new String(decrypted));
	}

	@Test
	public void test_encrypt_vs_bouncy() throws Exception {
		String message = "This a really secret message, please do not read it !";
		byte[] input = message.getBytes();
		byte[] expecteds = bouncyEncrypt(input);
		byte[] actuals = des.encrypt(input);
		assertArrayEquals(expecteds, actuals);
	}

	private static byte[] bouncyEncrypt(byte[] input) throws Exception {
		BlockCipher engine = new DESEngine();
		BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine, new PKCS7Padding());
		cipher.init(true, new KeyParameter(KEY.getBytes()));
		byte[] output = new byte[cipher.getOutputSize(input.length)];
		int outputLength = cipher.processBytes(input, 0, input.length, output, 0);
		cipher.doFinal(output, outputLength);
		return output;
	}
}
