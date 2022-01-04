package org.mf.tools.des;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PermutationTest {

	@Test
	public void test_permute() {
		long bits = BitsUtils.parse("00010011 00110100 01010111 01111001 10011011 10111100 11011111 11110001");
		bits = Permutations.PC1.permute(bits);
		assertEquals("1111000 0110011 0010101 0101111 0101010 1011001 1001111 0001111", BitsUtils.format(bits, 56, 8));
	}
	
}
