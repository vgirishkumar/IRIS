package com.temenos.ebank.common.wicket;

import org.junit.Test;

public class NewApplicationTest extends EbankPageTest {

	@Test
	public void singleApplicationInternationalAccountStep1(){
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());
	}

	@Test
	public void singleApplicationFixedTermDepositStep1(){
		loadNewApplicationForProduct( ProductPagesAndLinks.FIXED_TERM_DEPOSIT );
	}

	@Test
	public void singleApplicationRegularSaverStep1(){
		loadNewApplicationForProduct( ProductPagesAndLinks.REGULAR_SAVER );
	}

	@Test
	public void singleApplicationIASAStep1(){
		loadNewApplicationForProduct( ProductPagesAndLinks.IASA );
	}

	@Test
	public void singleApplicationRASAStep1(){
		loadNewApplicationForProduct( ProductPagesAndLinks.RASA );
	}

	@Test
	public void singleApplicationIBSAStep1(){
		loadNewApplicationForProduct( ProductPagesAndLinks.IBSA );
	}

}
