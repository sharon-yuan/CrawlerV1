/**
 * user : wqp0010@gmail.com 
 * date : 2016年12月15日 上午9:54:39
 */
package com.Suirui.CrawlerV1;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class Test1 {
	public static void main(String[] args) {
		System.err.println("begin at " + (new Date()));
		int threadNumber = 4;
        final CountDownLatch countDownLatch = new CountDownLatch(threadNumber);
        
		WorkThread thread = new WorkThread();
		for (int i = 0; i < threadNumber;i++) {
			thread.setCountDownLatch(countDownLatch);
			thread.setStartIndex(i * 1000 + 1);
			thread.setEndIndex(i * 1000 + 1000);
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


