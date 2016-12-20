import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class testThread implements Runnable{

	public static AtomicInteger cashTime = new AtomicInteger(0);
	private int startIndex;
	private int endIndex;

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	@Override
	public void run() {
		
		System.out.println("Thread " + Thread.currentThread().getName() + "begin , startIndex = " + startIndex
				+ ", endIndex = " + endIndex);
		
		try {
			Thread.sleep(1000*10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
System.out.println("Thread " + Thread.currentThread().getName() + "begin , startIndex = " + startIndex
				+ ", endIndex = " + endIndex);
	}

}
