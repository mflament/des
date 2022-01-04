package org.mf.tools.des;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SBoxesTest {

	@Test
	public void test() {
		long bits = BitsUtils.parse("011000 010001 011110 111010 100001 100110 010100 100111");
		long res = 0;
		for (int i = 0; i < SBoxes.length(); i++) {
			res |= SBoxes.get(bits, i);
		}
		assertEquals("0101 1100 1000 0010 1011 0101 1001 0111", BitsUtils.format(res, 32, 8));
	}

}
