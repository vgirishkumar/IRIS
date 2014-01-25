package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.components.CompositeFormComponent;
import com.temenos.ebank.common.wicket.components.EbankTextField;

/**
 * Sort code wicket component
 * 
 * @author vionescu
 * 
 */
public class SortCodePanel extends FormComponentPanel<String> implements CompositeFormComponent{

	private static final long serialVersionUID = 1L;

	private String sortCode1;
	private String sortCode2;
	private String sortCode3;

	private EbankTextField sortCode1Field;
	private EbankTextField sortCode2Field;
	private EbankTextField sortCode3Field;

	public SortCodePanel(String id) {
		// TODO
		super(id);
		addFields();
	}

	public SortCodePanel(String id, IModel<String> model) {
		super(id, model);
		addFields();
	}

	/**
	 * Adds the three input texts for forming the sort code
	 */
	private void addFields() {
		setType(String.class);
		sortCode1Field = new EbankTextField("sortCode1", new PropertyModel<String>(this, "sortCode1"), true);
		add(sortCode1Field);

		sortCode2Field = new EbankTextField("sortCode2", new PropertyModel<String>(this, "sortCode2"), true);
		add(sortCode2Field);
		sortCode3Field = new EbankTextField("sortCode3", new PropertyModel<String>(this, "sortCode3"), true);
		add(sortCode3Field);
	}

	@Override
	protected void onBeforeRender() {
		String sortCode = (String) getModelObject();
		if (sortCode != null && sortCode.length() == 6) {
			setSortCode1(sortCode.substring(0, 2));
			setSortCode2(sortCode.substring(2, 4));
			setSortCode3(sortCode.substring(4, 6));
		}

		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		String[] convertedInputs = new String[3];
		if (WicketUtils.isFormProcessingEnabled(this)) {
			convertedInputs[0] = (String) sortCode1Field.getConvertedInput();
			convertedInputs[1] = (String) sortCode2Field.getConvertedInput();
			convertedInputs[2] = (String) sortCode3Field.getConvertedInput();
		} else {
			convertedInputs[0] = (String) sortCode1Field.getInput();
			convertedInputs[1] = (String) sortCode2Field.getInput();
			convertedInputs[2] = (String) sortCode3Field.getInput();
		}
		String convertedInput = StringUtils.defaultIfEmpty(StringUtils.join(convertedInputs), null);
		setConvertedInput(convertedInput);
	}

	@Override
	public String getInput() {
		return sortCode1Field.getInput() + sortCode2Field.getInput() + sortCode3Field.getInput();
	}

	public void setSortCode1(String sortCode1) {
		this.sortCode1 = sortCode1;
	}

	public String getSortCode1() {
		return sortCode1;
	}

	public void setSortCode2(String sortCode2) {
		this.sortCode2 = sortCode2;
	}

	public String getSortCode2() {
		return sortCode2;
	}

	public void setSortCode3(String sortCode3) {
		this.sortCode3 = sortCode3;
	}

	public String getSortCode3() {
		return sortCode3;
	}

	public FormComponent<?> getFirstInput() {
		return sortCode1Field;
	}

}
