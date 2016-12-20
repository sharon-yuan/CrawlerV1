/**
 * user : wqp0010@gmail.com 
 * date : 2016年12月15日 上午9:54:39
 */
package com.Suirui.CrawlerV1.net;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class MutiThreadCrawler {
	public static String filenameString="1.txt";
	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "D:\\MyDrivers\\geckodriver-v0.11.1-win64\\geckodriver.exe");
		Logger logger = Logger.getLogger("com.gargoylesoftware.htmlunit");
		logger.setLevel(Level.OFF);
		System.err.println("begin at " + (new Date()));
		int threadNumber =1;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
      
        ProxyThread proxThread=new ProxyThread();;
       Thread filereader=new Thread(proxThread);
       filereader.start();
		WorkThread thread = new WorkThread();
		for (int i = 1; i <= threadNumber;i++) {
		
			thread.setCountDownLatch(countDownLatch);
			thread.setStartIndex(1);
			thread.setEndIndex(8072);
			Thread tmp =new Thread(thread ,"Thread" +i);
			tmp.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("end at " + (new Date()));
	}

}


