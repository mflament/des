package org.mf.tools.des;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class BenchmarkUtils {
	
	private static final DESKey DES_KEY = DESKey.create(System.currentTimeMillis());
	
	public static DES newDES() {
		return new DES(DES_KEY);
	}
	
	public static byte[] load(String resource) {
		ClassLoader cl = BenchmarkUtils.class.getClassLoader();
		try (InputStream is = new BufferedInputStream(cl.getResourceAsStream(resource))) {
			byte[] buffer = new byte[1024 * 1024];
			int read, length = 0;
			while ((read = is.read(buffer, length, buffer.length - length)) != -1) {
				length += read;
			}
			return Arrays.copyOf(buffer, length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] encrypt(String resource) {
		load(resource);
		try {
			return newDES().encrypt(load(resource));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private BenchmarkUtils() {}

}
