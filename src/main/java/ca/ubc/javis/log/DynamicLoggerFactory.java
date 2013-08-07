package ca.ubc.javis.log;

import java.util.concurrent.ExecutionException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DynamicLoggerFactory {

	private static final org.slf4j.Logger LOG = LoggerFactory
	        .getLogger(DynamicLoggerFactory.class);
	private static final String TARGET_PATH = "target/sitelog/";
	private static final String LOG_PATTERN =
	        "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
	private static final String LOG_PREFIX = "site-";
	private static final LoggerContext LOGGER_CONTEXT = (LoggerContext) LoggerFactory
	        .getILoggerFactory();
	private static final LoadingCache<String, Logger> CACHE;

	static {
		CACHE = CacheBuilder.newBuilder().build(new CacheLoader<String, Logger>() {

			@Override
			public Logger load(String url) {
				return newLogForSite(url);
			}

		});
	}

	/**
	 * @param url
	 *            The URL of the site for which the logger should be created.
	 * @return a logger with its own file appender according to the site name. If the logger already
	 *         exists that logger is returned.
	 */
	public static org.slf4j.Logger getLoggerForSite(String url) {
		try {
			return CACHE.get(url);
		} catch (ExecutionException e) {
			LOG.warn("Could not load the logger for {}. Returning normal logger", url, e);
			return LoggerFactory.getLogger(LOG_PREFIX + url);

		}
	}

	private static Logger newLogForSite(String url) {
		FileAppender<ILoggingEvent> fileAppender = newFileAppender(url);

		// attach the rolling file appender to the logger of your choice
		Logger logbackLogger = LOGGER_CONTEXT.getLogger(url);
		logbackLogger.addAppender(fileAppender);
		logbackLogger.setLevel(Level.TRACE);

		return logbackLogger;
	}

	private static FileAppender<ILoggingEvent> newFileAppender(String url) {
		FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
		fileAppender.setContext(LOGGER_CONTEXT);
		fileAppender.setName(LOG_PREFIX + url);
		// set the file name
		fileAppender.setFile(TARGET_PATH + url + ".log");

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(LOGGER_CONTEXT);
		encoder.setPattern(LOG_PATTERN);
		encoder.start();

		fileAppender.setEncoder(encoder);
		fileAppender.start();
		return fileAppender;
	}

}