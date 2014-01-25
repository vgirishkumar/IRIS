package com.temenos.ebank.pages.thankYou;

import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getAlertPanel;
import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getGenericFormValidationFeedbackPanel;
import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getInfoPanel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.resource.ContextRelativeResource;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AccountDetails;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.clientAquisition.wizard.SupportSnippet;
import com.temenos.ebank.pages.crossSell.CrossSellPanel;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceCrossSell;

/**
 * @author vbeuran
 * 
 */
@SuppressWarnings("rawtypes")
public class ThankYouPage extends BasePage {

	@SpringBean(name = "serviceCrossSell")
	private IServiceCrossSell serviceCrossSell;

	private boolean noPreviousCrossSell;
	private Application lastCompletedApp;
	
	/**
	 * @param response
	 * @param noPreviousCrossSell
	 *            - when showing thank you after a cross sell, this param is set to false
	 * @param appRef
	 *            - This argument is necessary only after the cross sell application (the cross sell Application object
	 *            isn't stored on the session).
	 */
	@SuppressWarnings({"unchecked" })
	public ThankYouPage(final AccountCreationResponse response, final boolean noPreviousCrossSell, Application lastCompletedApp) {
		final Application a = getClientAquisitionApplication();
		this.lastCompletedApp = lastCompletedApp;
		this.noPreviousCrossSell = noPreviousCrossSell;
		final List<CrossSellProduct> crossSellProducts = noPreviousCrossSell ? getCrossSellList(a) : new ArrayList();
			
		Form form = new Form("form");
		add(form);
		add(getAlertPanel(this));
		add(getGenericFormValidationFeedbackPanel(form, null));
		add(getInfoPanel(this));
		RepeatingView productList = new RepeatingView("productList");
		for (AccountDetails item : response.getAccountList()) {
			String itemId = productList.newChildId();
			WebMarkupContainer parent = new WebMarkupContainer(itemId);
			parent.add(new Label("productCurrency", item.getAccountCurrency()));
			parent.add(new Label("productName", new ResourceModel(String.format("wizard.%s.title", lastCompletedApp.getProductRef()) ) ) );
			productList.add(parent);
		}
		form.add(productList);
			
		form.add(new SupportSnippet("supportSnippet"));
		// newly created accounts details

		form.add(new ExternalLink("downloadDeclaration", new ResourceModel("brochurewareLink")));
		final WebMarkupContainer post = new WebMarkupContainer("post");
		final WebMarkupContainer sent = new WebMarkupContainer("sent");
		post.add(sent.setVisible(false));
		AjaxFallbackLink postDocumentLink = new AjaxFallbackLink("postDocuments") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				boolean postOk = serviceCrossSell.postMeMyDocuments(a.getAppRef(), noPreviousCrossSell);
				Image statusImage = new Image("status", new ContextRelativeResource(postOk ? "img/bullet-feature.gif" : "img/icon-information.gif"));
				this.add(new SimpleAttributeModifier("title", getString(postOk ? "disabledButton" : "processed") ));
				statusImage.add(new SimpleAttributeModifier("title", getString(postOk ? "disabledButton" : "processed")));
				sent.add(statusImage).setVisible(true);
				this.setEnabled(false);
				target.addComponent(post);
			}
		};
		post.add(postDocumentLink);
		form.add(post.setOutputMarkupId(true));
		
		boolean accountsCreated = response.getAccountList() != null && !response.getAccountList().isEmpty();

		// treat the empty account list with displaying an alert panel saying "Your data has been processed...." 
		WebMarkupContainer detailPanels = new WebMarkupContainer("detailPanelsBlock");
		RepeatingView panelList = new RepeatingView("panelList");
		detailPanels.add(panelList);
		if (accountsCreated) {
			for (AccountDetails item : response.getAccountList()) {
				String itemId = panelList.newChildId();
				WebMarkupContainer parent = new WebMarkupContainer(itemId);
				panelList.add(parent);
				parent.add(new AccountDetailsPanel("accountDetails", new CompoundPropertyModel<AccountDetails>(item),
						response.getAccountList().indexOf(item)));

			}
		} else {
			Session.get().getFeedbackMessages().warn(this, getString("processed"));
			Session.get().dirty();
		}
		if (response.getUnderlyingAccount() != null) {
			String itemId = panelList.newChildId();
			WebMarkupContainer parent = new WebMarkupContainer(itemId);
			panelList.add(parent);
			parent.add(new AccountDetailsPanelUnderlying("accountDetails", new CompoundPropertyModel<AccountDetails>(
					response.getUnderlyingAccount()), 0));
		}
		form.add(detailPanels.setVisible(accountsCreated));

		// newly created user Ids
		boolean newUser = response.getUserId() != null;
		boolean jointAppNewUser = response.getSecondUserId() != null && !a.getIsSole();
		WebMarkupContainer userIds = new WebMarkupContainer("userIdBlock");
		form.add(userIds);

		// note that we use the "Session.get().getClientAcquisitionApplication.getIsSole()" for jointAppNewUser and that
		// might be inaccurate for the page after CS; for this instance of the page noPreviousCrossSell short-circuits
		// the "&&" condition;
		userIds.setVisible(noPreviousCrossSell && (newUser || jointAppNewUser));
		WebMarkupContainer row1 = new WebMarkupContainer("userIdRow");
		row1.add(new Label("userId", response.getUserId()));
		userIds.add(row1.setVisible(newUser));

		WebMarkupContainer row2 = new WebMarkupContainer("secondUserIdRow");
		row2.add(new Label("secondUserId", response.getSecondUserId()));
		userIds.add(row2.setVisible(jointAppNewUser));

		//this is complicated: 
		//1. declaration label and link is always shown but can be named Declaration or Joint Declaration
		//2. we have document list and joint applicants document list, sharing one post me my documents button. 
		//For more details, look inside DocumentListPanel
		boolean documentsVisible = !CollectionUtils.isEmpty(response.getDocumentList())
				|| !CollectionUtils.isEmpty(response.getSecondDocumentList());
		WebMarkupContainer warningDocumentsBlock = new WebMarkupContainer("warning");
		form.add(warningDocumentsBlock.setVisible(documentsVisible));
		warningDocumentsBlock.add(new DocumentListPanel("documentListMain", response.getDocumentList(), true, documentsVisible,
				noPreviousCrossSell, lastCompletedApp.getAppRef()));
		warningDocumentsBlock.add(new DocumentListPanel("documentListJoint", response.getSecondDocumentList(), false, documentsVisible,
				noPreviousCrossSell, lastCompletedApp.getAppRef()));

		boolean crossSellVisible = (crossSellProducts != null) && !crossSellProducts.isEmpty();
		form.add(crossSellVisible ? new CrossSellPanel("crossList",crossSellProducts) : new EmptyPanel("crossList"));
	}

	protected Application getClientAquisitionApplication() {
		return getEbankSession().getClientAquisitionApplication();
	}
	
	@Override
	protected
	boolean supportsModalSessionScript() {
		return true;
	}
	
	/**
	 * calls T24 for the list of Cross sell products
	 * 
	 * @param app
	 *            - the application submitted in the previous screen
	 * @return a list of CrossSellProduct"s"
	 */
	private List<CrossSellProduct> getCrossSellList(Application app) {
		AcquisitionRequest csRequest = new AcquisitionRequest(app, "CS");
		return ((AcquisitionResponse) serviceCrossSell.getCrossSellProducts(csRequest)).getCrossSellProducts();
	}
	
	@Override
	protected void onBeforeRender() {
		// add support for web analyitcs
		addAnalytics();
		super.onBeforeRender();
	}
	/**
	 * Register analytics behaviour and parameters on base page.
	 */
	@SuppressWarnings({ "unchecked" })
	private void addAnalytics() {
		Map parameters = new HashMap();

		Application app = getClientAquisitionApplication();
		parameters.put("appid", app.getAppRef());
		parameters.put("existingCustomer", app.getCustomer().getIsExistingCustomer() ? "Yes" : "No");
		parameters.put("isJoint", app.getIsSole() ? "Single" : "Joint");
		parameters.put("currency", app.getAccountCurrency());
		parameters.put("product", app.getProductRef());
		if (!noPreviousCrossSell) {
			app = lastCompletedApp;
			parameters.put("appidCS", app.getAppRef());
			parameters.put("isJointCS", app.getIsSole() ? "Single" : "Joint");
			parameters.put("currencyCS", app.getAccountCurrency());
			parameters.put("productCS", app.getProductRef());
		}

		// TODO: for the moment, we use only "StepX" for the name of the step
		parameters.put("step", noPreviousCrossSell ? "Step7" : "StepX3");
		Date currentDate = new Date();
		parameters.put("date", new SimpleDateFormat("MM/dd/yy").format(currentDate));
		parameters.put("time", new SimpleDateFormat("hh:mm:ss").format(currentDate));

		((BasePage) getPage()).addAnalyticsParameters(parameters);
	}
}
