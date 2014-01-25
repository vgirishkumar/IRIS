/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnLabel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Panel for displaying single/joint radio group
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class SingleJointPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SingleJointPanel(String id, IModel<ApplicationWicketModelObject> model, final Component [] secondCustomerPanels, final Component[] componentsToUpdate) {
		super(id, model);
		//ApplicationWicketModelObject a = model.getObject();
		final RadioGroup accountSoleTypeRG = new RadioGroup("isSole", new PropertyModel<Boolean>(model, "isSole"));
		Radio radioJoinedSole = new Radio("sole", new Model(true));
		accountSoleTypeRG.add(radioJoinedSole);
		accountSoleTypeRG.add(addResourceLabelAndReturnLabel(radioJoinedSole));
		Radio radioJoinedJoined = new Radio("joint", new Model(false));
		accountSoleTypeRG.add(radioJoinedJoined);
		accountSoleTypeRG.add(addResourceLabelAndReturnLabel(radioJoinedJoined));
		add(addResourceLabelAndReturnBorder(accountSoleTypeRG));
		accountSoleTypeRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isJointAccount = !(Boolean) accountSoleTypeRG.getModelObject();
				for (Component secondCustomerPanel : secondCustomerPanels) {
					secondCustomerPanel.setVisible(isJointAccount);
					target.addComponent(secondCustomerPanel);
				}
				for (Component componentToUpdate : componentsToUpdate)
				target.addComponent(componentToUpdate);
			}
		});
		
	}
}
