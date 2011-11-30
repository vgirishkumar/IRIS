package com.temenos.ebank.pages.crossSell;

import com.temenos.ebank.domain.ProductType;

public class CrossSellPageFactory {

	public static CSAccountOptionsPage buildCrossSellPage(ProductType productType) {
		CSAccountOptionsPage page = null;
		switch( productType ){
		case IBSA:
			page = new CSAccountOptionsIBSAPage();
			break;
		case RASA:
			page = new CSAccountOptionsRASAPage();
			break;
		case INTERNATIONAL:
			page = new CSAccountOptionsCAPage();
			break;
		case FIXED_TERM_DEPOSIT:
			page = new CSAccountOptionsFTDPage();
			break;
		case REGULAR_SAVER:
			page = new CSAccountOptionsRSPage();
			break;
		}
		return page;
	}

}
