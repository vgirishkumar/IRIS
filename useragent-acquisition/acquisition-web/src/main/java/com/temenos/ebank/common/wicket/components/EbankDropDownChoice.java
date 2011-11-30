package com.temenos.ebank.common.wicket.components;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * A dropdown choice component that always shows, by default, a null option.
 * 
 * @author gcristescu
 */
public class EbankDropDownChoice<T> extends DropDownChoice<T>{
	private static final long serialVersionUID = 1L;

	public EbankDropDownChoice(String id, IModel<T> model, List<? extends T> data, IChoiceRenderer<? super T> renderer) {
		super(id, model, data, renderer);
	}

	public EbankDropDownChoice(String id, IModel<T> model, List<? extends T> choices) {
		super(id, model, choices);
	}

	public EbankDropDownChoice(String id, List<? extends T> data, IChoiceRenderer<? super T> renderer) {
		super(id, data, renderer);
	}

	public EbankDropDownChoice(String id, List<? extends T> choices) {
		super(id, choices);
	}

	public EbankDropDownChoice(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		setNullValid(true);
	}
}
