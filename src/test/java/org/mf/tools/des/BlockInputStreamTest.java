package org.mf.tools.des;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class BlockInputStreamTest {

	private static byte[] input;

	@BeforeClass
	public static void setup() {
		input = new byte[36];
		System.arraycopy(BitsUtils.asBytes(0x596F7572206C6970L), 0, input, 0, 8);
		System.arraycopy(BitsUtils.asBytes(0x732061726520736DL), 0, input, 8, 8);
		System.arraycopy(BitsUtils.asBytes(0x6F6F746865722074L), 0, input, 16, 8);
		System.arraycopy(BitsUtils.asBytes(0x68616E2076617365L), 0, input, 24, 8);
		System.arraycopy(BitsUtils.byteArray(0x6C, 0x69, 0x6E, 0x65), 0, input, 16, 4);
	}

	@Test
	public void test_read_add_padding() throws IOException {
		try (BlockInputStream is = createStream(4, true)) {
			long[] target = new long[1];
			int read = is.read(target);
			assertEquals(0, read);

			read = is.read(target);
			assertEquals(1, read);
			assertEquals(0x596F757204040404L, target[0]);

			read = is.read(target);
			assertEquals(-1, read);
		}
	}

	@Test
	public void test_read_full_padding() throws IOException {
		try (BlockInputStream is = createStream(8, true)) {
			long[] target = new long[1];
			int read = is.read(target);
			assertEquals(1, read);
			assertEquals(0x596F7572206C6970L, target[0]);

			read = is.read(target);
			assertEquals(1, read);
			assertEquals(0x0808080808080808L, target[0]);

			read = is.read(target);
			assertEquals(-1, read);
		}
	}

	@Test
	public void test_read_no_padding() throws IOException {
		try (BlockInputStream is = createStream(8, false)) {
			long[] target = new long[1];
			int read = is.read(target);
			assertEquals(1, read);
			assertEquals(0x596F7572206C6970L, target[0]);

			read = is.read(target);
			assertEquals(-1, read);
		}
	}

	@Test(expected=EOFException.class)
	public void test_read_missing_padding() throws IOException {
		try (BlockInputStream is = createStream(4, false)) {
			long[] target = new long[1];
			int read = is.read(target);
			assertEquals(0, read);
			is.read(target);
		}
	}

	private BlockInputStream createStream(int length, boolean padded) {
		ByteArrayInputStream bais = new ByteArrayInputStream(Arrays.copyOf(input, length));
		return new BlockInputStream(bais, padded);
	}

}
