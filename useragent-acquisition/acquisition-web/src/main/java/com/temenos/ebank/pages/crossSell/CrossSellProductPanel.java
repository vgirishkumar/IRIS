package com.temenos.ebank.pages.crossSell;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.ResourceModel;

import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.domain.ProductType;

/**
 * Paints details for one cross-sell product.
 *
 * @author vbeuran
 */
public class CrossSellProductPanel extends Panel {

	private static final long serialVersionUID = 1L;
	private RepeatingView productDetails;
	private String productRef;

	@SuppressWarnings("rawtypes")
	public CrossSellProductPanel(String id,final CrossSellProduct product, PopupSettings settings) {
		super(id);

		/* keep the product reference and the product details as fields in order to have them available in onInitialize */
		productRef = product.getProductRef();
		add(new Label("productName", new ResourceModel(productRef + ".title")));
		productDetails = new RepeatingView("productDetails");
		add(productDetails);
		add(new ExternalLink("findOutMore", new ResourceModel(productRef + ".link"), new ResourceModel("findOutMoreLabel")).setPopupSettings(settings));
		add(new  Link("applyButton") {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				setResponsePage(CrossSellPageFactory.buildCrossSellPage(ProductType.get(productRef)));
			}
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		/*
		 * Initialize here the product details view in order to use properly getString for retrieving the localized
		 * information.
		 */
		int maxNoOfDetails = new Integer(getString(productRef + ".detailsMaxNo")).intValue();
		for(int i = 0; i <= maxNoOfDetails; i++) {
			String itemId = productDetails.newChildId();
			productDetails.add(new WebMarkupContainer(itemId).add(new Label("detail", getString(productRef + ".details" + i))));
		}
	}

}
