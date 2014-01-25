package com.temenos.ebank.pages.clientAquisition.wizard;

import java.util.Arrays;
import java.util.List;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.step1.Step1CA;
import com.temenos.ebank.pages.clientAquisition.step2.Step2;
import com.temenos.ebank.pages.clientAquisition.step3.Step3;
import com.temenos.ebank.pages.clientAquisition.step4.Step4CA;
import com.temenos.ebank.pages.clientAquisition.step5.Step5;
import com.temenos.ebank.pages.clientAquisition.step6.Step6;

public class CAWizard extends ClientAquisitionWizard {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CAWizard(String id, Application a) {
		super(id, a);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected List getProductSpecificSteps() {
		return Arrays.asList(new Class[] { Step1CA.class, Step2.class, Step3.class,
				Step4CA.class, Step5.class, Step6.class });
	}

	@Override
	protected ProductType getProductType() {
		return ProductType.INTERNATIONAL;
	}

}
