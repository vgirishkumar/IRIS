package com.temenos.interaction.translate.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.anyLong;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kubek2k.springockito.annotations.BeanNameStrategy;
import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.kubek2k.springockito.annotations.SpringockitoContextLoader;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.temenos.interaction.core.loader.Action;
import com.temenos.interaction.core.loader.FileEvent;
import com.temenos.interaction.loader.detector.DirectoryChangeActionNotifier;
import com.temenos.interaction.loader.detector.DirectoryChangeActionNotifier.ListenerNotificationTask;
import com.temenos.interaction.springdsl.StateRegisteration;
import com.temenos.interaction.translate.loader.RIMResourceStateProvider;
import com.temenos.interaction.translate.loader.ResourceSetGeneratorAction;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring-translate-config.xml"})
public class TestInteractionTranslate extends AbstractJUnit4SpringContextTests {

	/*@Autowired
	@Resource(name = "serviceRootFactory")
	@Spy
	private StateRegisteration registeration;
	
	@Autowired
	@Resource(name = "updateAfterChangeRIMAction")
	@Spy
	private ResourceSetGeneratorAction resourceSetGeneratorAction;
	
	@Autowired
	@Resource(name = "resourceStateProvider")
	@Spy
	private RIMResourceStateProvider resourceStateProvider;
	
	@Autowired
	@Resource(name = "rimDirWatchdog")
	@Spy
	private DirectoryChangeActionNotifier notifier;
	
	@Autowired
	@ReplaceWithMock(beanNameStrategy = BeanNameStrategy.FIELD_NAME)
	private ScheduledExecutorService executorService;
	
	@Mock
	private ScheduledFuture<?> scheduledFuture;
	
	@Mock
	private WatchService watchService;*/
	
	@Before
	public void setUp() throws Exception {
		/*final File hotDeployDir = new File("classpath:test-hotdeploy-dir/myNewRim.rim");
		final ListenerNotificationTask notificationTask = mock(ListenerNotificationTask.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				resourceSetGeneratorAction.execute(
					new DirectoryChangeActionNotifier.DirectoryChangeEvent(hotDeployDir)
				);
				return null;
			}
		}).when(notificationTask).run();
		doAnswer(new Answer<Void>(){
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				notificationTask.run();
				return null;
			}
		}).doReturn(scheduledFuture).when(executorService)
			.scheduleWithFixedDelay(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class));*/
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateUpdateResource() throws IOException {
		//given
		List<Action<FileEvent<File>>> listeners = new ArrayList<Action<FileEvent<File>>>();
		//listeners.add(resourceSetGeneratorAction);
		//when
		//notifier.setListeners(listeners);
		//then
		//verify();
	}

}
