package org.mf.tools.des;

import java.util.Arrays;
import static org.mf.tools.des.BitsUtils.*;

public final class Permutation {

	private final int[] mappings;

	Permutation(int[] mappings) {
		super();
		this.mappings = mappings;
		// check that the number of target bits is a multiple of 8
		if (mappings.length % 8 != 0)
			throw new IllegalArgumentException("Invalid permutation table size " + mappings.length);
	}
	
	public long permute(long bits) {
		long res = 0;
		for (int i = 0; i < mappings.length; i++) {
			if (get(bits, mappings[i]))
				res = set(res, i + 1);
		}
		return res;
	}

	@Override
	public String toString() {
		return Arrays.toString(mappings);
	}

}