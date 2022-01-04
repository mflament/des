package org.mf.tools.des;

import static org.mf.tools.des.BitsUtils.asBytes;
import static org.mf.tools.des.BitsUtils.concat;
import static org.mf.tools.des.BitsUtils.format;
import static org.mf.tools.des.BitsUtils.leftShift;
import static org.mf.tools.des.BitsUtils.readLong;
import static org.mf.tools.des.BitsUtils.split;

public class DESKey {

	private static final int[] BITS_ROTATION = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

	private final long bits;

	private final long[] subkeys;

	public DESKey(long bits, long[] subkeys) {
		super();
		this.bits = bits;
		this.subkeys = subkeys;
	}

	public byte[] getBytes() {
		return asBytes(bits);
	}

	public long subkey(int index) {
		return subkeys[index];
	}

	@Override
	public String toString() {
		return format(bits, 64, 8);
	}

	public static DESKey create(String s) {
		return create(s.getBytes());
	}

	public static DESKey create(byte[] bytes) {
		if (bytes.length != 8)
			throw new IllegalArgumentException("Invalid key length " + bytes.length + ", expecting 8 bytes");
		return create(readLong(bytes));
	}

	public static DESKey create(long bits) {
		long permuted = Permutations.PC1.permute(bits);
		long[] subkeys = new long[16];
		long[] split = split(permuted, 56);
		for (int i = 0; i < subkeys.length; i++) {
			split[0] = leftShift(split[0], 28, BITS_ROTATION[i]);
			split[1] = leftShift(split[1], 28, BITS_ROTATION[i]);
			subkeys[i] = Permutations.PC2.permute(concat(split, 28));
		}
		return new DESKey(bits, subkeys);
	}

	public long[] subkeys() {
		return subkeys;
	}

	public long[] reversedSubkeys() {
		long[] res = new long[subkeys.length];
		for (int i = 0; i < subkeys.length; i++) {
			res[i] = subkeys[subkeys.length - (i + 1)];
		}
		return res;
	}
}