package io.github.ihexon.test;


/**
 * 作用: User: duqi Date: 2017/11/9 Time: 13:40
 */
public class CopyOnWriteArrayListExample2 implements Runnable{

	public static void main(String[] args) {
		CopyOnWriteArrayListExample2 c =new CopyOnWriteArrayListExample2();
		c.run();
	}

	public void run(){
		zzh z = new zzh();
	}




}

class zzh implements  Runnable {

	public zzh(){}

	@Override
	public void run() {
		System.out.println("Fuckkkkkkk");
	}
}
