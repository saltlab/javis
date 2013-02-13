package ca.ubc.javis;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.io.FileUtils;

import com.crawljax.browser.EmbeddedBrowser.BrowserType;
import com.crawljax.condition.UrlCondition;
import com.crawljax.core.CrawljaxController;
import com.crawljax.core.CrawljaxException;
import com.crawljax.core.configuration.CrawlSpecification;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.configuration.InputSpecification;
/**
 * @authors:janab
 *
 */
public class CrawljaxRunner  {

	/**
	 * @param args
	 */
	private static final int MAX_CRAWL_DEPTH = 3;
	private static final int MAX_STATES = 50;
	private static Logger urlLogger = Logger.getLogger("URL-logfile");
	private static Logger errorLogger = Logger.getLogger(CrawljaxRunner.class.getName());
	public static int cons=0,counter=0;
	public static String URL;
	
	private static void GetTrace(Exception e)
	{
		StackTraceElement elements[] = e.getStackTrace();
	    for (int i = 0, n = elements.length; i < n; i++) {       
	        String str=(elements[i].getFileName()
	            + ":" + elements[i].getLineNumber() 
	            + ">> "
	             + elements[i].getMethodName() + "()");
	        errorLogger.log(Level.WARNING,str);
	    }
	}
	private static CrawlSpecification getCrawlSpecification(String URLstring) {
		URL = URLstring;
		String  randomUrl = "";
		java.net.URI uri;
		HttpURLConnection conn = null;
		FileHandler myFileHandler = null;
		FileHandler Xmlfh = null;
		try {
				
			uri = new java.net.URI (URLstring);
			conn  = (HttpURLConnection) uri.toURL().openConnection();
			urlLogger.setLevel(Level.ALL);
			
			try {
				myFileHandler = new FileHandler("URLLog.txt");
				myFileHandler.setFormatter(new SimpleFormatter());
				
				Xmlfh = new FileHandler("urlLog.xml");
				Xmlfh.setFormatter(new XMLFormatter());
			} catch (SecurityException e) {
				
				errorLogger.log(Level.WARNING, e.toString());
				GetTrace(e);
				e.printStackTrace();
			} catch (IOException e) {
				
				errorLogger.log(Level.WARNING, e.toString());
				GetTrace(e);
				e.printStackTrace();
			}
			urlLogger.addHandler(myFileHandler);
			urlLogger.addHandler(Xmlfh);
			int r;
	        try {
				
				r = conn.getResponseCode();
				randomUrl = conn.getURL().toString();
			} catch (IOException e) {
				
				errorLogger.log(Level.WARNING, e.toString());
				GetTrace(e);
				e.printStackTrace();
			}
			urlLogger.log(Level.INFO,"URL"+(cons+1)+": "+ randomUrl);
			
			System.out.println(randomUrl+"----------");
			
	        conn.connect();
		} catch (Exception e) {
			
			errorLogger.log(Level.WARNING, e.toString());
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
		String initialization="",resticting="";
		int first=0,second=0;
		initialization = URLstring;
		first = initialization.indexOf(".");
		if(initialization.contains("org"))
			second = initialization.indexOf("org");
		else
			second = URLstring.indexOf(".com");
		resticting = initialization.substring(first+1,second-1);
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
		
		errorLogger.setLevel(Level.ALL);
		FileHandler fl=null;
		FileHandler xml=null;
		try{
			fl=new FileHandler("ErrorLog.txt");
			fl.setFormatter(new SimpleFormatter());
			xml=new FileHandler("ErrorLog.xml");
			xml.setFormatter(new XMLFormatter());
		} catch (SecurityException e) {
			
			errorLogger.log(Level.WARNING, e.toString());
			GetTrace(e);
	
		} catch (IOException e) {
			
			errorLogger.log(Level.WARNING, e.toString());
			GetTrace(e);
	
		}
		
		errorLogger.addHandler(fl);
		errorLogger.addHandler(xml);
	
		System.out.println("start");
		String[] urlArray= new String[400];
		urlArray=GetUrls.getArray("//ubc//ece//home//am//grads//janab//Desktop//Alexa.txt",400);
		for(int i=15;i<16;i++){
			try {
				
				counter=i;
				
				System.setProperty("webdriver.firefox.bin" ,"//ubc//ece//home//am//grads//janab//Firefox10//firefox//firefox" );
				CrawljaxController crawljax = new CrawljaxController(getConfig(urlArray[i]));
				
				System.out.println("success 1");

				crawljax.run(); 
				Copying(i);
				System.out.println("success 2");

			} catch (CrawljaxException e) {
				
				errorLogger.log(Level.WARNING, e.toString());
				GetTrace(e);
			} catch (ConfigurationException e) {
				
				errorLogger.log(Level.WARNING, e.toString());
				GetTrace(e);
			}
			
			fl.flush();
			fl.close();
			xml.flush();
			xml.close();
			
			System.out.println("end of loop: " + (1));
			System.out.println("success 3");
			

		}
			
	}
	private static void Copying(int i) {
		File file = new File("//ubc//ece//home//am//grads//janab//JavisResults//"+i);
		file.mkdir();	
		File source = new File("/ubc/ece/home/am/grads/janab/Github/javis/");
		File dest = new File("//ubc//ece//home//am//grads//janab//JavisResults//"+i);
		try {
		    FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	
		
	}
	

}
