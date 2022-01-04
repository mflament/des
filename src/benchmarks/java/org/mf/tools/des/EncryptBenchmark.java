package org.mf.tools.des;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

public class EncryptBenchmark {

	@State(Scope.Benchmark)
	public static class DesState {
		public DES des;
		public byte[] input;

		@Setup(Level.Trial)
		public void setup() {
			des = BenchmarkUtils.newDES();
			input = BenchmarkUtils.load("clear.txt");
			System.out.println("Encrypting " + input.length + " bytes of data");
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	@OutputTimeUnit(TimeUnit.SECONDS)
	@Fork(1)
	@Warmup(iterations = 1, time = 5, timeUnit = TimeUnit.SECONDS)
	public void encrypt(DesState state,Blackhole blackhole) throws IOException {
		byte[] encryted = state.des.encrypt(state.input);
		blackhole.consume(encryted);
	}
}
