package com.Suirui.CrawlerV1.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;

import org.apache.commons.net.telnet.TelnetClient;

import com.Suirui.CrawlerV1.util.FileIO;
import com.Suirui.CrawlerV1.util.MD5Util;
import com.Suirui.CrawlerV1.util.ProxyController;

public class ProxyThread implements Runnable {

	@Override
	public void run() {

		while (true) {
			String tempfileName = MD5Util.MD5(new Date().toString());
			String proxyAPIString = ProxyController.readAPI();
			while (proxyAPIString == null)
				try {
					System.out.println("proxy 线程sleep 60s");
					Thread.sleep(1000 * 60);
					proxyAPIString = ProxyController.readAPI();
				} catch (InterruptedException e1) {
					System.out.println("proxythread failed at sleep 60s");
					e1.printStackTrace();
					continue;
				}
			FileIO.saveintoFile("E:/data/china/proxy/" + tempfileName, proxyAPIString);
			Level2Crawler.proxys.addAll(FileIO.getLinesArray("E:/data/china/proxy/" + tempfileName));
			System.out.println("ProxyThread read proxy!save it into: " + "E:/data/china/proxy/" + tempfileName);
			MutiThreadCrawler.filenameString = tempfileName;
			try {
				System.out.println("ProxyThread sleep 15min");
				Thread.sleep(1000 * 60 * 15);
			} catch (InterruptedException e) {
				System.out.println("ProxyThread failed at sleep 60*15s");
				e.printStackTrace();
			}
			if (Thread.activeCount() <= 1) {
				try {
					Thread.sleep(1000 * 60 * 5);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
				if (Thread.activeCount() <= 1)
					return;
			}
		}

	}

	

}
