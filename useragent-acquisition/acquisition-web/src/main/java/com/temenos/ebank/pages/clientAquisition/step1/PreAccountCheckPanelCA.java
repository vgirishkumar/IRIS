/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankCheckBoxMultipleChoice;
import com.temenos.ebank.common.wicket.validators.AccountCurrenciesValidator;
import com.temenos.ebank.domain.ConfigParamTable.INTEGER;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Pre-account check panel for current account subscription
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class PreAccountCheckPanelCA extends PreAccountCheckPanel {
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;


	/**
	 * used by Step1
	 * 
	 * @param id
	 * @param s
	 * @param model
	 * @param singleJointPanel
	 */
	public PreAccountCheckPanelCA(String id, IModel<ApplicationWicketModelObject> model,
			Panel singleJointPanel) {
		super(id, model, ProductType.INTERNATIONAL);
		IGenericChoiceRenderer choiceRenderer = GenericChoiceRendererFactory.getRenderer(
				getProductCurrencyPropertyName(), this);

		//TODO: aparent nu mai am nevoie de prefix si sufix, de inlocuit si la ibsa 
		final EbankCheckBoxMultipleChoice chkAccountCurrencies = new EbankCheckBoxMultipleChoice("accountCurrencies",
				new PropertyModel(getDefaultModel(), "accountCurrencies"),
				choiceRenderer.getChoices(), choiceRenderer) {

//			private int i = 0;
//			@Override
//			public String getPrefix() {
//				i++;
//				return (i % 2 == 0 ? "" : "<div class = \"checkGroupRow\">") + "<div class=\"checkGroupCell\">";
//			}
//
//			@Override
//			public String getSuffix() {
//				return "</div>" + (i % 2 != 0 ? "" : "</div>");
//			}
		};
		Integer maxNoOfAccounts = serviceConfigParam.getConfigParamTable().get(INTEGER.MAX_NO_ACCOUNTS);
		chkAccountCurrencies.setRequired(true);
		chkAccountCurrencies.add(new AccountCurrenciesValidator(maxNoOfAccounts));
		add(addResourceLabelAndReturnBorder(chkAccountCurrencies));
		//inserts the supplied singleJointPanel into this panel; only done while in Step1
		if (singleJointPanel != null) {
			add(singleJointPanel);
		} else {
			add(new EmptyPanel("singleJoint"));
		}
	}
}
