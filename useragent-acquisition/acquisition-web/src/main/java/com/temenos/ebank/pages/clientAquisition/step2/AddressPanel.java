package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.formValidation.DefaultTextFieldFormBehavior.INIT_DEFAULT_TEXT_JS_CALL;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.IPartiallySavable;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.formValidation.MaxLengthValidationBehavior;
import com.temenos.ebank.common.wicket.formValidation.MinLengthValidationBehavior;
import com.temenos.ebank.common.wicket.formValidation.RequiredValidationBehavior;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.ConfigParamTable.STRING;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;

public class AddressPanel extends Panel implements IPartiallySavable {

	private static final long serialVersionUID = -9102663151661097506L;

	private AjaxLink<?> searchAgainBt;
	private EbankDropDownChoice<String> cmbCountryOResidence;

	private EbankTextField postCodeTextField;

	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;
	
	@SuppressWarnings("serial")
	public AddressPanel(String id, final IModel<AddressWicketModelObject> model, final PafAddressPanel parentPanel) {
		super(id, model);

		if (!EbankWizardStep.getIsSummary()) {
			add(new HiddenField<String>("adrId"));
		}
		add(addResourceLabelAndReturnBorder(new EbankTextField("line1")));
		add(addResourceLabelAndReturnBorder(new EbankTextField("line2", true)));
		add(addResourceLabelAndReturnBorder(new EbankTextField("county", true)));
		Border districtBorder = addResourceLabelAndReturnBorder(new EbankTextField("district", true));
		add(districtBorder.setVisible(false));
		add(addResourceLabelAndReturnBorder(new EbankTextField("town")));

		IGenericChoiceRenderer countriesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.COUNTRY, this);
		cmbCountryOResidence = new EbankDropDownChoice<String>("country", countriesRenderer.getChoices(), countriesRenderer);
		add(addResourceLabelAndReturnBorder(cmbCountryOResidence));

		Form<AddressWicketModelObject> searchAgainForm = new Form<AddressWicketModelObject>("searchAgainForm");
		postCodeTextField = new EbankTextField("postcode");
		//postcode is required only if country is the configured one
		postCodeTextField.setRequired(serviceConfigParam.getConfigParamTable().get(STRING.PAF_POSTCODE_MANDATORY_COUNTRY).equals(model.getObject().getCountry()));
		postCodeTextField.add( new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				//the behaviour was added so that model gets updated
				//this way, when the containing border is refreshed, 
				//the updated model is painted instead of an old one
			}
		});
		
		final Border postcodeBorder = addResourceLabelAndReturnBorder(postCodeTextField);
		//we want to update this through AJAX, output the markup id
		postcodeBorder.setOutputMarkupId(true);
		searchAgainForm.add(postcodeBorder);

		cmbCountryOResidence.setRequired(true);
		cmbCountryOResidence.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				postCodeTextField.getConvertedInput();
				boolean postCodeRequired = serviceConfigParam.getConfigParamTable().get(STRING.PAF_POSTCODE_MANDATORY_COUNTRY).equals(cmbCountryOResidence.getModelObject());
				postCodeTextField.setRequired(postCodeRequired);
				target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
				//TODO: client side validation is not triggering for the post code 
				//after the containing border was refreshed through AJAX
				//target.appendJavascript("ebankValidator.valid();");
				target.addComponent(postcodeBorder);
			}
		});
		
		searchAgainBt = new AjaxLink<Object>("searchAgainBt") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				//if switch to list is wanted, uncomment this
				//parentPanel.refreshAddressList(target);
				//if switching to initial view - search panel
				parentPanel.switchToSearch( target );
			}
		};

		//search again is always visible, comment following
		//searchAgainBt.setVisible(parentPanel != null);
		searchAgainForm.add(searchAgainBt);
		
		// in edit mode, we show the form. in summary mode, we just show the post code value
		if (!EbankWizardStep.getIsSummary()) {
			add(searchAgainForm);
		} else {
			add(postcodeBorder);
		}
	}
	
	public AddressPanel(String id, final IModel<AddressWicketModelObject> model) {
		this(id, model, null);
	}

	public List<String> getObligatoryComponentsIds() {
		return Arrays.asList("line1", "town", "country");
	}
	
	@Override
	public void onModelChanged() {
		String country = ((AddressWicketModelObject) getDefaultModel().getObject()).getCountry();
		cmbCountryOResidence.setModel(new Model<String>(country));
		//country was changed from the PAF lookup, set post code required accordingly
		postCodeTextField.setRequired(serviceConfigParam.getConfigParamTable().get(STRING.PAF_POSTCODE_MANDATORY_COUNTRY).equals(country));
	}

	//search again is always visible, comment following
/*	public void hideSearchAgainButton() {
		searchAgainBt.setVisible(false);
	}
*/
	public void postCodeNotRequired() {
		postCodeTextField.setRequired(false);
		List<IBehavior> behaviors = postCodeTextField.getBehaviors();
		for (IBehavior behavior : behaviors) {
			if (behavior instanceof RequiredValidationBehavior || behavior instanceof MinLengthValidationBehavior
					|| behavior instanceof MaxLengthValidationBehavior) {
				postCodeTextField.remove(behavior);
			}
		}
	}

}
