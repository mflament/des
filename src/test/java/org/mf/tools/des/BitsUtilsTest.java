package org.mf.tools.des;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mf.tools.des.BitsUtils.asBytes;
import static org.mf.tools.des.BitsUtils.byteArray;
import static org.mf.tools.des.BitsUtils.clear;
import static org.mf.tools.des.BitsUtils.concat;
import static org.mf.tools.des.BitsUtils.format;
import static org.mf.tools.des.BitsUtils.get;
import static org.mf.tools.des.BitsUtils.leftShift;
import static org.mf.tools.des.BitsUtils.parse;
import static org.mf.tools.des.BitsUtils.set;
import static org.mf.tools.des.BitsUtils.*;

import org.junit.Test;

public class BitsUtilsTest {

	@Test
	public void test() {
		long bits = 0;
		assertEquals("00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));

		bits = set(bits, 1);
		assertEquals("10000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));

		bits = set(bits, 6);
		assertEquals("10000100 00000000 00000000 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));

		bits = clear(bits, 1);
		assertEquals("00000100 00000000 00000000 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));

		bits = set(bits, 6);
		assertEquals("00000100 00000000 00000000 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));

		bits = set(bits, 24);
		assertEquals("00000100 00000000 00000001 00000000 00000000 00000000 00000000 00000000", format(bits, 64, 8));
	}

	@Test
	public void test_parse() {
		long bits = parse("1001 0001");
		assertTrue(get(bits, 1));
		assertFalse(get(bits, 2));
		assertFalse(get(bits, 3));
		assertTrue(get(bits, 4));
		assertFalse(get(bits, 5));
		assertFalse(get(bits, 6));
		assertFalse(get(bits, 7));
		assertTrue(get(bits, 8));
	}

	@Test
	public void test_format() {
		long bits = BitsUtils.parse("1111000 0110011 0010101 0101111 0101010 1011001 1001111 0001111");
		assertEquals("1111000 0110011 0010101 0101111 0101010 1011001 1001111 0001111", format(bits, 56, 8));
	}

	@Test
	public void test_split() {
		long bits = parse("1001 0001");
		long[] split = split(bits, 8);
		assertEquals("1001", format(split[0], 4, 1));
		assertEquals("0001", format(split[1], 4, 1));
	}

	@Test
	public void test_concat() {
		long[] split = { parse("1001"), parse("0001") };
		long concat = concat(split, 4);
		assertEquals("1001 0001", format(concat, 8, 2));

		split = new long[] { parse("1001"), parse("1001") };
		concat = concat(split, 4);
		assertEquals("1001 1001", format(concat, 8, 2));
	}

	@Test
	public void test_left_shift() {
		long bits = parse("1111000011001100101010101111");

		long shifted = leftShift(bits, 28, 1);
		assertEquals("1110000110011001010101011111", format(shifted, 28));

		shifted = leftShift(shifted, 28, 1);
		assertEquals("1100001100110010101010111111", format(shifted, 28));

		shifted = leftShift(shifted, 28, 2);
		assertEquals("0000110011001010101011111111", format(shifted, 28));

		shifted = leftShift(shifted, 28, 2);
		assertEquals("0011001100101010101111111100", format(shifted, 28));
	}

	@Test
	public void test_read_long() {
		byte[] bytes = asBytes(0x0123456789ABCDEFL);
		long bits = readLong(bytes);
		assertEquals("0000 0001 0010 0011 0100 0101 0110 0111 1000 1001 1010 1011 1100 1101 1110 1111",
				format(bits, 64, 16));
	}

	@Test
	public void test_as_bytes() {
		byte[] bytes = asBytes(0x85E813540F0AB405L);
		assertArrayEquals(byteArray(0x85, 0xE8, 0x13, 0x54, 0x0F, 0x0A, 0xB4, 0x05), bytes);
	}

}
