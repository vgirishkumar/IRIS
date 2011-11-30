package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.AddLabelAndBorderOptions;
import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.components.CompositeFormComponent;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;

public class YearsAndMonths extends FormComponentPanel<Integer> implements CompositeFormComponent {

	private static final long serialVersionUID = -1860884375002626999L;

	private static List<Integer> years = new ArrayList<Integer>();
	private static List<Integer> months = new ArrayList<Integer>();
	static {
		for (int i = 0; i < 100; i++)
			years.add(i);
		for (int i = 0; i < 12; i++)
			months.add(i);
	}

	private Integer nrYears;
	private Integer nrMonths;

	private final EbankDropDownChoice nrYearsDDC;
	private final EbankDropDownChoice nrMonthsDDC;

	public YearsAndMonths(String id, IModel<Integer> model) {
		super(id, model);

		setType(Integer.class);

		nrYearsDDC = new EbankDropDownChoice<Integer>("nrYears", new PropertyModel<Integer>(this, "nrYears"), years);
		nrYearsDDC.setRequired(true);
		add(addResourceLabelAndReturnBorder(nrYearsDDC, new AddLabelAndBorderOptions().setIsContainedInComposite(true)));
		nrMonthsDDC = new EbankDropDownChoice<Integer>("nrMonths", new PropertyModel<Integer>(this, "nrMonths"), months);
		nrMonthsDDC.setRequired(true);
		add(addResourceLabelAndReturnBorder(nrMonthsDDC, new AddLabelAndBorderOptions().setIsContainedInComposite(true)));
	}

	@Override
	protected void onBeforeRender() {

		getYearsAndMonthsFromModelObject();
		super.onBeforeRender();
	}

	private void getYearsAndMonthsFromModelObject() {
		Integer totalMonths = getModelObject();

		if (totalMonths != null) {
			nrYears = totalMonths / 12;
			nrMonths = totalMonths % 12;
		}
	}

	@Override
	protected void convertInput() {
		Integer previousValue = getModelObject() != null ? getModelObject() : 0;
		Integer newValue = 0;
		if (WicketUtils.isFormProcessingEnabled(this)) {
			nrYears = (Integer) nrYearsDDC.getConvertedInput();
			nrMonths = (Integer) nrMonthsDDC.getConvertedInput();
		} else {
			nrYears = Integer.valueOf(nrYearsDDC.getInput());
			nrMonths = Integer.valueOf(nrMonthsDDC.getInput());
		}
		if (nrYears != null) {
			if (nrMonths != null)
				newValue = nrYears * 12 + nrMonths;
			else {
				newValue = nrYears * 12 + previousValue % 12;
			}
		} else if (nrMonths != null) {
			newValue = previousValue - previousValue % 12 + nrMonths;
		} else {
			newValue = null;
		}
		setConvertedInput(newValue);
		setModelObject(newValue);
	}

	@Override
	public String getInput() {
		return nrYearsDDC.getInput() + nrMonthsDDC.getInput();
	}

	public void setNrYears(Integer nrYears) {
		this.nrYears = nrYears;
	}

	public Integer getNrYears() {
		return nrYears;
	}

	public void setNrMonths(Integer nrMonths) {
		this.nrMonths = nrMonths;
	}

	public Integer getNrMonths() {
		return nrMonths;
	}

	private final class OnDurationChangedAjaxBehaviour extends OnChangeAjaxBehavior {
		private static final long serialVersionUID = 1L;
		private final ContactDetailsPanel contactDetailsPanel;

		private OnDurationChangedAjaxBehaviour(ContactDetailsPanel contactDetailsPanel) {
			this.contactDetailsPanel = contactDetailsPanel;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			convertInput();

			contactDetailsPanel.currentAddressDurationChanged(target);
		}
	}

	public void addShowHidePreviousAddressesListBehavior(ContactDetailsPanel contactDetailsPanel) {
		OnChangeAjaxBehavior onChangeAjaxBehaviourY = new OnDurationChangedAjaxBehaviour(contactDetailsPanel);
		nrYearsDDC.add(onChangeAjaxBehaviourY);
		// WICKET says get new behavior instance for attaching to another component
		OnChangeAjaxBehavior onChangeAjaxBehaviourM = new OnDurationChangedAjaxBehaviour(contactDetailsPanel);
		nrMonthsDDC.add(onChangeAjaxBehaviourM);
	}

	private final class OnPreviousDurationChangedAjaxBehaviour extends OnChangeAjaxBehavior {
		private static final long serialVersionUID = 1L;
		private final ContactDetailsPanel contactDetailsPanel;
		private String containerMarkupId;

		private OnPreviousDurationChangedAjaxBehaviour(ContactDetailsPanel contactDetailsPanel, String containerMarkupId) {
			this.contactDetailsPanel = contactDetailsPanel;
			this.containerMarkupId = containerMarkupId;
		}

		@Override
		protected void onUpdate(AjaxRequestTarget target) {
			convertInput();

			// notify parent about the change to add new previous address if necessary
			contactDetailsPanel.previousAddressDurationChanged(target, containerMarkupId);
		}
	}

	public void addPreviousAddressBehavior(final ContactDetailsPanel contactDetailsPanel, String containerMarkupId) {
		OnChangeAjaxBehavior onChangeAjaxBehaviour = new OnPreviousDurationChangedAjaxBehaviour(contactDetailsPanel,
				containerMarkupId);
		nrYearsDDC.add(onChangeAjaxBehaviour);
		// WICKET says get new behavior instance for attaching to another component
		onChangeAjaxBehaviour = new OnPreviousDurationChangedAjaxBehaviour(contactDetailsPanel, containerMarkupId);
		nrMonthsDDC.add(onChangeAjaxBehaviour);
	}

	public String getDisplayValue() {
		getYearsAndMonthsFromModelObject();
		StringBuffer displayValue = new StringBuffer();
		if (nrYears != null && nrYears > 0) {
			displayValue.append(nrYears).append(" years");
		}
		if( nrYears != null && nrYears > 0 && nrMonths != null && nrMonths > 0 ){
			displayValue.append(" and ");
		}
		if (nrMonths != null && nrMonths > 0) {
			displayValue.append(nrMonths).append(" months");
		}
		return displayValue.toString();
	}
	
	public FormComponent<?> getFirstInput() {
		return nrYearsDDC;
	}
}
