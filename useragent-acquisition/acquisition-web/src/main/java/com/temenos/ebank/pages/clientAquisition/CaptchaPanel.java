package com.temenos.ebank.pages.clientAquisition;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.util.UUID;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.temenos.ebank.common.wicket.jcaptcha.CaptchaImage;
import com.temenos.ebank.common.wicket.jcaptcha.CaptchaInput;

/**
 * Panel grouping a CAPTCHA challenge, consisting of an image and the response
 * input text-box.
 * 
 * @author acirlomanu
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class CaptchaPanel extends Panel {
	@SuppressWarnings("unused")
	private String challengeResponse;
	// by keeping the reference to the image and border, I am able to refresh the panel's contents without doing a
	// remove/add. How does this work ???
	private CaptchaImage captchaImage;
	private Border captchaInputBorder;

	/**
	 * Constructor.
	 */
	public CaptchaPanel(String id) {
		super(id);
		generateChallenge();
		add(captchaImage, captchaInputBorder, new AjaxFallbackLink("captchaRefresh", new ResourceModel(
				"captcha.refreshLabel")) {

			@Override
			public void onClick(AjaxRequestTarget target) {
				// generate a new challenge
				generateChallenge();

				// refresh the panel with the image and the input
				target.addComponent(CaptchaPanel.this);
			}
		});
		/*
		 * outputMarkupId is required for refreshing this panel. I tried
		 * refreshing the image and input, but failed because they do not have
		 * contents (it seems we need to refresh a wraper)
		 */
		setOutputMarkupId(true);
	}

	private void generateChallenge() {
		// reset the input value
		challengeResponse = "";
		// generate another challenge
		String challengeId = UUID.randomUUID().toString();
		// TODO figure out how are the new objects are linked to this panel instance when refresh is called (there is no
		// "add" call for the new instances)
		captchaImage = new CaptchaImage("captchaImage", challengeId) {
			@Override
			protected ImageCaptchaService getImageCaptchaService() {
				return CaptchaPanel.this.getImageCaptchaService();
			}
		};
		CaptchaInput captchaInput = new CaptchaInput("captchaResponse", new PropertyModel(this, "challengeResponse"),
				challengeId) {
			@Override
			protected ImageCaptchaService getImageCaptchaService() {
				return CaptchaPanel.this.getImageCaptchaService();
			}

			@Override
			protected void onError(AbstractValidator validator, IValidatable validatable) {
				validator.error(validatable, "captcha.validation.failed");
			}
		};
		captchaInputBorder = addResourceLabelAndReturnBorder(captchaInput);
	}

	/**
	 * Gets the {@link ImageCaptchaService} used for generating the CAPTCHA
	 * image for vaidating the response.
	 * 
	 * @return the CaptchaService provided by the concrete implementation of
	 *         this {@link CaptchaPanel}.
	 */
	protected abstract ImageCaptchaService getImageCaptchaService();

}