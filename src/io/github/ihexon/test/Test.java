package io.github.ihexon.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;
class Worker implements Runnable{
	private List<String> outputScraper;
	private CountDownLatch countDownLatch;

	public Worker(List<String> outputScraper,CountDownLatch countDownLatch){
		this.outputScraper = outputScraper;
		this.countDownLatch = countDownLatch;
	}

	@Override
	public void run() {
		if (false) {
			throw new RuntimeException("Oh dear, I'm a BrokenWorker");
		}
		outputScraper.add("Counted down");
		System.out.println(System.nanoTime()+" "+outputScraper);
		countDownLatch.countDown();
	}
}


public class Test{
	public static void main(String[] args) throws InterruptedException {
//		new Test().zzh();
		ErrPrintln(System.nanoTime());
	}



	private static void  ErrPrintln(Object x){
		String s = String.valueOf(x);
			System.err.println(s);
	}

	public void zzh() throws InterruptedException{
		CountDownLatch countDownLatch = new CountDownLatch(3);
		 List<String>  outputScraper = Collections.synchronizedList(new ArrayList<>());

		List<Thread> workers = Stream.generate(() -> new Thread(new Worker(outputScraper,countDownLatch)))
				.limit(3).collect(Collectors.toList());
		workers.forEach(Thread::start);
//		countDownLatch.await(3L,TimeUnit.SECONDS);;









	}
}
