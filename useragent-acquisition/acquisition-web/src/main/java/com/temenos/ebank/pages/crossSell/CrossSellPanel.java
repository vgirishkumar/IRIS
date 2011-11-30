/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.ResourceModel;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.wicket.EbankSession;

/**
 * Paints several cross-sell products.
 * 
 * @author vbeuran
 * 
 */
@SuppressWarnings("rawtypes")
public class CrossSellPanel extends Panel {
	
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("serial")
	public CrossSellPanel(String id, final List<CrossSellProduct> crossSellProducts) {
		super(id);
		
		RepeatingView productList = new RepeatingView("productList");
		PopupSettings settings = getPopupSettings();
		add(productList);
		for (CrossSellProduct item : crossSellProducts ) {	
			String itemId = productList.newChildId();
			WebMarkupContainer parent = new WebMarkupContainer(itemId);
			productList.add(parent);
			parent.add(new CrossSellProductPanel("productDetails", item, settings));			
		}
		
		add(new ExternalLink("compareAccounts", new ResourceModel("brochurewareCompareLink") ).setPopupSettings(settings));
		add(new Link("noThanks") {
			@Override
			public void onClick() {
				setResponsePage(getApplication().getHomePage());
			}
		});
	}
	
	/**
	 * Creates a "settings object" for the popup pages to be opened by this page 
	 * @return
	 */
	private PopupSettings getPopupSettings() {
		int popupHeight = 600;
		int popupWidth = 800;
		PopupSettings settings = new PopupSettings(PopupSettings.MENU_BAR |
												   	PopupSettings.RESIZABLE | 
												   	PopupSettings.SCROLLBARS | 
												   	PopupSettings.STATUS_BAR )
													.setHeight(popupHeight)
													.setWidth(popupWidth);
		settings.setTop(200);
		settings.setLeft(300);
		return settings;
	}
	
	protected Application getClientAquisitionApplication() {
		return ((EbankSession)this.getSession()).getClientAquisitionApplication();
	}
	
}
