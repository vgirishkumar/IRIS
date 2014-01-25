/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankRadioChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.services.interfaces.nomencl.IServiceNomencl;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Pre-account check panel for current account subscription
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PreAccountCheckPanelFTD extends PreAccountCheckPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<String> currencies;
	private Map<String, Map<String, BigDecimal>> termsAndRatesByCurrency = new HashMap<String, Map<String, BigDecimal>>();

	@SpringBean
	private IServiceNomencl serviceNomencl;

	@SuppressWarnings("serial")
	public PreAccountCheckPanelFTD(String id, IModel<ApplicationWicketModelObject> model,
			Panel singleJointPanel) {
		super(id, model, ProductType.FIXED_TERM_DEPOSIT);
		ApplicationWicketModelObject a = model.getObject();

		IGenericChoiceRenderer currencyRenderer = GenericChoiceRendererFactory.getRenderer(
				getProductCurrencyPropertyName(), this);
		currencies = currencyRenderer.getChoices();
		final EbankRadioChoice rcCurrency = new EbankRadioChoice("accountCurrency", currencies, currencyRenderer);
		rcCurrency.setRequired(true);
		Border border = addResourceLabelAndReturnBorder(rcCurrency);
		add(border);

		String selectedCurrency = StringUtils.defaultIfEmpty(a.getAccountCurrency(), currencies.get(0));

		IGenericChoiceRenderer termsRenderer = GenericChoiceRendererFactory.getRenderer(Choices.ftdTerm, this);
		List<String> terms = getTerms(selectedCurrency);
		final EbankDropDownChoice cmbTerm = new EbankDropDownChoice("ftdTerm", terms, termsRenderer);
		cmbTerm.setRequired(true);
		border = addResourceLabelAndReturnBorder(cmbTerm);
		add(border);

		final FormComponent txtFtdInterestRate = new EbankTextField("ftdInterestRate", true);
		txtFtdInterestRate.setEnabled(false);
		border = addResourceLabelAndReturnBorder(txtFtdInterestRate);
		add(border);

		rcCurrency.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				String currency = (String) rcCurrency.getModelObject();
				cmbTerm.setChoices(PreAccountCheckPanelFTD.this.getTerms(currency));
				target.addComponent(cmbTerm);
				if (refreshInterestRate(rcCurrency, cmbTerm, txtFtdInterestRate)) {
					target.addComponent(txtFtdInterestRate);
				}
			}
		});

		cmbTerm.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (refreshInterestRate(rcCurrency, cmbTerm, txtFtdInterestRate)) {
					target.addComponent(txtFtdInterestRate);
				}
			}
		});

		if (singleJointPanel != null) {
			add(singleJointPanel);
		} else {
			add(new EmptyPanel("singleJoint"));
		}

	}

	/**
	 * Reads a Map of terms and rates for the given currency and caches it in the termsAndRatesByCurrency map
	 * 
	 * @param currency
	 * @return
	 */
	private Map<String, BigDecimal> getTermsAndRates(String currency) {
		Map<String, BigDecimal> ftdTermsAndRates;
		if (termsAndRatesByCurrency.containsKey(currency)) {
			ftdTermsAndRates = termsAndRatesByCurrency.get(currency);
		} else {
			ftdTermsAndRates = serviceNomencl.getFTDTermsAndRates(currency);
			termsAndRatesByCurrency.put(currency, ftdTermsAndRates);
		}
		return ftdTermsAndRates;
	}
	
	private List<String> getTerms(String currency){
		Set<String> termsSet = getTermsAndRates(currency).keySet();
		List<String> termsArr = new ArrayList<String>(termsSet.size());
		for( String term : termsSet ){
			termsArr.add(term);
		}
		return termsArr;
	}
	
	private BigDecimal getInterestRate(String currency, String term) {
		return getTermsAndRates(currency).get(term);
	}

	/**
	 * Recalculates the interest rate. Returns true if the interest rate needs refresh, false otherwise
	 * 
	 * @param rcCurrency
	 * @param cmbTerm
	 * @param txtFtdInterestRate
	 * 
	 */
	private boolean refreshInterestRate(final RadioChoice rcCurrency, final EbankDropDownChoice cmbTerm,
			final FormComponent txtFtdInterestRate) {
		String currency = (String) rcCurrency.getModelObject();
		String term = (String) cmbTerm.getModelObject();
		if (StringUtils.isBlank(currency) || StringUtils.isBlank(term)) {
			BigDecimal currentInterestRate = (BigDecimal) txtFtdInterestRate.getModelObject();
			if (currentInterestRate != null) {
				txtFtdInterestRate.setModelObject(BigDecimal.ZERO);
			} else {
				return false;
			}
		} else {
			txtFtdInterestRate.setModelObject(PreAccountCheckPanelFTD.this.getInterestRate(currency,
					term));
		}
		return true;
	}

}
