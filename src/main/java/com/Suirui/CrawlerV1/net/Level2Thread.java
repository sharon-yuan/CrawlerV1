package com.Suirui.CrawlerV1.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.net.telnet.TelnetClient;
import org.seleniumhq.jetty9.util.security.Credential.MD5;

import com.Suirui.CrawlerV1.util.Config;
import com.Suirui.CrawlerV1.util.FileIO;
import com.Suirui.CrawlerV1.util.MD5Util;
import com.Suirui.CrawlerV1.util.ProxyController;
import com.Suirui.CrawlerV1.util.Proxys;

public class Level2Thread implements Runnable {
	public static AtomicInteger cashTime = new AtomicInteger(0);
	private int startIndex;
	private int endIndex;
	private CountDownLatch countDownLatch;

	public CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

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

		System.err.println("Thread " + Thread.currentThread().getName() + "begin , startIndex = " + startIndex
				+ ", endIndex = " + endIndex);
		if (startIndex == 0 || endIndex == 0) {
			countDownLatch.countDown();
			System.out.println("Thread " + Thread.currentThread().getName() + "end");
			return;
		}

		int i = startIndex;
		
		System.out.println("total proxy count = " + Level2Crawler.proxys.size());
		ArrayList<String> urList = new ArrayList<>();
		while (i < endIndex)
			urList.addAll(FileIO.getLinesArray(Config.LINKS_DIR + i++));

		while (!urList.isEmpty()) {

			if (Level2Crawler.proxys.size() <= 7) {
				System.err.println(Thread.currentThread().getName() +" dont have enought proxy");
				Level2Crawler.proxys.addAll(FileIO.getLinesArray("E:/data/china/proxy/" + MutiThreadCrawler.filenameString));
			}
			Proxy proxy = Level2Crawler.proxys.nextRandom();
			Level2Crawler.proxys.remove(proxy);
			if (!ProxyController.testProxy(proxy)) {
				
				continue;
			}

			String[] proxyarray = proxy.toString().split(":");
			String proxyHost = proxyarray[0].split("/")[1];
			int proxyPort = Integer.valueOf(proxyarray[1]);
			System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:2.0b13pre) Gecko/20110307 Firefox/4.0b13pre");
			while (!urList.isEmpty()) {

				Proxy tmpproxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

				URL url;
				try {
					url = new URL(urList.get(0));
					File tempPageFile = new File(Config.PAGES_DIR + MD5Util.MD5(url.toString()));
					if (tempPageFile.exists()) {
						urList.remove(0);
						continue;
					}
					HttpURLConnection con = (HttpURLConnection) url.openConnection(tmpproxy);
					
					con.setConnectTimeout(3000);
					con.setReadTimeout(3000);

					InputStream inStrm = con.getInputStream();
					StringBuffer out = new StringBuffer();
					byte[] b = new byte[4096];
					for (int n; (n = inStrm.read(b)) != -1;) {
						out.append(new String(b, 0, n));
					}
					// System.out.println("page is " + out.toString());
					FileIO.saveintoFile(tempPageFile.getAbsolutePath(),"<!--"+url.toString()+"-->" +'\n'+out.toString());
					urList.remove(0);
					System.out.println(Thread.currentThread().getName()+" using proxy "+proxy);
					Thread.sleep(100);
				} 
				 catch (Exception e) {
					Level2Crawler.proxys.remove(proxy);
					System.err.println(Thread.currentThread().getName()+" fail to open ccgp page!");
					break;
				}

			}
			
		}
		System.out.println("Thread " + Thread.currentThread().getName() + "end");
		countDownLatch.countDown();
	}
}
