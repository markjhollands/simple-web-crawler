/*
 * Copyright (c) 2019 Mark Hollands
 */

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

/*
 * @author Mark Hollands
 * 
 * Web Crawler application
 * 
 * Simple web crawler. Given a URL the application will crawl
 * each page, retrieve all links and follow local links.
 * 
 * The application will not crawl the same link twice, as this
 * could cause an infinite loop.
 */
public class WebCrawler {
	
	ArrayList<String> links;
	String hostname;
	int port;
	
	/*
	 * Application entry point
	 * 
	 * @param args Crawler expects and will only use a single
	 *             parameter, the URL to crawl.
	 */
	public static void main(String[] args) throws IOException {
		printMessage("Welcome to Mark's Web Crawler Application");
		
		Validate.isTrue(args.length == 1, "usage: supply url to fetch");
        String url = args[0];
        printMessage(String.format("Fetching %s...", url));
		
		new WebCrawler().crawl(url);
	}
	
	/*
	 * Constructor
	 * 
	 * Initialises links ArrayList
	 */
	public WebCrawler() {
		links = new ArrayList<String>();
	}
	
	/*
	 * crawl() method
	 * 
	 * First crawl method. Validates the URL and stores the
	 * hostname and port number. It will then start the crawl.
	 * 
	 * @param url The URL of the site to crawl
	 */
	public void crawl(String url) throws IOException {
		URL rawURL = new URL(url);
		hostname = rawURL.getHost();
		port = rawURL.getPort();
		printMessage(String.format("Hostname: %s", hostname));
		printMessage(String.format("Port: %d", port));
		crawl("- ", url);
	}
	
	/*
	 * crawl() method
	 * 
	 * Main crawl method. This will be called recursively until
	 * all local links on a site have been crawled once.
	 * 
	 * @param sep Indentation / separation string to indent the
	 *            line to be printed
	 * @param url The URL of the site to crawl
	 */
	private void crawl(String sep, String url) throws IOException {
		if (!links.contains(url)) {
        	URL rawURL = new URL(url);
        	if (rawURL.getHost().contentEquals(hostname) && rawURL.getPort() == port) {
        		links.add(url);
        		
        		Document doc = null;
        		try {
        			doc = Jsoup.connect(url).get();
        		} catch (ConnectException ce) {
        			printLink(sep, url, "Unable to connect to link, cannot crawl");
        		} catch (HttpStatusException hse) {
        			if (hse.getStatusCode() == 404) {
        				printLink(sep, url, "404 - Link not found, cannot crawl");
        			} else {
        				printLink(sep, url, "Unable to connect to link, cannot crawl");
        			}
        		}
        		
        		if (doc != null) {
        			Elements links = doc.select("a[href]");
        			printLink(sep, url, String.format("Links: %d", links.size()));
    		        for (Element link : links) {
    		            crawl("  " + sep, link.attr("abs:href"));
    		        }
        		}
        	} else {
        		printLink(sep, url, "External link, will not crawl");
        	}
		} else {
			printLink(sep, url, "Already crawled");
		}
	}
	
	/*
	 * printLink()
	 * 
	 * Helper method to print the link to the screen.
	 * 
	 * @param indent            Indentation for line
	 * @param link              Link to print out
	 * @param additionalMessage Any additional message to print out
	 *                          Will appear in brackets after link.
	 */
	private void printLink(String indent, String link, String additionalMessage) {
		printMessage(String.format("%s%s (%s)", indent, link, additionalMessage));
	}
	
	/*
	 * printMessage()
	 * 
	 * Simple helper method to print a message. This
	 * implementation will print to STDOUT.
	 * 
	 * @param message Message to be output
	 */
	private static void printMessage(String message) {
		System.out.println(message);
	}
}