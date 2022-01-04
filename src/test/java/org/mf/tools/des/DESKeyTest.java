package org.mf.tools.des;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import static org.mf.tools.des.BitsUtils.*;

public class DESKeyTest {

	@Test
	public void test_to_string() {
		DESKey key = DESKey.create(0x133457799BBCDFF1L);
		assertEquals("00010011 00110100 01010111 01111001 10011011 10111100 11011111 11110001", key.toString());

		assertEquals("000110 110000 001011 101111 111111 000111 000001 110010", formatedSubkey(key, 0));
		assertEquals("011110 011010 111011 011001 110110 111100 100111 100101", formatedSubkey(key, 1));
		assertEquals("010101 011111 110010 001010 010000 101100 111110 011001", formatedSubkey(key, 2));
		assertEquals("011100 101010 110111 010110 110110 110011 010100 011101", formatedSubkey(key, 3));
		assertEquals("011111 001110 110000 000111 111010 110101 001110 101000", formatedSubkey(key, 4));
		assertEquals("011000 111010 010100 111110 010100 000111 101100 101111", formatedSubkey(key, 5));
		assertEquals("111011 001000 010010 110111 111101 100001 100010 111100", formatedSubkey(key, 6));
		assertEquals("111101 111000 101000 111010 110000 010011 101111 111011", formatedSubkey(key, 7));
		assertEquals("111000 001101 101111 101011 111011 011110 011110 000001", formatedSubkey(key, 8));
		assertEquals("101100 011111 001101 000111 101110 100100 011001 001111", formatedSubkey(key, 9));
		assertEquals("001000 010101 111111 010011 110111 101101 001110 000110", formatedSubkey(key, 10));
		assertEquals("011101 010111 000111 110101 100101 000110 011111 101001", formatedSubkey(key, 11));
		assertEquals("100101 111100 010111 010001 111110 101011 101001 000001", formatedSubkey(key, 12));
		assertEquals("010111 110100 001110 110111 111100 101110 011100 111010", formatedSubkey(key, 13));
		assertEquals("101111 111001 000110 001101 001111 010011 111100 001010", formatedSubkey(key, 14));
		assertEquals("110010 110011 110110 001011 000011 100001 011111 110101", formatedSubkey(key, 15));
	}

	private static final String formatedSubkey(DESKey key, int index) {
		return format(key.subkey(index), 48, 8);
	}
}
