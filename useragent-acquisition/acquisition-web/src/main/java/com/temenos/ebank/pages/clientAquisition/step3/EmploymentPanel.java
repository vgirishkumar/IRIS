package com.temenos.ebank.pages.clientAquisition.step3;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.MinimumValidator;

import com.temenos.ebank.common.wicket.IPartiallySavable;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.pages.clientAquisition.step2.PafAddressPanel;
import com.temenos.ebank.pages.clientAquisition.step2.YearsAndMonths;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EmploymentPanel extends Panel implements IPartiallySavable {
	private static final long serialVersionUID = -7929609786734527065L;

	private final static String EMPLOYED = "EMPLOYED";
	private final static String SELF = "SELF";
	private final static String OTHER = "OTHER";

	public EmploymentPanel(String id, final IModel<CustomerWicketModel> model) {
		super(id, model);

		IGenericChoiceRenderer employmentStatusRenderer = GenericChoiceRendererFactory.getRenderer(Choices.employmentStatus,
				this);
		final EbankDropDownChoice cmbStatus = new EbankDropDownChoice("employmentStatus", employmentStatusRenderer.getChoices(), employmentStatusRenderer);
		cmbStatus.setRequired(true);
		Border border = addResourceLabelAndReturnBorder(cmbStatus);
		add(border);

		boolean isEmployed = EMPLOYED.equals(model.getObject().getEmploymentStatus());
		boolean isSelf = SELF.equals(model.getObject().getEmploymentStatus());
		boolean isOther = OTHER.equals(model.getObject().getEmploymentStatus());

		// other employment status, text, max 40, mandatory if OTHER, invisible otherwise
		final FormComponent txtOtherEmploymentStatus = new EbankTextField("otherEmploymentStatus");
		final Component borderOtherEmployment = addResourceLabelAndReturnBorder(txtOtherEmploymentStatus)
				.setOutputMarkupId(true) // to be able to toggle
				.setOutputMarkupPlaceholderTag(true) // to be able to have the invisible placeholder
				.setVisible(isOther); // initial visibility
		add(borderOtherEmployment);

		// instantiate the address, if necessary
		if (isEmployed) {
			initEmployerAddress(model.getObject());
		}

		final WebMarkupContainer selfEmploymentDetailsContainer = new WebMarkupContainer(
				"selfEmploymentDetailsContainer");
		selfEmploymentDetailsContainer.setVisible(isSelf);
		selfEmploymentDetailsContainer.setOutputMarkupId(true);
		selfEmploymentDetailsContainer.setOutputMarkupPlaceholderTag(true);
		add(selfEmploymentDetailsContainer);

		final WebMarkupContainer employmentDetailsContainer = new WebMarkupContainer("employmentDetailsContainer");
		employmentDetailsContainer.setVisible(isEmployed);
		employmentDetailsContainer.setOutputMarkupId(true);
		employmentDetailsContainer.setOutputMarkupPlaceholderTag(true);
		add(employmentDetailsContainer);

		final FormComponent txtOccupation = new EbankTextField("occupation");
		border = addResourceLabelAndReturnBorder(txtOccupation);
		selfEmploymentDetailsContainer.add(border);

		final FormComponent txtEmployerName = new EbankTextField("employerName");
		border = addResourceLabelAndReturnBorder(txtEmployerName);
		employmentDetailsContainer.add(border);

		final YearsAndMonths employmentLastDuration = new YearsAndMonths("employmentLastDuration", new PropertyModel(
				model, "employmentLastDuration"));
		employmentLastDuration.setRequired(true);
		employmentLastDuration.add(new MinimumValidator<Integer>(1));
		employmentDetailsContainer.add(addResourceLabelAndReturnBorder(employmentLastDuration));

		final PafAddressPanel addressPanel = new PafAddressPanel("employerAddress", new CompoundPropertyModel(
				new PropertyModel(model, "employerAddress")));
		employmentDetailsContainer.add(addressPanel);

		// we add the ajaxupdating behavior after declaring the actual fields that are affected by it.
		cmbStatus.add(toggleEmploymentDetailsBehavior(model, cmbStatus, borderOtherEmployment,
				employmentDetailsContainer, selfEmploymentDetailsContainer));
	}

	public List<String> getObligatoryComponentsIds() {
		return Arrays.asList("employmentStatus");
	}

	private static void initEmployerAddress(CustomerWicketModel customerWicketModel) {
		// the customer should not be null at this point; let the NPE rip.
		if (customerWicketModel.getEmployerAddress() == null) {
			customerWicketModel.setEmployerAddress(new AddressWicketModelObject());
		}
	}

	private AjaxFormComponentUpdatingBehavior toggleEmploymentDetailsBehavior(final IModel<CustomerWicketModel> model,
			final EbankDropDownChoice cmbStatus, final Component otherEmploymentContainer,
			final WebMarkupContainer employmentDetailsContainer, final WebMarkupContainer selfEmploymentDetailsContainer) {

		return new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				toggleOtherEmployment(model, otherEmploymentContainer, target);

				boolean isEmployed = EMPLOYED.equals(cmbStatus.getModelValue());
				boolean isSelf = SELF.equals(cmbStatus.getModelValue());

				if (isEmployed) {
					initEmployerAddress(model.getObject());
					target.addComponent(employmentDetailsContainer.setVisible(true));
					target.addComponent(selfEmploymentDetailsContainer.setVisible(false));
				} else if (isSelf) {
					target.addComponent(employmentDetailsContainer.setVisible(false));
					target.addComponent(selfEmploymentDetailsContainer.setVisible(true));
				} else {
					// TODO reset the fields now, or just before the submit ? see
					// http://stackoverflow.com/questions/2957839/wicket-can-a-panel-or-component-react-on-a-form-submit-without-any-boilerplate
					model.getObject().setOccupation(null);
					model.getObject().setEmployerName(null);
					model.getObject().setEmployerAddress(null);
					model.getObject().setEmploymentLastDuration(null);
					target.addComponent(employmentDetailsContainer.setVisible(false));
					target.addComponent(selfEmploymentDetailsContainer.setVisible(false));
				}
			}

			private void toggleOtherEmployment(final IModel<CustomerWicketModel> model,
					final Component otherEmploymentContainer, AjaxRequestTarget target) {
				boolean isOther = OTHER.equals(model.getObject().getEmploymentStatus());
				if (otherEmploymentContainer.isVisible() ^ isOther) {
					if (!isOther) {
						// reset the value. see TODO above about whether this is appropriate
						model.getObject().setOtherEmploymentStatus(null);
					}
					otherEmploymentContainer.setVisible(isOther);
					target.addComponent(otherEmploymentContainer);
				}
			}
		};
	}
}
