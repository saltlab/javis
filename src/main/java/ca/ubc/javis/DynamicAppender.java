package ca.ubc.javis;

import java.io.File;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;


public class DynamicAppender {

	public ch.qos.logback.classic.Logger createLogger(String name) {
        ch.qos.logback.classic.Logger templateLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("javis.log");
        LoggerContext context = templateLogger.getLoggerContext();

        DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> timeBasedTriggeringPolicy = new DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent>();
        timeBasedTriggeringPolicy.setContext(context);

        String logDir = context.getProperty("HOME_PATH");
        
        TimeBasedRollingPolicy<ILoggingEvent> timeBasedRollingPolicy = new TimeBasedRollingPolicy<ILoggingEvent>();
        timeBasedRollingPolicy.setContext(context);
        timeBasedRollingPolicy.setFileNamePattern(logDir + name + ".log.");
        timeBasedRollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(timeBasedTriggeringPolicy);
        timeBasedTriggeringPolicy.setTimeBasedRollingPolicy(timeBasedRollingPolicy);

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<ILoggingEvent>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(context);
        rollingFileAppender.setFile(logDir + name + ".log");
        rollingFileAppender.setName(name + "Appender");
        rollingFileAppender.setPrudent(false);
        rollingFileAppender.setRollingPolicy(timeBasedRollingPolicy);
        rollingFileAppender.setTriggeringPolicy(timeBasedTriggeringPolicy);

        timeBasedRollingPolicy.setParent(rollingFileAppender);

        timeBasedRollingPolicy.start();

        rollingFileAppender.stop();
        rollingFileAppender.start();
        
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(name);
        logbackLogger.setLevel(templateLogger.getLevel());
 
        return logbackLogger;
	}
	
	public void initializeLogback(String path) {
    File logbackFile = new File(path);
    System.setProperty("logback.configurationFile", logbackFile.getAbsolutePath());
    StaticLoggerBinder loggerBinder = StaticLoggerBinder.getSingleton();
    LoggerContext loggerContext = (LoggerContext) loggerBinder.getLoggerFactory();

    loggerContext.reset();
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(loggerContext);
    try {
        configurator.doConfigure(logbackFile);
    } catch( JoranException e ) {
        
    }
}
}
