package com.temenos.interaction.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import org.junit.Test;

public class TestResponderGen {

	@Test
	public void testSourceGenerationFromEDMX() {
	    InputStream is = getClass().getResourceAsStream("/edmx.xml");
	    File targetDir = new File("./target/integration-test");
	    targetDir.mkdir();
	    
		JPAResponderGen rg = new JPAResponderGen();
		assertTrue(rg.generateArtifacts(is, targetDir));
		
		assertEquals(6, countFiles(new File(targetDir, "AirlineModel")));
		assertEquals(1, countFiles(new File(targetDir, "META-INF")));
	}
	
	private int countFiles(File dir) {
		int cnt = 0;
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				cnt += countFiles(files[i]);
			} else {
				cnt++;
			}
		}
		return cnt;
	}
}
