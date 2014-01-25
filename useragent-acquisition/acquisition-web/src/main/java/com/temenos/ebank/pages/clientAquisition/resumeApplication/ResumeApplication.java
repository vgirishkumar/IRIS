package com.temenos.ebank.pages.clientAquisition.resumeApplication;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.common.wicket.formValidation.CompositeErrorsAndInfosFeedbackPanel;
import com.temenos.ebank.common.wicket.formValidation.EmailValidationBehavior;
import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.FTDWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IBSAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RSWizardPage;
import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion;

@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
public class ResumeApplication extends BasePage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "serviceClientAcquisition")
	private IServiceClientAquistion serviceClientAcquisition;
	
	public ResumeApplication() {
		final FormComponent<String> txtRefno = new EbankTextField("refno", new Model<String>());
		final FormComponent<String> txtEmailAddress = new EbankTextField("emailAddress", new Model<String>());
		final Form frm = new Form("form") {
			@Override
			protected void onSubmit() {
				String reference = txtRefno.getModelObject();
				String email = txtEmailAddress.getModelObject();
				AcquisitionResponse acquisitionResponse = serviceClientAcquisition.getApplicationByReferenceAndEmail(reference, email);
				if (acquisitionResponse.getResponseCode().isOk()) {
					Application a = acquisitionResponse.getApplication();
					//This is useful for producing test data for the unit tests
					//In order to run this from site, one must alter the classpath to include xstream libraries
					//SerializeObjectToXmlUtils.serializeAppToXml(a,"D:\\temp\\apprefs");
					switch (ProductType.get(a.getProductRef())) {
					case INTERNATIONAL:
						setResponsePage(new CAWizardPage(a));
						break;
					case FIXED_TERM_DEPOSIT:
						setResponsePage(new FTDWizardPage(a));
						break;
					case REGULAR_SAVER:
						setResponsePage(new RSWizardPage(a));
						break;
					case IASA:
						setResponsePage(new IASAWizardPage(a));
						break;
					case IBSA:
						setResponsePage(new IBSAWizardPage(a));
						break;
					case RASA:
						setResponsePage(new RASAWizardPage(a));
						break;
					default:
						logger.error("Invalid product type : " + a.getProductRef());
						break;
					}
				} else {
					error(new Alert(acquisitionResponse));
				}
			}
		};

		Border border = addResourceLabelAndReturnBorder(txtRefno);
		frm.add(border);

		txtEmailAddress.add(new EmailValidationBehavior());
		border = addResourceLabelAndReturnBorder(txtEmailAddress);
		frm.add(border);

		Button btnCancel = new Button("cancel") {
			@Override
			public void onSubmit() {
				setResponsePage(ApplyForInternationalAccount.class);
			}
		};
		btnCancel.setDefaultFormProcessing(false);
		frm.add(btnCancel);

		Button btnContinue = new Button("continue");
		frm.add(btnContinue);
		frm.setDefaultButton(btnContinue);
		add(frm);
		add( new CompositeErrorsAndInfosFeedbackPanel(EbankWizard.FEEDBACK_ID, frm, this, null));
	}
	
}
