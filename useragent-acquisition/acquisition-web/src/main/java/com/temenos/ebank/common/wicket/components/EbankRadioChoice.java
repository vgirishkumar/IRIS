package com.temenos.ebank.common.wicket.components;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;

public class EbankRadioChoice extends RadioChoice<String>  implements ClientValidatedCheckable{
	private static final long serialVersionUID = 1L;

	public EbankRadioChoice(String id, List<String> choices, IChoiceRenderer<String> renderer) {
		super(id, choices, renderer);
	}

	public String getFirstCheckableId() {
		if (CollectionUtils.isEmpty(getChoices())) {
			return StringUtils.EMPTY;
		}
		return getMarkupId() + "-" + getChoiceRenderer().getIdValue(getChoices().get(0), 0);
	}
}
