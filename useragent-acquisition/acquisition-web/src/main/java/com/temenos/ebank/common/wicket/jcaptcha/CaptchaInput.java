/* Original code was from the wicket-in-action book, chapter 09. */
package com.temenos.ebank.common.wicket.jcaptcha;

import org.apache.wicket.Application;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.temenos.ebank.wicket.EbankWicketApplication;

/**
 * Input for the text depicted by a CAPTCHA image. It is normally associated
 * with a {@link CaptchaImage} by the same challenge ID.
 * 
 * @author acirlomanu
 * @see CaptchaImage
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class CaptchaInput extends TextField {

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the input wicket id
	 * @param challengeResponseModel
	 *            the model for this input;
	 * @param challengeId
	 *            the challengeId corresponding to the image related to this
	 *            input
	 * @see TextField#TextField(String, IModel)
	 */
	public CaptchaInput(String id, IModel challengeResponseModel, final String challengeId) {

		super(id, challengeResponseModel);

		/* this input validates itself against the CAPTCHA challenge ID */
		add(new AbstractValidator() {

			@Override
			protected void onValidate(IValidatable validatable) {
				try {
					if (!getImageCaptchaService().validateResponseForID(challengeId, validatable.getValue())) {
						onError(this, validatable);
					}
				} catch (CaptchaServiceException e) {
					// TODO log the exception ? such exception could be raised by a user trying to re-validate a
					// 'consumed' CAPTCHA
					onError(this, validatable);
				}
			}

			@Override
			public boolean validateOnNullValue() {
				// return 'true' in test/production
				return Application.DEPLOYMENT.equals(EbankWicketApplication.get().getConfigurationType());
			}
		});
	}

	@Override
	protected void onComponentTag(final ComponentTag tag) {
		super.onComponentTag(tag);
		tag.put("value", "");
	}

	/**
	 * Gets the {@link ImageCaptchaService} used for matching the value of the
	 * response corresponding to this object's challengeID .
	 * 
	 * @return the CaptchaService provided by the concrete implementation of
	 *         this {@link CaptchaInput}.
	 */
	protected abstract ImageCaptchaService getImageCaptchaService();

	/**
	 * Handler when the challenge response does not match the challenge id.
	 */
	protected abstract void onError(AbstractValidator validator, IValidatable validatable);
}
