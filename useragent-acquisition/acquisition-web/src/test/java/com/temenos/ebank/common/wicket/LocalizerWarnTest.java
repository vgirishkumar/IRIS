package com.temenos.ebank.common.wicket;

import static org.mockito.Mockito.reset;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.wicket.Localizer;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that internationalization is initialized properly.
 * @author acirlomanu
 */
// this test depends on the actual logging framework - log4j; if it changes, this needs to change as well.
public class LocalizerWarnTest extends EbankPageTest {

	/**
	 * Custom appender attached to the {@link Localizer} logger
	 */
	TestAppender appender;

	@Before
	@Override
	public void setUp() {
		super.setUp();
		
		appender = new TestAppender();
		Logger logger = Logger.getLogger(Localizer.class);
        logger.addAppender(appender);
	}
	
	@After
	@Override
	public void tearDown() {
		super.tearDown();
		
		Logger logger = Logger.getLogger(Localizer.class);
		logger.removeAppender(appender);
	}

	/*
	 * We resume an existing application and check that all the page components are internationalized properly, without
	 * any warning in the respective log.
	 */
	@Test
	public void testLocalizerWarnings() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("5DdZow6O", 6);
		// test the presence of warning in the logs
		for (LoggingEvent event : appender.getLog()) {
			Assert.assertThat("Unexpected WARN message :" + event.getRenderedMessage(), event.getLevel(), CoreMatchers.not(Level.WARN));
		}
	}

	/**
	 * @see http://stackoverflow.com/questions/1827677/how-to-do-a-junit-assert-on-a-message-in-a-logger
	 */
	class TestAppender extends AppenderSkeleton {
	    private final List<LoggingEvent> log = new ArrayList<LoggingEvent>();

	    public boolean requiresLayout() {
	        return false;
	    }

	    @Override
	    protected void append(final LoggingEvent loggingEvent) {
	        log.add(loggingEvent);
	    }

	    public void close() {
	    }

	    public List<LoggingEvent> getLog() {
	        return new ArrayList<LoggingEvent>(log);
	    }
	}
}
