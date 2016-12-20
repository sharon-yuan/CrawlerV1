package com.Suirui.CrawlerV1.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

public class ProxyController {
	public static final String Sychonizedkey = "key";

	public static void addGoodProxyToDir(String aProxy) {
		while (aProxy.startsWith("/") || aProxy.startsWith("\\")) {
			aProxy = aProxy.substring(1);
		}

		BufferedWriter output;
		try {
			File tempF = new File(Config.PROXY_DIR + MD5Util.MD5(aProxy));
			if (tempF.exists())
				return;
			output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempF), "utf-8"));
			output.write(aProxy);
			output.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<String[]> readerProxyFromDir() {
		List<String[]> ansList = new ArrayList<>();
		File proxyDir = new File(Config.PROXY_DIR);
		if (!proxyDir.isDirectory()) {
			System.err.println("proxy dir doesn't exist!");
			return null;
		}
		System.out.println(proxyDir.getAbsolutePath());
		File[] filelist = proxyDir.listFiles();
		for (File tempF : filelist) {
			ansList.addAll(ProxyReader.getproxy(tempF.getPath()));

		}
		System.out.println(ansList.size());
		return ansList;
	}

	public static List<String[]> readerProxyFromFile(String filename) {
		List<String[]> ansList = new ArrayList<>();
		File proxyDir = new File(Config.PROXY_DIR);
		if (!proxyDir.isDirectory()) {
			System.err.println("proxy dir doesn't exist!");
			return null;
		}
		System.out.println(Thread.currentThread().getName() +"read proxy from" + proxyDir.getAbsolutePath() + "\\" + filename);
		System.out.println(proxyDir.getAbsolutePath());
		// File file = new File(proxyDir.getAbsolutePath()+"\\"+filename);

		ansList.addAll(ProxyReader.getproxy(proxyDir.getAbsolutePath() + "\\" + filename));
		System.out.println("proxyfile path at" + proxyDir.getAbsolutePath() + "\\" + filename);

		System.out.println("proxyfile size" + ansList.size());
		return ansList;
	}

	public static Proxys readProxyFromWeb() {
		synchronized (Sychonizedkey) {
			Proxys ansList = new Proxys();
			URL u = null;
			try {

				u = new URL("http://api.xicidaili.com/free2016.txt");
				HttpURLConnection con = (HttpURLConnection) u.openConnection();
				con.setConnectTimeout(3000);
				con.setReadTimeout(3000);
				InputStream inStrm = con.getInputStream();
				StringBuffer out = new StringBuffer();
				byte[] b = new byte[4096];

				for (int n; (n = inStrm.read(b)) != -1;) {

					out.append(new String(b, 0, n));
				}
				String[] strings = out.toString().split("\n");
				for (String tempString : strings) {
					ansList.add(tempString.substring(0, tempString.length() - 1));
					// System.out.println(tempString.substring(0,
					// tempString.length()-1));
				}
			} catch (Exception e) {

				System.err.println(Thread.currentThread().getName() +" readerProxyFromWeb " + u.toString() + "打不开打不开打不开！！！");

			}
			System.out.println("read proxy at " + new Date().toString());
			return ansList;
		}
	}

	public static String readAPI() {

		synchronized (Sychonizedkey) {
			URL u = null;
			try {

				u = new URL("http://api.xicidaili.com/free2016.txt");
				HttpURLConnection con = (HttpURLConnection) u.openConnection();
				con.setConnectTimeout(3000);
				con.setReadTimeout(3000);
				InputStream inStrm = con.getInputStream();
				StringBuffer out = new StringBuffer();
				byte[] b = new byte[4096];

				for (int n; (n = inStrm.read(b)) != -1;) {

					out.append(new String(b, 0, n));
				}
				System.out.println("read " + u.toString() + " at " + new Date().toString());
				if (out.toString().contains("503 Service Temporarily Unavailable"))
					return null;
				return out.toString();
			} catch (Exception e) {

				System.err.println("readerFromWeb " + u.toString() + "打不开打不开打不开！！！");

			}

			return null;

		}
	}
	public static boolean testProxy(Proxy proxy) {

		String[] proxyarray = proxy.toString().split(":");
		String proxyHost = proxyarray[0].split("/")[1];
		int proxyPort = Integer.valueOf(proxyarray[1]);

		// 测试可用性
		System.out.println(Thread.currentThread().getName() +" test proxy " + proxy);

		TelnetClient telnetClient = new TelnetClient("vt200");
		// 指明Telnet终端类型，否则会返回来的数据中文会乱码
		telnetClient.setDefaultTimeout(1000); // socket延迟时间：1000ms
	
		try {
			telnetClient.connect(proxyHost, proxyPort);
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " telnetClient failed at "+proxy);
			return false;
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

				System.err.println(Thread.currentThread().getName() +" 并不匿名！！！: " + tmpproxy);
				return false;
			} else {
				System.out.println(Thread.currentThread().getName() + " 测试ip网页返回长度： " + out.toString().length());
				return true;
			}

		} catch (Exception e) {

			System.err.println(Thread.currentThread().getName() +" 站长ip都打不开！要你何用！！：" + proxy);

			return false;
		}

		

	}
}
