package com.temenos.ebank.common.wicket.components;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

/**
 * @author raduf
 * Class created by copying code from CheckBoxMultipleChoice.
 * The class needs to insert a class html attribute into the check boxes rendered by this component. 
 * The class attribute will be interpreted by the client side jquery validation plug-in.  
 * The only way to modify the class html attribute was to copy and modify onComponentTagBody which is final in CheckBoxMultipleChoice 
 * @param <T>
 */
public class EbankCheckBoxMultipleChoice extends CheckBoxMultipleChoice implements ClientValidatedCheckable{
	private static final long serialVersionUID = 1L;

	public EbankCheckBoxMultipleChoice(String id, IModel model, List choices, IChoiceRenderer renderer) {
		super(id, model, choices, renderer);
	}
	
	@Override
	protected String getCheckBoxMarkupId(String id)
	{
		return getMarkupId() + "_" + id;
	}

	public String getFirstCheckableId(){
		if (CollectionUtils.isEmpty(getChoices())) {
			return StringUtils.EMPTY;
		}
		String id = getChoiceRenderer().getIdValue(getChoices().get(0), 0);
		return getCheckBoxMarkupId(id);
	}
}
