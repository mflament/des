package org.mf.tools.des;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class BlockReaderTest {

	private static BlockReader paddedReader(String text) {
		byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
		return new BlockReader(new BlockInputStream(new ByteArrayInputStream(bytes), true));
	}

	@Test
	public void test_read_simple() throws IOException {
		// 36 bytes , expect 4 bytes of padding
		long[] expected = { 0x0123456789ABCDEFL };
		// 36 bytes , expect 4 bytes of padding
		byte[] input = BitsUtils.asBytes(0x0123456789ABCDEFL);
		try (BlockReader reader = new BlockReader(new BlockInputStream(new ByteArrayInputStream(input), false))) {
			AtomicInteger index = new AtomicInteger();
			reader.read(l -> assertEquals("Unexpected block at index " + index.get(), expected[index.getAndIncrement()],
					l));
		}
	}

	@Test
	public void test_read_padded() throws IOException {
		// 36 bytes , expect 4 bytes of padding
		long[] expected = { 0x596F7572206C6970L, 0x732061726520736DL, 0x6F6F746865722074L, 0x68616E2076617365L,
				0x6C696E6504040404L };
		// 36 bytes , expect 4 bytes of padding
		try (BlockReader reader = paddedReader("Your lips are smoother than vaseline")) {
			AtomicInteger index = new AtomicInteger();
			reader.read(l -> assertEquals("Unexpected block at index " + index.get(), expected[index.getAndIncrement()],
					l));
		}
	}

	@Test
	public void test_read_full_padded() throws IOException {
		// 40 bytes , expect a full long of padding bytes
		long[] expected = { 0x596F7572206C6970L, 0x732061726520736DL, 0x6F6F746865722074L, 0x68616E2076617365L,
				0x6C696E6520202020L, 0x0808080808080808L };
		try (BlockReader reader = paddedReader("Your lips are smoother than vaseline    ")) {
			AtomicInteger index = new AtomicInteger();
			reader.read(l -> assertEquals("Unexpected block at index " + index.get(), expected[index.getAndIncrement()],
					l));
		}
	}

	@Test(expected = EOFException.class)
	public void test_read_missing_padding() throws IOException {
		byte[] bytes = "fail".getBytes(StandardCharsets.US_ASCII);
		try (BlockReader reader = new BlockReader(new BlockInputStream(new ByteArrayInputStream(bytes), false))) {
			reader.read(l -> {});
		}
	}
}
