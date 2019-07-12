package com.pickleplum.apps.webcrawler;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;
import java.util.ArrayList;

public class WebCrawler {
	
	ArrayList<String> links;
	String hostname;
	int port;
	
	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to Mark's Web Crawler Application");
		
		Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        System.out.println(String.format("Fetching %s...", url));
		
		new WebCrawler().crawl(url);
	}
	
	public WebCrawler() {
		links = new ArrayList<String>();
	}
	
	public void crawl(String url) throws IOException {
		URL rawURL = new URL(url);
		hostname = rawURL.getHost();
		port = rawURL.getPort();
		System.out.println(String.format("Hostname: %s", hostname));
		System.out.println(String.format("Port: %d", port));
		crawl("- ", url);
	}
	
	private void crawl(String sep, String url) throws IOException {
		if (!links.contains(url)) {
        	URL rawURL = new URL(url);
        	if (rawURL.getHost().contentEquals(hostname) && rawURL.getPort() == port) {
        		links.add(url);
        		
        		Document doc = null;
        		try {
        			doc = Jsoup.connect(url).get();
        		} catch (ConnectException ce) {
        			System.out.println(String.format("%s%s (Unable to connect to link, cannot crawl)", sep, url));
        		} catch (HttpStatusException hse) {
        			if (hse.getStatusCode() == 404) {
        				System.out.println(String.format("%s%s (404 - Link not found, cannot crawl)", sep, url));
        			} else {
        				System.out.println(String.format("%s%s (Unable to connect to link, cannot crawl)", sep, url));
        			}
        		}
        		
        		if (doc != null) {
        			System.out.println(sep + url);
        			
        			Elements links = doc.select("a[href]");
    		        
    		        System.out.println(String.format("%sLinks: %d", sep, links.size()));
    		        for (Element link : links) {
    		            crawl("  " + sep, link.attr("abs:href"));
    		        }
        		}
        	} else {
        		System.out.println(String.format("%s%s (External link, will not crawl)", sep, url));
        	}
		} else {
			System.out.println(String.format("%s%s (Already crawled)", sep, url));
		}
	}
}