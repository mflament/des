package org.mf.tools.des;

import java.io.IOException;
import java.io.OutputStream;

public class BlockOutputStream implements AutoCloseable {

	private final OutputStream os;

	private final boolean unpad;

	private byte[] buffer;

	public BlockOutputStream(OutputStream os, boolean unpad) {
		super();
		this.os = os;
		this.unpad = unpad;
	}

	public void write(long bits) throws IOException {
		if (buffer != null) {
			os.write(buffer);
		} else {
			buffer = new byte[8];
		}
		BitsUtils.asBytes(bits, buffer, 0);
	}

	@Override
	public void close() throws IOException {
		if (unpad) {
			byte padding = buffer[7];
			int pendings = 8 - padding;
			os.write(buffer, 0, pendings);
		} else if (buffer != null) {
			os.write(buffer, 0, 8);
		}
		os.close();
	}

}
