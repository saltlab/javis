package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ubc.javis.log.DynamicLoggerFactory;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.condition.UrlCondition;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.BrowserConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.CrawljaxConfiguration.CrawljaxConfigurationBuilder;
import com.crawljax.core.state.StateVertex;
import com.crawljax.core.state.StateVertexTest;

public class JavisRunner {

	private static final int MAX_CRAWL_DEPTH = 3;
	private static final int MAX_STATES = 5;
	private static final Logger URL_LOGGER = LoggerFactory.getLogger("URL-logfile");
	private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(JavisRunner.class);

	public static int cons = 0, counter = 0;
	public static String URL;
	public static String path = "/ubc/ece/home/am/grads/janab/JavisResults/";
	public static String logPath = "./target/javis.log";
	public static long startTime;
	public static String name;

	private static void startCrawl(String URLstring) {
		CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(URLstring);
		
		builder.crawlRules().click("div");
		builder.crawlRules().click("a");
		builder.crawlRules().click("img");
		builder.crawlRules().click("span");
		builder.crawlRules().click("button");
		builder.crawlRules().click("input").withAttribute("type", "button");
		builder.crawlRules().dontClick("form");
		builder.crawlRules().dontClick("iframe");
		
		builder.crawlRules().setRandomize(true);
		
		builder.setMaximumRunTime(5, TimeUnit.HOURS);
		builder.setMaximumDepth(MAX_CRAWL_DEPTH);
		builder.setMaximumStates(MAX_STATES);
		
		String urlName = name.substring(1, name.length());
		builder.crawlRules().addCrawlCondition("Only crawl this random URL", new UrlCondition(urlName));

		builder.setBrowserConfig(new BrowserConfiguration(BrowserType.FIREFOX));
		
		Logger siteLog = DynamicLoggerFactory.getLoggerForSite(URLstring);

		GetCandidateElements getCandidateElements = new GetCandidateElements(siteLog);
		builder.addPlugin(getCandidateElements);

		Javis javis = new Javis(siteLog);
		builder.addPlugin(javis);
		
		CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
		crawljax.call();
	}

	public static void main(String[] args) throws IOException {
		String[] urlArray = new String[400];
		urlArray = GetUrls.getArray("src//main//resources//Alexa.txt", 400);
		for (int i = 21; i < 22; i++) {
			getName(urlArray[i]);
			startTime = System.currentTimeMillis();
			File file = new File(path + i + name);
			file.mkdir();
			File totalContentFile = new File(path + i + name + "/TotalContent.txt");
			totalContentFile.createNewFile();
			File totalChangeFile = new File(path + i + name + "/TotalChangeResultLog.txt");
			totalChangeFile.createNewFile();
			clearProperties();
			counter = i;
			System.setProperty("webdriver.firefox.bin",
			        "//ubc//ece//home//am//grads//janab//Firefox19//firefox//firefox");
			startCrawl(urlArray[i]);
		}

	}

	private static void getName(String URLstring) {
		String initialization = "", resticting = "";
		int first = 0, second = 0;
		initialization = URLstring;
		first = initialization.indexOf(".");
		if (initialization.contains(".org"))
			second = initialization.indexOf("org");
		else if (initialization.contains(".ru"))
			second = URLstring.indexOf(".ru");
		else if (initialization.contains(".ca"))
			second = URLstring.indexOf(".ca");
		else if (initialization.contains(".co"))
			second = URLstring.indexOf(".co");
		else if (initialization.contains(".ly"))
			second = URLstring.indexOf(".ly");
		else if (initialization.contains(".se"))
			second = URLstring.indexOf(".se");
		else
			second = URLstring.indexOf(".com");
		resticting = initialization.substring(first + 1, second);
		name = "-".concat(resticting);

	}

	private static void clearProperties() {
		GetDomDifferences.buffer.delete(0, GetDomDifferences.buffer.length());
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
