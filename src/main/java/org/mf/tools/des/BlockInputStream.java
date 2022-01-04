package org.mf.tools.des;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Read an input stream as block (long) and optionally add any required padding
 * using PKCS#7.
 * 
 * @see https://en.wikipedia.org/wiki/Padding_(cryptography)#PKCS#5_and_PKCS#7
 */
public class BlockInputStream implements AutoCloseable {

	private final InputStream is;

	private final boolean addPadding;

	private byte[] buffer;

	private byte[] pendings = new byte[7];
	private int pendingsCount = 0;

	private boolean eof;

	public BlockInputStream(InputStream is, boolean addPadding) {
		this.is = is;
		this.addPadding = addPadding;
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	public int read(long[] target) throws IOException {
		return read(target, 0, target.length);
	}

	public int read(long[] target, int offset, int length) throws IOException {
		if (eof)
			return -1;

		int required = allocateBuffer(length);

		if (pendingsCount > 0) {
			// copy the pending bytes to the start of buffer
			System.arraycopy(pendings, 0, buffer, 0, pendingsCount);
			required -= pendingsCount;
		}

		int bytesRead = is.read(buffer, pendingsCount, required);
		if (bytesRead == -1) {
			eof = true;
			// end of stream
			if (addPadding) {
				addPadding();
				target[offset] = BitsUtils.readLong(buffer);
				return 1;
			} else if (pendingsCount > 0) {
				throw new EOFException("end of stream reached with " + (8 - pendingsCount) + " missing  bytes");
			} else {
				eof = true;
				return -1;
			}
		}

		int remaining = bytesRead + pendingsCount;
		int read = 0, bufferOffset = 0;
		while (remaining >= 8) {
			target[offset + read] = BitsUtils.readLong(buffer, bufferOffset);
			read++;
			bufferOffset += 8;
			remaining -= 8;
		}

		if (remaining > 0) {
			// store remaining bytes
			System.arraycopy(buffer, bufferOffset, pendings, 0, remaining);
			pendingsCount = remaining;
		} else {
			pendingsCount = 0;
		}

		return read;
	}

	/**
	 * Create or expand the buffer to be able to hold N block ( = 8 * length)
	 */
	private int allocateBuffer(int blocks) {
		int length = blocks * 8;
		if (buffer == null || buffer.length < length)
			buffer = new byte[length];
		return length;
	}

	private void addPadding() {
		byte padding = (byte) (8 - pendingsCount);
		for (int i = pendingsCount; i < 8; i++) {
			buffer[i] = padding;
		}
	}

	public static int paddedSize(byte[] input) {
		int padding = input.length % 8;
		if (padding == 0)
			padding = 8;
		return input.length + padding;
	}

}
