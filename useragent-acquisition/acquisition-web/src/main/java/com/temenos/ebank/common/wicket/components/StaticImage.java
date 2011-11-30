package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * External image component. The HTML counterpart *must* be a &lt;img&gt;. This component just puts the <code>src</code>
 * attribute on the <code>img</code>. As opposed to the {@link Image} component, this one does not load the image as a
 * context resource, but lets the
 * client browser load the image directly.
 * 
 * 
 * @author acirlomanu
 * @see http://apache-wicket.1842946.n4.nabble.com/Plain-IMG-src-urls-td1877368.html
 * 
 */
public class StaticImage extends WebComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that takes the component id and the URL as a model.
	 * 
	 * @param id
	 * @param model
	 */
	public StaticImage(String id, IModel model) {
		super(id, model);
	}

	/**
	 * Constructor that takes the component id and the URL as a String.
	 * 
	 * @param id
	 * @param url
	 */
	public StaticImage(String id, String url) {
		this(id, new Model(url));
	}

	/**
	 * Puts the <code>src</code> attribute on the <code>img</code> tag.
	 * Non-absolute URLs are computed relatively to the Wicket handler path.
	 *  
	 * @see org.apache.wicket.Component#onComponentTag(org.apache.wicket.markup.ComponentTag)
	 */
	protected void onComponentTag(ComponentTag tag) {
		checkComponentTag(tag, "img");
		super.onComponentTag(tag);
		String url = getDefaultModelObjectAsString();
		if (url == null) {
			// should we warn or something? rather not, maybe someone has changed their mind and are providing the src in the HTML template.
			return;
		}
		// if the URL is not absolute, then compute it relatively to the Wicket handler path
		if (!url.startsWith("http")) {
			url = RequestCycle.get().getRequest().getRelativePathPrefixToWicketHandler() + (url.startsWith("/") ? url.substring(1) : url);  
		}
		tag.put("src", url);
	}
}
