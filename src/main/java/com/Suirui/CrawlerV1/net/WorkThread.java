/**
 * user : wqp0010@gmail.com 
 * date : 2016年12月15日 上午10:04:54
 */
package com.Suirui.CrawlerV1.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.Suirui.CrawlerV1.util.FileIO;
import com.Suirui.CrawlerV1.util.Proxys;
import com.Suirui.CrawlerV1.util.ProxyController;

public class WorkThread implements Runnable {
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
		else System.out.println(Thread.currentThread().getName()+" start");

		int i = startIndex;
		Proxys proxys = new Proxys();
		List<String[]> proxyList =null;// proxyController.readerProxyFromDir();
		/*for (String[] tempProxy : proxyList) {
			proxys.add(tempProxy[0], Integer.valueOf(tempProxy[1]));
		}*/

		proxyList = ProxyController.readerProxyFromFile("1.txt");
		for (String[] tempProxy : proxyList) {
			proxys.add(tempProxy[0], Integer.valueOf(tempProxy[1]));
		}
		System.out.println("total proxy count = " + proxys.size());

	
	
	System.out.println("total proxy count = " + proxys.size());
	
		

		System.setProperty("webdriver.gecko.driver", "D:\\MyDrivers\\geckodriver-v0.11.1-win64\\geckodriver.exe");
		// System.setProperty("webdriver.gecko.driver",
		// "D:\\Softwares\\geckodriver-v0.11.1-win64\\geckodriver.exe");
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Softwares\\firefox\\firefox.exe");
		File file = new File("E:/data/china/links/" + i);
		while (file.exists()) {
			i++;
			file = new File("E:/data/china/links/" + i);
			if (i==8072) {
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				System.out.println(
						i + "----------==========================finished!!!!=====================-------------------");
				return;}
		}
		boolean openedFlag = false;
		
		while (!openedFlag) {

			if (proxys.size() <= 7) {
				proxyList = ProxyController.readerProxyFromFile(MutiThreadCrawler.filenameString);
				for (String[] tempProxy : proxyList) {
					proxys.add(tempProxy[0], Integer.valueOf(tempProxy[1]));
				}
				
				// proxys= proxyController.readerProxyFromWeb();
				System.out.println("total proxy count = " + proxys.size());
			}
			java.net.Proxy proxy = proxys.nextRandom();
			String[] proxyarray = proxy.toString().split(":");
			String proxyHost = proxyarray[0].split("/")[1];
			int proxyPort = Integer.valueOf(proxyarray[1]);

			// 测试可用性
			System.err.println(startIndex+"test proxy " + proxy);

			TelnetClient telnetClient = new TelnetClient("vt200");
			// 指明Telnet终端类型，否则会返回来的数据中文会乱码
			telnetClient.setDefaultTimeout(1000); // socket延迟时间：1000ms
			System.err.println("try proxy " + proxy);
			try {
				telnetClient.connect(proxyHost, proxyPort);
			} catch (Exception e) {
				proxys.remove(proxy);
				System.err.println("remove proxy：" + proxy);

				continue;
			}
			try {
				Proxy tmpproxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

				URL u = new URL("http://www.ip138.com/");
				HttpURLConnection con = (HttpURLConnection) u.openConnection(tmpproxy);
				con.setConnectTimeout(3000);
				con.setReadTimeout(3000);

				InputStream inStrm = con.getInputStream();
				StringBuffer out = new StringBuffer();
				byte[] b = new byte[4096];
				for (int n; (n = inStrm.read(b)) != -1;) {
					out.append(new String(b, 0, n));
				}
				// System.out.println("page is " + out.toString());
				if (out.toString().contains("114.247.234.178")) {
					proxys.remove(proxy);
					System.err.println("并不匿名！！！: " + tmpproxy);
					continue;
				} else
					System.out.println("测试ip网页返回长度： " + out.toString().length());
			} catch (Exception e) {
				proxys.remove(proxy);
				System.err.println("站长ip都打不开！要你何用！！：" + proxy);

				continue;
			}
			System.err.println("use proxy : " + proxy);

			/*
			 * try { Proxy tmpproxy = new Proxy(java.net.Proxy.Type.HTTP, new
			 * InetSocketAddress(proxyHost, proxyPort));
			 * 
			 * URL u = new URL("http://www.ccgp.gov.cn/"); HttpURLConnection con
			 * = (HttpURLConnection) u.openConnection(tmpproxy);
			 * con.setConnectTimeout(3000); con.setReadTimeout(3000);
			 * InputStream inStrm = con.getInputStream(); StringBuffer out = new
			 * StringBuffer(); byte[] b = new byte[4096]; for (int n; (n =
			 * inStrm.read(b)) != -1;) { out.append(new String(b, 0, n)); } //
			 * System.out.println("page is " + out.toString()); if
			 * (!out.toString().contains("<title>中国政府采购网_首页</title>")) {
			 * proxys.remove(proxy); System.err.println("remove proxy : " +
			 * proxy); continue; } } catch (Exception e) { proxys.remove(proxy);
			 * System.err.println("remove proxy : " + proxy); continue; }
			 * 
			 * System.err.println("use proxy : " + proxy);
			 */
			/*
			 * String proxyHost = "222.211.53.201"; int proxyPort = 8118;
			 */
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("network.proxy.type", 1);
			profile.setPreference("network.proxy.http", proxyHost);
			profile.setPreference("network.proxy.http_port", proxyPort);
			profile.setPreference("network.proxy.ssl", proxyHost);
			profile.setPreference("network.proxy.ssl_port", Integer.valueOf(proxyPort));
			profile.setPreference("network.proxy.share_proxy_settings", false);
			profile.setPreference("network.proxy.no_proxies_on", "localhost");
			//profile.setPreference("permissions.default.image", 2);

			//profile.setPreference("webdriver.load.strategy", "fast");
			WebDriver driver = new FirefoxDriver(profile);
			
			while (i<8072) {
			 file = new File("E:/data/china/links/" + i);
				while (file.exists()) {
					i++;
					file = new File("E:/data/china/links/" + i);}
			try {
				
				Thread.sleep(1000*60 + (int) (Math.random() * 1000*60*3));
				String lasturl = "http://search.ccgp.gov.cn/dataB.jsp?searchtype=1&page_index=" + i
						+ "&buyerName=&projectId=&dbselect=infox&kw=&start_time=2016%3A01%3A01&end_time=2016%3A06%3A29&timeType=6&bidSort=2&pinMu=0&bidType=1&displayZone=&zoneId=&pppStatus=&agentName=";
				driver.get(lasturl);
				
				
					
					System.err.println(i + "th url is :" + driver.getCurrentUrl());
					final int flag = i;
					org.jsoup.nodes.Document doc = Jsoup.parse(driver.getPageSource());
					Elements elements = doc.getElementsByAttribute("href");

					String linkContent = "";

					WebElement nextButton = driver.findElement(By.className("next"));
					lasturl = driver.getCurrentUrl();
					for (Element element : elements) {
						if (element.attr("href").matches("http://www.ccgp.gov.cn/cggg/.*htm")) {
							linkContent += element.attr("href") + '\n';
						}
					}
					FileIO.saveintoFile("E:/data/china/links/" + i, linkContent);
					if(i>=8072) break;
					nextButton.click();
					
					int sleepLongth = 1000*20 + (int) (Math.random() * 1000*10);
					Thread.sleep(sleepLongth);
					int retryTimes = 0;
					while (driver.getCurrentUrl().equals(lasturl)) {

						retryTimes++;
						Thread.sleep(1000*5);
						if (retryTimes % 10 == 0) {
							System.err.println(startIndex+"wait " + (retryTimes*5) + "s");
							if (retryTimes == 60)
								nextButton.click();
							if (retryTimes >= 120)
								throw new Exception(startIndex+"waited 60s for nextButton");
						}
					}
					lasturl = driver.getCurrentUrl();
					i++;
					
				
				
				
				
			} catch (Exception e) {
				driver.close();
				driver.quit();
				openedFlag = false;
				proxys.remove(proxy);
				e.printStackTrace();
				int tmpCashTime = cashTime.get();
				if (tmpCashTime % 120 == 0)
					if(tmpCashTime!=0)
					try {
						System.out.println("崩溃太多 休息一下吧");
						Thread.sleep(1000 * 60 * 30);

					} catch (Exception e1) {

						e1.printStackTrace();
					}
				cashTime.incrementAndGet();
				System.err.println("cash time = " + tmpCashTime);
				
			}
			}
		}
		System.out.println("Thread " + Thread.currentThread().getName() + "end");
		countDownLatch.countDown();
	}

}
