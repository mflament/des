package org.mf.tools.des;

public final class BitsUtils {

	private BitsUtils() {}

	/**
	 * get the bit state at a given position [1..size]
	 */
	public static boolean get(long bits, int position) {
		long mask = 1L << (64 - position);
		return (bits & mask) != 0;
	}

	/**
	 * set the bit at a given position [1..size]
	 */
	public static long set(long bits, int position) {
		long mask = 1L << (64 - position);
		return bits | mask;
	}

	/**
	 * clear the bit at a given position [1..size]
	 */
	public static long clear(long bits, int position) {
		long mask = 1L << (64 - position);
		return bits & ~mask;
	}

	/**
	 * split the bits into 2 blocks of (size / 2) bits
	 */
	public static long[] split(long bits, int size) {
		long[] res = new long[2];
		split(bits, size, res);
		return res;
	}

	/**
	 * split the bits into 2 blocks of (size / 2) bits
	 */
	public static void split(long bits, int size, long[] target) {
		int halfSize = size / 2;
		// create a mask with hafSize bits to 1
		long mask = blockMask(halfSize);
		target[0] = bits & mask;
		target[1] = (bits << halfSize) & mask;
	}

	/**
	 * concat 2 blocks of blockSize bits into a single one
	 */
	public static long concat(long[] blocks, int blockSize) {
		long res = blocks[0];
		res |= blocks[1] >>> blockSize;
		return res;
	}

	/**
	 * cyclic shiftCount left shift of size bits.
	 */
	public static long leftShift(long bits, int size, int shiftCount) {
		long res = bits << shiftCount;
		// cycle the shiftCount first bits after the size-shiftCount new first bits
		res |= (bits & blockMask(shiftCount)) >>> (size - shiftCount);
		return res;
	}

	/**
	 * create a mask for size bits at position 1
	 */
	private static long blockMask(int size) {
		long mask = (long) Math.pow(2, size) - 1;
		// move the bits at the start of the long
		mask <<= 64 - size;
		return mask;
	}

	public static String format(long bits, int size) {
		return format(bits, size, 1);
	}

	public static String format(long bits, int size, int blocksCount) {
		// output size is: number of bits + number of space separator - last separator
		StringBuilder sb = new StringBuilder(size + (blocksCount - 1));
		int blockSize = size / blocksCount;
		// for each block
		for (int pos = 1; pos <= size; pos++) {
			sb.append(get(bits, pos) ? '1' : '0');
			// append a separator between each block
			if (pos % blockSize == 0 && pos != size)
				sb.append(' ');
		}
		return sb.toString();
	}

	public static long parse(String s) {
		long bits = 0;
		int position = 1;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '1')
				bits = set(bits, position++);
			else if (c == '0')
				position++;
			else if (c != ' ')
				throw new IllegalArgumentException("Invalid input " + s + ", invalid char at " + i);
		}
		return bits;
	}

	public static long readLong(byte[] buffer) {
		return readLong(buffer, 0);
	}

	public static long readLong(byte[] buffer, int offset) {
		long res = 0;
		for (int i = offset; i < offset + 8; i++) {
			res |= ((long) buffer[i] & 0xFF) << ((7 - i) * 8);
		}
		return res;
	}

	public static byte[] asBytes(long... bits) {
		byte[] res = new byte[bits.length * 8];
		for (int i = 0; i < bits.length; i++) {
			asBytes(bits[i], res, i * 8);
		}
		return res;
	}

	public static void asBytes(long bits, byte[] target, int offset) {
		for (int i = offset; i < offset + 8; i++) {
			long shift = (7 - i) * 8;
			target[i] = (byte) ((bits & (0xFFL << shift)) >>> shift);
		}
	}

	public static byte[] byteArray(int... values) {
		byte[] res = new byte[values.length];
		for (int i = 0; i < values.length; i++) {
			if (values[i] > 255)
				throw new IllegalArgumentException("Invalid byte value " + values[i]);
			res[i] = (byte) values[i];
		}
		return res;
	}

}
