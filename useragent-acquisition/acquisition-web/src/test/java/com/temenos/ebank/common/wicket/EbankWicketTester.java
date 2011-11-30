package com.temenos.ebank.common.wicket;

import org.apache.wicket.util.tester.WicketTester;

import com.temenos.ebank.wicket.EbankWicketApplication;

public class EbankWicketTester extends WicketTester {

	public EbankWicketTester(EbankWicketApplication ebankApp) {
		super(ebankApp, "src/main/webapp/");
	}
}
