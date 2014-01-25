package com.temenos.ebank.pages.thankYou;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.services.interfaces.clientAquisition.IServiceCrossSell;

public class DocumentListPanel extends Panel {

	private static final long serialVersionUID = 1L;
	String number = null;
	String numberingFormat = null;

	@SpringBean(name = "serviceCrossSell")
	private IServiceCrossSell serviceCrossSell;

	/**
	 * 
	 * @param id
	 *            - wicket:id for the component
	 * @param documentList
	 *            - documents to display
	 * @param mainApplicant
	 *            - main applicants list ? if so , generate the declaration document automatically , download
	 *            declaration and post me my documents
	 * @param documentsVisible
	 *            - is any additional document required ? if so, the post me my documents button needs to be visible
	 * @param noCrossSell
	 *            - true if this is the Thank you page for a "full" application, false if it's the Thank you page after
	 *            a cross sell application
	 */
	public DocumentListPanel(String id, List<String> documentList, boolean mainApplicant, boolean documentsVisible,
			boolean noCrossSell, String appRef) {
		super(id);
		numberingFormat = mainApplicant ? "numberingFormatMain" : "numberingFormatJoint";
		RepeatingView rv = new RepeatingView("documentList");
		add(rv);

		if (mainApplicant) {
			createDeclarationLine(this, documentsVisible, noCrossSell, appRef);
		} else {
			add(new Panel("declarationLine").setVisible(false));
		}

		if ( !(documentList == null)  && !documentList.isEmpty()) {
			char index = mainApplicant ? 'b' : 'a';
			for (Object item : documentList) {
				String itemId = rv.newChildId();
				WebMarkupContainer parent = new WebMarkupContainer(itemId);
				rv.add(parent);
				Label documentNumber = new Label("documentNumber", new StringResourceModel(numberingFormat, this, null,
						new String[] { String.valueOf(index++) }));
				Label documentName = new Label("documentName", item.toString());
				parent.add(documentNumber);
				parent.add(documentName);
			}
		}

	}

	/**
	 * First line of the documents block needs extra handling in adding external links for download/post
	 * 
	 * @param documentsBlock
	 *            - handle for the container in which to add this component
	 * @param documentsVisible
	 *            - is any additional document required ? if so, the post me my documents button needs to be visible
	 * @param noPreviousCrossSell
	 *            -true if this is the Thank you page for a "full" application, false if it's the Thank you page after
	 *            a cross sell application
	 */
	private void createDeclarationLine(Panel documentsBlock, boolean documentsVisible,
			final boolean noPreviousCrossSell, final String appRef) {
		WebMarkupContainer declarationBlock = new WebMarkupContainer("declarationLine");
		documentsBlock.add(declarationBlock);
		IModel declaration = new StringResourceModel(noPreviousCrossSell ? "declarationName" : "jointDeclarationName", this, null);
		Label docNumber = new Label("declarationNumber", new StringResourceModel(numberingFormat, this, null,
				new String[] { "a" }));
		Label docName = new Label("declarationName", declaration);
		declarationBlock.add(docNumber);
		declarationBlock.add(docName);

		ExternalLink downloadDocumentLink = new ExternalLink("downloadDeclaration", new ResourceModel(
				"brochurewareLink"));
		downloadDocumentLink.add(new Label("downloadDeclarationLabel", new StringResourceModel(
				"downloadDeclarationLabel", this, null, new IModel[] { declaration })));
		declarationBlock.add(downloadDocumentLink);

		final WebMarkupContainer post = new WebMarkupContainer("post");
		final WebMarkupContainer sent = new WebMarkupContainer("sent");
		post.add(sent.setVisible(false));
		@SuppressWarnings("rawtypes")
		AjaxFallbackLink postDocumentLink = new AjaxFallbackLink("postDocuments") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				boolean postOk = serviceCrossSell.postMeMyDocuments(appRef, noPreviousCrossSell);
				if (postOk) {
					sent.setVisible(true);
					this.setEnabled(false);
					target.addComponent(post);
				}
			}
		};
		post.add(postDocumentLink);
		declarationBlock.add(post.setVisible(documentsVisible).setOutputMarkupId(true));
	}
}
