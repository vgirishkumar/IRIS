package com.temenos.interaction.rimdsl.generator.launcher;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetup;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetupSpringPRD;

public class GeneratorTest {
	private File validGenJavaDir = new File("target/valid-gen-java");
	private File invalidGenJavaDir = new File("target/invalid-gen-java");
	
	
	@Before
	public void setUp() throws Exception {
		createNewDir(validGenJavaDir);
		createNewDir(invalidGenJavaDir);		
	}

	private void createNewDir(File dir) {
		if(dir.exists()) {
			dir.delete();
		}
		
		dir.mkdirs();
	}
	
	@Test
	public void testStandaloneWithoutListenerValidRim() {
		Injector injector = new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
				
		boolean result = generator.runGenerator("src/test/resources/valid.rim", validGenJavaDir.getPath());
		
		assertTrue(result);
	}
	

	@Test
	public void testStandaloneSpringPRDWithoutListenerValidRim() {
		Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
				
		boolean result = generator.runGenerator("src/test/resources/valid.rim", validGenJavaDir.getPath());
		
		assertTrue(result);
	}
	

	@Test
	public void testStandaloneListenerWithValidRim() {
		Injector injector = new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		
		ValidatorEventListener listener = mock(ValidatorEventListener.class);
		
		generator.setValidatorEventListener(listener);
		
		generator.runGenerator("src/test/resources/valid.rim", validGenJavaDir.getPath());
		
		verify(listener, times(0)).notify(anyString());
	}

	
	@Test
	public void testStandaloneSpringPRDListenerWithRestbucksRim() {
		Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		
		ValidatorEventListener listener = mock(ValidatorEventListener.class);
		
		generator.setValidatorEventListener(listener);
		
		generator.runGenerator("src/test/resources/Restbucks.rim", validGenJavaDir.getPath());
		
		verify(listener, times(0)).notify(anyString());
	}

	
	@Test
	public void testStandaloneListenerWithInvalidRim() {
		Injector injector = new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		
		ValidatorEventListener listener = mock(ValidatorEventListener.class);
		
		generator.setValidatorEventListener(listener);
		
		generator.runGenerator("src/test/resources/invalid.rim", invalidGenJavaDir.getPath());
		
		verify(listener, times(1)).notify(anyString());
	}
	
	@Test
	public void testStandaloneSpringPRDListenerWithInvalidRim() {
		Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		
		ValidatorEventListener listener = mock(ValidatorEventListener.class);
		
		generator.setValidatorEventListener(listener);
		
		generator.runGenerator("src/test/resources/invalid.rim", invalidGenJavaDir.getPath());
		
		verify(listener, times(1)).notify(anyString());
	}

	@Test
	public void testStandaloneSpringPRDListenerWithValidRim() {
		Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
		Generator generator = injector.getInstance(Generator.class);
		
		ValidatorEventListener listener = mock(ValidatorEventListener.class);
		
		generator.setValidatorEventListener(listener);
		
		generator.runGenerator("src/test/resources/valid.rim", validGenJavaDir.getPath());
		
		verify(listener, times(0)).notify(anyString());
	}	
}
