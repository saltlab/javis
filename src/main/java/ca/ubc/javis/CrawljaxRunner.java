package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.condition.UrlCondition;
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;

/**
 * @authors:janab
 */
public class CrawljaxRunner {

	/**
	 * @param args
	 */
	private static final int MAX_CRAWL_DEPTH = 3;
	private static final int MAX_STATES = 5;
	private static final Logger URL_LOGGER = LoggerFactory.getLogger("URL-logfile");
	private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(CrawljaxRunner.class);

	public static int cons = 0, counter = 0;
	public static String URL;
	public static String path = "//ubc//ece//home//am//grads//janab//JavisResults//";
	public static String javaPath = "//ubc//ece//home//am//grads//janab//Github//javis//";
	public static long startTime;

	private static void GetTrace(Exception e) {
		StackTraceElement elements[] = e.getStackTrace();
		for (int i = 0, n = elements.length; i < n; i++) {
			String str =
			        (elements[i].getFileName() + ":" + elements[i].getLineNumber() + ">> "
			                + elements[i].getMethodName() + "()");
			ERROR_LOGGER.warn(str);
		}
	}

	private static CrawlSpecification getCrawlSpecification(String URLstring) {
		URL = URLstring;
		String randomUrl = "";
		java.net.URI uri;
		HttpURLConnection conn = null;
		try {

			uri = new java.net.URI(URLstring);
			conn = (HttpURLConnection) uri.toURL().openConnection();

			try {
				int r = conn.getResponseCode();
				randomUrl = conn.getURL().toString();
			} catch (IOException e) {

				ERROR_LOGGER.warn(e.toString());
				GetTrace(e);
				e.printStackTrace();
			}
			URL_LOGGER.info("URL" + (cons + 1) + ": " + randomUrl);

			System.out.println(randomUrl + "----------");

			conn.connect();
		} catch (Exception e) {

			ERROR_LOGGER.warn(e.toString());
			GetTrace(e);

		}
		CrawlSpecification crawler = new CrawlSpecification(URLstring);

		crawler.click("div");
		crawler.click("a");
		crawler.click("button");
		crawler.click("img");
		crawler.click("span");
		crawler.click("input").withAttribute("type", "button");
		crawler.dontClick("form");

		// limit the crawling scope
		crawler.setMaximumStates(MAX_STATES);
		crawler.setDepth(MAX_CRAWL_DEPTH);
		crawler.setMaximumRuntime(10800);

		crawler.setInputSpecification(getInputSpecification());
		String initialization = "", resticting = "";
		int first = 0, second = 0;
		initialization = URLstring;
		first = initialization.indexOf(".");
		if (initialization.contains("org"))
			second = initialization.indexOf("org");
		else if (initialization.contains(".ru"))
			second = URLstring.indexOf(".ru");
		else
			second = URLstring.indexOf(".com");
		resticting = initialization.substring(first + 1, second - 1);
		crawler.addCrawlCondition("Only crawl this random URL", new UrlCondition(resticting));

		return crawler;

	}

	private static InputSpecification getInputSpecification() {
		InputSpecification input = new InputSpecification();
		input.field("q").setValue("Crawljax");
		return input;
	}

	private static CrawljaxConfiguration getConfig(String URLstring) {
		CrawljaxConfiguration crawljaxConfiguration = new CrawljaxConfiguration();
		crawljaxConfiguration.setBrowser(BrowserType.firefox);
		crawljaxConfiguration.setCrawlSpecification(getCrawlSpecification(URLstring));

		GetCandidateElements getCandidateElements = new GetCandidateElements();
		crawljaxConfiguration.addPlugin(getCandidateElements);

		Javis javis = new Javis();
		crawljaxConfiguration.addPlugin(javis);

		return crawljaxConfiguration;
	}

	public static void main(String[] args) {

		System.out.println("start");
		String[] urlArray = new String[400];
		urlArray =
		        GetUrls.getArray("//ubc//ece//home//am//grads//janab//Desktop//Alexa.txt", 400);
		for (int i = 20; i < 21; i++) {
			try {
				startTime = System.currentTimeMillis();
				File file = new File(path + i);
				file.mkdir();
				clearProperties();

				counter = i;

				System.setProperty("webdriver.firefox.bin",
				        "//ubc//ece//home//am//grads//janab//Firefox10//firefox//firefox");
				CrawljaxController crawljax = new CrawljaxController(getConfig(urlArray[i]));

				System.out.println("success 1");

				crawljax.run();
				System.out.println("success 2");

				File logFile = new File(javaPath + "log4j.log");
				logFile.renameTo(new File(path + i + "//log4j.log"));

			} catch (CrawljaxException e) {

				ERROR_LOGGER.warn(e.toString());
				GetTrace(e);
			} catch (ConfigurationException e) {

				ERROR_LOGGER.warn(e.toString());
				GetTrace(e);
			}

			System.out.println("end of loop: " + (1));
			System.out.println("success 3");

		}

	}

	private static void clearProperties() {
		Javis.setVisibleEdge(0);
		Javis.setInvisibleEdge(0);
		Javis.setInvisibleState(0);
		Javis.setVisibleState(0);
		Javis.setDivCounter(0);
		Javis.setAInvisCounter(0);
		Javis.setAVisCounter(0);
		Javis.setButtonCounter(0);
		Javis.setImgVisCounter(0);
		Javis.setInputCounter(0);
		Javis.setSpanCounter(0);
		Javis.setImgInisCounter(0);
		for (int i = 0; i < Javis.visiblearray.length; i++)
			Javis.visiblearray[i] = "";
		for (int i = 0; i < Javis.invisiblearray.length; i++)
			Javis.invisiblearray[i] = "";
	}

}
