/**
 * user : wqp0010@gmail.com 
 * date : 2016年12月15日 上午10:04:54
 */
package com.Suirui.CrawlerV1;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

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
		System.out.println("Thread " + Thread.currentThread().getName() + "begin , startIndex = " + startIndex
				+ ", endIndex = " + endIndex);
		if (startIndex == 0 || endIndex == 0) {
			countDownLatch.countDown();
			System.out.println("Thread " + Thread.currentThread().getName() + "end");
			return;
		}

		int i = startIndex;
		Proxys proxys = new Proxys();
		List<String[]> proxyList = proxyController.readerProxyFromDir();
		for (String[] tempProxy : proxyList) {
			proxys.add(tempProxy[0], Integer.valueOf(tempProxy[1]));
		}
		System.out.println("total proxy count = " + proxys.size());

		System.setProperty("webdriver.gecko.driver", "D:\\MyDrivers\\geckodriver-v0.11.1-win64\\geckodriver.exe");
		// System.setProperty("webdriver.gecko.driver",
		// "D:\\Softwares\\geckodriver-v0.11.1-win64\\geckodriver.exe");
		// System.setProperty("webdriver.firefox.bin",
		// "D:\\Softwares\\firefox\\firefox.exe");
		boolean openedFlag = false;
		while (!openedFlag) {

			java.net.Proxy proxy = proxys.nextRandom();
			String[] proxyarray = proxy.toString().split(":");
			String proxyHost = proxyarray[0].split("/")[1];
			int proxyPort = Integer.valueOf(proxyarray[1]);

			// 测试可用性
			System.err.println("test proxy " + proxy);
			try {
				Proxy tmpproxy = new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
				URL u = new URL("http://www.ccgp.gov.cn/");
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
				if (!out.toString().contains("<title>中国政府采购网_首页</title>")) {
					proxys.remove(proxy);
					System.err.println("remove proxy : " + proxy);
					continue;
				}
			} catch (Exception e) {
				proxys.remove(proxy);
				System.err.println("remove proxy : " + proxy);
				continue;
			}
			System.err.println("use proxy : " + proxy);

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
			profile.setPreference("permissions.default.image", 2);
			WebDriver driver = new FirefoxDriver(profile);

			try {
				String lasturl = "http://search.ccgp.gov.cn/dataB.jsp?searchtype=1&page_index=" + i
						+ "&buyerName=&projectId=&dbselect=infox&kw=&start_time=2016%3A01%3A01&end_time=2016%3A06%3A29&timeType=6&bidSort=2&pinMu=0&bidType=1&displayZone=&zoneId=&pppStatus=&agentName=";
				driver.get(lasturl);
				Thread.sleep(10 * 1000);
				while (i <= endIndex) {
					System.err.println(i + "th url is :" + driver.getCurrentUrl());
					final int flag = i;
					org.jsoup.nodes.Document doc = Jsoup.parse(driver.getPageSource());
					Elements elements = doc.getElementsByAttribute("href");

					String linkContent = "";
					for (Element element : elements) {
						if (element.attr("href").matches("http://www.ccgp.gov.cn/cggg/.*htm")) {
							System.out.println(element.attr("href"));

							linkContent += element.attr("href") + '\n';
						}
					}
					FileIO.saveintoFile("E:/data/china/links/" + i, linkContent);

					WebElement nextButton = driver.findElement(By.className("next"));
					lasturl = driver.getCurrentUrl();
					nextButton.click();
					int sleepLongth = 5000 + (int) (Math.random() * 5000);
					Thread.sleep(sleepLongth);
					int retryTimes = 0;
					while (driver.getCurrentUrl().equals(lasturl)) {

						retryTimes++;
						Thread.sleep(1000);
						if (retryTimes % 10 == 0) {
							System.err.println("wait " + retryTimes + "s");
							if (retryTimes == 60)
								nextButton.click();
							if (retryTimes >= 120)
								throw new Exception("waited 60s for nextButton");
						}
					}
					lasturl = driver.getCurrentUrl();
					i++;
				}
				Thread.sleep(1000);
				openedFlag = true;
			} catch (Exception e) {
				driver.close();
				driver.quit();
				openedFlag = false;
				proxys.remove(proxy);
				e.printStackTrace();
				int tmpCashTime = cashTime.getAndIncrement();
				System.err.println("cash time = " + tmpCashTime);
			}

		}
		System.out.println("Thread " + Thread.currentThread().getName() + "end");
		countDownLatch.countDown();
	}

}
