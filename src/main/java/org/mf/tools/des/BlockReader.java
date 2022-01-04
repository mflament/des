package org.mf.tools.des;

import java.io.IOException;

/**
 * Read an input stream and feed a consumer.
 * 
 */
public class BlockReader implements AutoCloseable {

	@FunctionalInterface
	public interface BlockConsumer {
		void accept(long bits) throws IOException;
	}

	private final BlockInputStream bis;

	public BlockReader(BlockInputStream bis) {
		super();
		this.bis = bis;
	}

	@Override
	public void close() throws IOException {
		bis.close();
	}

	public void read(BlockConsumer consumer) throws IOException {
		long[] target = new long[1];
		int read;
		while ((read = bis.read(target)) != -1) {
			if (read > 0)
				consumer.accept(target[0]);
		}
	}

}
