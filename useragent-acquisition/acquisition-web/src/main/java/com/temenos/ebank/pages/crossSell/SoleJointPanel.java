package com.temenos.ebank.pages.crossSell;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnLabel;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.AddLabelAndBorderOptions;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

public class SoleJointPanel extends Panel {
	private static final long serialVersionUID = 5622777219362163159L;
	private static List<String> requiredFieldsIdsForSave = Arrays.asList("isSole");
	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	public SoleJointPanel(String id, IModel<ApplicationWicketModelObject> model, final Application previousApplication,
			final Application crossSellApplication, final Component groupSecondApplicantWebContainer,
			final Component[] componentsToUpdate) {
		super(id, model);
		
		final RadioGroup accountSoleTypeRG = new RadioGroup("isSole", new PropertyModel<Boolean>(model, "isSole"));
		Radio radioJoinedSole = new Radio("sole", new Model(true));
		accountSoleTypeRG.add(addResourceLabelAndReturnLabel(radioJoinedSole));
		accountSoleTypeRG.add(radioJoinedSole);

		Radio radioJoinedJoined = new Radio("joint", new Model(false));
		accountSoleTypeRG.add(addResourceLabelAndReturnLabel(radioJoinedJoined));
		accountSoleTypeRG.add(radioJoinedJoined);
		add(addResourceLabelAndReturnBorder(accountSoleTypeRG, new AddLabelAndBorderOptions().setVisibleFieldInfo(false).setVisibleHint(true)));
		accountSoleTypeRG.setRequired(true);
		
		//Label isSoleLabel = new Label("isSoleLabel", new StringResourceModel("isSoleLabel", this, null,
		//		new String[] { previousApplication.getSecondCustomer().getFirstName() + " "
		//				+ previousApplication.getSecondCustomer().getLastName() }));
		//add(isSoleLabel);
		accountSoleTypeRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isJointAccount = !(Boolean) accountSoleTypeRG.getModelObject();
				boolean secondCustomerDataNeeded = ProductType.INTERNATIONAL.getCode().equals(crossSellApplication.getProductRef())
												   && isJointAccount;
				crossSellApplication.setSecondCustomer(secondCustomerDataNeeded ? previousApplication.getSecondCustomer() : null);
				groupSecondApplicantWebContainer.setVisible(isJointAccount);
				target.addComponent(groupSecondApplicantWebContainer);
				if (componentsToUpdate != null) {
					componentsToUpdate[0].setVisible(isJointAccount);
					target.addComponent(componentsToUpdate[0]);
				}
			}
		});

	}
	public List<String> getObligatoryComponentsIds() {
		return requiredFieldsIdsForSave;
	}
}
