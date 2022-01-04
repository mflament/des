package org.mf.tools.des;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.mf.tools.des.BitsUtils.*;

/**
 * The DES algorithm turns a 64-bit message block M into a 64-bit cipher block C.<br/>
 * If each 64-bit block is encrypted individually, then the mode of
 * encryption is called <strong>Electronic Code Book (ECB)</strong> mode.
 * 
 * @see http://page.math.tu-berlin.de/~kant/teaching/hess/krypto-ws2006/des.htm
 * @see https://en.wikipedia.org/wiki/DES_supplementary_material
 */
public class DES {

	private final DESKey key;

	public DES(DESKey key) {
		super();
		this.key = key;
	}

	public byte[] encrypt(byte[] input) throws IOException {
		return process(input, false);
	}

	public byte[] decrypt(byte[] input) throws IOException {
		return process(input, true);
	}

	public void encrypt(InputStream is, OutputStream os) throws IOException {
		process(is, os, false);
	}

	public void decrypt(InputStream is, OutputStream os) throws IOException {
		process(is, os, true);
	}

	private byte[] process(byte[] input, boolean decrypt) throws IOException {
		byte[] output;
		ByteArrayInputStream is = new ByteArrayInputStream(input);
		int outputSize = decrypt ? input.length : BlockInputStream.paddedSize(input);
		ByteArrayOutputStream os = new ByteArrayOutputStream(outputSize);
		process(is, os, decrypt);
		output = os.toByteArray();
		return output;
	}

	private void process(InputStream is, OutputStream os, boolean decrypt) throws IOException {
		long[] subkeys = decrypt ? key.reversedSubkeys() : key.subkeys();
		try (BlockInputStream bis = new BlockInputStream(is, !decrypt);
				BlockOutputStream bos = new BlockOutputStream(os, decrypt)) {
			int read;
			long[] target = new long[1];
			while ((read = bis.read(target)) != -1) {
				for (int i = 0; i < read; i++) {
					process(target[i], bos, subkeys);
				}
			}
		}
	}

	private void process(long bits, BlockOutputStream os, long[] subkeys) throws IOException {
		long permuted = Permutations.IP.permute(bits);
		long[] lr = split(permuted, 64); // L0R0
		for (int i = 0; i < 16; i++) {
			long subkey = subkeys[i]; // Kn
			long l = lr[1]; // Ln = Rn-1
			long r = lr[0] ^ f(lr[1], subkey); // Rn = Ln-1 XOR f(Rn-1,Kn)
			lr[0] = l;
			lr[1] = r;
		}
		// We then reverse the order of the two blocks into the 64-bit block R16L16
		long rl = swap(lr);
		// apply IP-1
		rl = Permutations.INV_IP.permute(rl);
		os.write(rl);
	}

	/**
	 * swap lr and concat into a 32 bits block
	 */
	private long swap(long[] lr) {
		long tmp = lr[0];
		lr[0] = lr[1];
		lr[1] = tmp;
		return concat(lr, 32);
	}

	private static long f(long bits, long subkey) {
		long bns = subkey ^ Permutations.E.permute(bits); // Kn XOR E(Rn-1) = B1B2B3B4B5B6B7B8 (6 * 8 = 48 bits output)
		// s boxing each Bn
		long sns = 0; // Sn (4 * 8 = 32 bits output)
		for (int i = 0; i < SBoxes.length(); i++) {
			sns |= SBoxes.get(bns, i);
		}
		// Permute Sn with P (still 32 bits output)
		return Permutations.P.permute(sns);
	}

}
