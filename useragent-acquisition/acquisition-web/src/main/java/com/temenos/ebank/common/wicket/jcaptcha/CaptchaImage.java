/* Original code was from the wicket-in-action book, chapter 09. */
package com.temenos.ebank.common.wicket.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * Dynamic, non-cacheable image representing a CAPTCHA. It is normally associated with a text input by the same
 * challenge ID.
 * 
 * @author acirlomanu
 * @see CaptchaInput
 */
@SuppressWarnings("serial")
public abstract class CaptchaImage extends NonCachingImage {

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the image wicket id
	 * @param challengeId
	 *            the challengeId corresponding to the text related to this
	 *            image
	 * @see NonCachingImage#NonCachingImage(String)
	 */
	public CaptchaImage(String id, final String challengeId) {

		super(id);
		setImageResource(new DynamicImageResource() {
			@Override
			protected byte[] getImageData() {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				/* the image's content are retrieved from the captcha service */
				BufferedImage challenge = getImageCaptchaService().getImageChallengeForID(challengeId,
						Session.get().getLocale());
				try {
					ImageIO.write(challenge, "jpg", os);
					return os.toByteArray();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	/**
	 * Gets the {@link ImageCaptchaService} used for generating the CAPTCHA
	 * image corresponding to this object's challengeID.
	 * 
	 * @return the CaptchaService provided by the concrete implementation of
	 *         this {@link CaptchaImage}.
	 */
	protected abstract ImageCaptchaService getImageCaptchaService();
}
