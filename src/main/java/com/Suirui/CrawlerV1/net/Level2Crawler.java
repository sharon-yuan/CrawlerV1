package com.Suirui.CrawlerV1.net;
import java.util.List;

import com.Suirui.CrawlerV1.net.Level2Thread;
import com.Suirui.CrawlerV1.net.ProxyThread;
import com.Suirui.CrawlerV1.util.ProxyController;
import com.Suirui.CrawlerV1.util.Proxys;

public class Level2Crawler {
public static	Proxys proxys = new Proxys();
	public static void main(String[] args){
		System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:2.0b13pre) Gecko/20110307 Firefox/4.0b13pre");
		
		List<String[]> proxyList = ProxyController.readerProxyFromFile("1.txt");
		for (String[] tempProxy : proxyList) {
			proxys.add(tempProxy[0], Integer.valueOf(tempProxy[1]));
		}
		  ProxyThread proxThread=new ProxyThread();;
	       Thread filereader=new Thread(proxThread);
	       filereader.start();
		int threadsCount=20;
		int lengthPerThread=401;
		Level2Thread[] l2rt=new Level2Thread[threadsCount];
		for (int i = 0; i < threadsCount; i++) {
			l2rt[i]=new Level2Thread();
			l2rt[i].setStartIndex(i*lengthPerThread+1);
			l2rt[i].setEndIndex((i+1)*lengthPerThread);
			Thread tmp =new Thread(l2rt[i] ,"Thread" +i);
			tmp.start();
			}
	
	}

}
