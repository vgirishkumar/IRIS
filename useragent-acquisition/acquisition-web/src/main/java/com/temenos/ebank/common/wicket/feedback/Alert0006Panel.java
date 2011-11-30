package com.temenos.ebank.common.wicket.feedback;

import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * Alert panel for alternate currencies
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings("unchecked")
public class Alert0006Panel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Alert0006Panel(String id, Alert alert) {
		super(id);

		final List<String> alernateCurrencies = (List<String>) (alert.getAdditionalInfo());
		ListView<String> listViewAlternateCurrencies = new ListView<String>("listViewAlternateCurrencies",
				alernateCurrencies) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<String> item) {
				boolean isLastItem = item.getIndex() == this.getModelObject().size() - 1;
				String currencyCode = item.getModelObject();
				item.add(new Label("alternateCurrency", getString("CHOICE.accountCurrency."
						+ currencyCode.trim().toUpperCase())));
				item.add(new WebMarkupContainer("or").setVisible(!isLastItem));
			}
		};
		add(listViewAlternateCurrencies);

	}

}
