/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnLabel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.validators.IBANValidator;
import com.temenos.ebank.common.wicket.validators.RegexValidator;
import com.temenos.ebank.exceptions.EbankValidationException;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;

/**
 * Existing accounts panel
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ExistingAccountsPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExistingAccountsPanel(String id, final IModel<CustomerWicketModel> model) {
		super(id, model);
		CustomerWicketModel c = model.getObject();

		final WebMarkupContainer groupExistingAccDetailsContainer = new WebMarkupContainer("groupExistingAccDetails");
		//for new application the customer or the property isExistingCustomer are not set, so carefull with npe-s
		boolean isExistingCustomer = c != null && Boolean.TRUE.equals(c.getIsExistingCustomer());
		groupExistingAccDetailsContainer.setVisible(isExistingCustomer);
		groupExistingAccDetailsContainer.setOutputMarkupPlaceholderTag(true);
		add(groupExistingAccDetailsContainer);

		final RadioGroup rgExistingCustomer = new RadioGroup("isExistingCustomer", new PropertyModel(model,
				"isExistingCustomer"));
		rgExistingCustomer.setRequired(true);
		Radio radioExistingCustomerYes = new Radio("existingCustomerYes", new Model(true));
		rgExistingCustomer.add(radioExistingCustomerYes);
		rgExistingCustomer.add(addResourceLabelAndReturnLabel(radioExistingCustomerYes));

		Radio radioExistingCustomerNo = new Radio("existingCustomerNo", new Model(false));
		rgExistingCustomer.add(radioExistingCustomerNo);
		rgExistingCustomer.add(addResourceLabelAndReturnLabel(radioExistingCustomerNo));
		add(addResourceLabelAndReturnBorder(rgExistingCustomer));

		rgExistingCustomer.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isExistingCustomer = (Boolean) rgExistingCustomer.getModelObject();
				groupExistingAccDetailsContainer.setVisible(isExistingCustomer);
				model.getObject().setExistingSortCode(null);
				model.getObject().setExistingAccNumber(null);
				model.getObject().setExistingBic(null);
				model.getObject().setExistingIban(null);
				target.addComponent(groupExistingAccDetailsContainer);
			}
		});

		FormComponent txtExistingSortCode = new EbankTextField("existingSortCode", true);
		txtExistingSortCode.add(new RegexValidator(RegexValidator.DIGITS, EbankValidationException.SORT_CODE_INVALID));
		Border border = addResourceLabelAndReturnBorder(txtExistingSortCode);
		groupExistingAccDetailsContainer.add(border);

		FormComponent txtExistingAccNumber = new EbankTextField("existingAccNumber", true);
		txtExistingAccNumber.add(new RegexValidator(RegexValidator.DIGITS, EbankValidationException.ACCOUNT_NUMBER_INVALID));
		border = addResourceLabelAndReturnBorder(txtExistingAccNumber);
		groupExistingAccDetailsContainer.add(border);

		FormComponent txtExistingBIC = new EbankTextField("existingBic", true);
		txtExistingBIC.add(new RegexValidator(RegexValidator.BIC, EbankValidationException.BIC_INVALID));
		border = addResourceLabelAndReturnBorder(txtExistingBIC);
		groupExistingAccDetailsContainer.add(border);

		FormComponent txtExistingIBAN = new EbankTextField("existingIban", true);
		txtExistingIBAN.add(new IBANValidator());
		border = addResourceLabelAndReturnBorder(txtExistingIBAN);
		groupExistingAccDetailsContainer.add(border);

	}
}
