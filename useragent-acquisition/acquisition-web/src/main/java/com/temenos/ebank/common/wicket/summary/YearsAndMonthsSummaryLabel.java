package com.temenos.ebank.common.wicket.summary;

import com.temenos.ebank.pages.clientAquisition.step2.YearsAndMonths;

/**
 * @author raduf
 *         Label component used for displaying the value of a YearsAndMonths component within a summary page.
 *         The purpose of this class is setting the defaultModelObject to what we want to display in the label,
 *         taking data from the model of the original input field.
 */
public class YearsAndMonthsSummaryLabel extends SummaryLabel {

	private static final long serialVersionUID = 1L;
	private YearsAndMonths yearsAndMonths;

	public YearsAndMonthsSummaryLabel(YearsAndMonths yearsAndMonths) {
		super(yearsAndMonths.getId());
		this.yearsAndMonths = yearsAndMonths;
	}

	@Override
	protected Object getSummaryLabelModelObject() {
		return yearsAndMonths.getDisplayValue();
	}

}
