package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.condition.UrlCondition;
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;
import com.google.common.io.Files;

public class CrawljaxRunner {

	private static final int MAX_CRAWL_DEPTH = 3;
	private static final int MAX_STATES = 10;
	private static final Logger URL_LOGGER = LoggerFactory.getLogger("URL-logfile");
	private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(CrawljaxRunner.class);

	public static int cons = 0, counter = 0;
	public static String URL;
	public static String path = "/ubc/ece/home/am/grads/janab/JavisResults/";
	public static String logPath = "./target/javis.log";
	public static long startTime;
	public static String name;

	private static CrawlSpecification getCrawlSpecification(String url) {
		URL = url;
		String randomUrl = "";
		java.net.URI uri;
		HttpURLConnection conn = null;
		try {
			uri = new java.net.URI(url);
			conn = (HttpURLConnection) uri.toURL().openConnection();
			try {
				conn.getResponseCode();
				randomUrl = conn.getURL().toString();
			} catch (IOException e) {
				ERROR_LOGGER.warn("Cannot read url {} ", url, e);
			}
			URL_LOGGER.info("URL" + (cons + 1) + ": " + randomUrl);
			conn.connect();
		} catch (Exception e) {
			ERROR_LOGGER.warn("Cannot open URL", e);
		}
		return configuringCrawlSpecification(url);

	}

	private static CrawlSpecification configuringCrawlSpecification(String URLstring) {
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
		crawler.setMaximumRuntime(3, TimeUnit.HOURS);

		crawler.setInputSpecification(getInputSpecification());
		String urlName = name.substring(1, name.length());
		crawler.addCrawlCondition("Only crawl this random URL", new UrlCondition(urlName));

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

	public static void main(String[] args) throws IOException {
		String[] urlArray = new String[400];
		urlArray =
		        GetUrls.getArray("src//main//resources//Alexa.txt", 400);
		for (int i = 20; i < 21; i++) {
			try {
				Files.write("", new File(logPath),Charsets.UTF_8);
				urlArray[i] = "http://www.google.ca";
				getName(urlArray[i]);
				startTime = System.currentTimeMillis();
				File file = new File(path + i + name);
				file.mkdir();
				clearProperties();
				counter = i;
				 System.setProperty("webdriver.firefox.bin",
				 "//ubc//ece//home//am//grads//janab//Firefox18//firefox//firefox");
				CrawljaxController crawljax = new CrawljaxController(getConfig(urlArray[i]));
				crawljax.run();
				File logFile = new File(logPath);
				if(logFile.exists())
					FileUtils.copyFile(logFile,new File(path + i + name +"/"+ logFile.getName()));
			} catch (CrawljaxException e) {
				ERROR_LOGGER.warn("Error in the main loop {}. Continuing...", e.getMessage(), e);
			}

		}

	}

	private static void getName(String URLstring) {
		String initialization = "", resticting = "";
		int first = 0, second = 0;
		initialization = URLstring;
		first = initialization.indexOf(".");
		if (initialization.contains("org"))
			second = initialization.indexOf("org");
		else if (initialization.contains(".ru"))
			second = URLstring.indexOf(".ru");
		else if (initialization.contains(".ca"))
			second = URLstring.indexOf(".ca");
		else
			second = URLstring.indexOf(".com");
		resticting = initialization.substring(first + 1, second);
		name = "-".concat(resticting);

	}

	private static void clearProperties() {
		Javis.sfgInformation.getVisibleEdge().set(0);
		Javis.sfgInformation.getInvisibleEdge().set(0);
		Javis.sfgInformation.getInvisibleState().set(0);
		Javis.sfgInformation.getVisibleState().set(0);
		Javis.sfgInformation.getDivCounter().set(0);
		Javis.sfgInformation.getAInvisCounter().set(0);
		Javis.sfgInformation.getAVisCounter().set(0);
		Javis.sfgInformation.getButtonCounter().set(0);
		Javis.sfgInformation.getImgVisCounter().set(0);
		Javis.sfgInformation.getInputCounter().set(0);
		Javis.sfgInformation.getSpanCounter().set(0);
		Javis.sfgInformation.getImgInvisCounter().set(0);
		Javis.visiblearray.clear();
		Javis.invisiblearray.clear();
		Javis.stateCondition.clear();
	}

}
