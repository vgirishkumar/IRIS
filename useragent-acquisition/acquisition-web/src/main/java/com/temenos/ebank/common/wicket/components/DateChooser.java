package com.temenos.ebank.common.wicket.components;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

import com.temenos.ebank.common.wicket.AddLabelAndBorderOptions;
import static com.temenos.ebank.common.wicket.WicketUtils.*;

/**
 * Date Chooser wicket component
 * 
 * @author vionescu
 */
@SuppressWarnings("serial")
public class DateChooser extends FormComponent<Date> implements CompositeFormComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The day ddc. */
	protected EbankDropDownChoice<Integer> dayDDC;

	/** The month ddc. */
	protected EbankDropDownChoice<Integer> monthDDC;

	/** The year ddc. */
	protected EbankDropDownChoice<Integer> yearDDC;

	protected Integer day;
	protected Integer month;
	protected Integer year;

	private Integer startYear;
	/**
	 * Instantiates a new date chooser.
	 * 
	 * @param id
	 *            the id
	 * @param model
	 *            the model
	 */

	public DateChooser(final String id, IModel<Date> model, Integer startYear) {
		super(id, model);
		if (startYear != null && startYear > 0) {
			this.startYear = startYear;	
		} else {
			//provide a default start year
			this.startYear = Calendar.getInstance().get(Calendar.YEAR) - 106;
		}
		monthDDC = new EbankDropDownChoice<Integer>("month", new PropertyModel<Integer>(this, "month"), getMonths(),
				new IChoiceRenderer<Integer>() {
					public Object getDisplayValue(Integer object) {
						Locale locale = getLocale();
						DateFormatSymbols dfs = new DateFormatSymbols(locale);
						return dfs.getShortMonths()[object.intValue()];
					}

					public String getIdValue(Integer integer, int index) {
						return String.valueOf(index);
					}
				});
		monthDDC.setOutputMarkupId(true);
		add(addResourceLabelAndReturnBorder(monthDDC, new AddLabelAndBorderOptions().setIsContainedInComposite(true)));

		yearDDC = new EbankDropDownChoice<Integer>("year", new PropertyModel<Integer>(this, "year"), getYears(),
				new IChoiceRenderer<Integer>() {
					public Object getDisplayValue(Integer object) {
						return String.valueOf(object);
					}

					public String getIdValue(Integer object, int index) {
						return String.valueOf(object);
					}
				});
		yearDDC.setOutputMarkupId(true);
		add(addResourceLabelAndReturnBorder(yearDDC, new AddLabelAndBorderOptions().setIsContainedInComposite(true)));
		dayDDC = new EbankDropDownChoice<Integer>("day", new PropertyModel<Integer>(this, "day"), getDays(),
				new IChoiceRenderer<Integer>() {
					public Object getDisplayValue(Integer object) {
						return String.valueOf(object);
					}

					public String getIdValue(Integer object, int index) {
						return String.valueOf(object);
					}
				});
		dayDDC.setOutputMarkupId(true);
		add(addResourceLabelAndReturnBorder(dayDDC, new AddLabelAndBorderOptions().setIsContainedInComposite(true)));

		this.setOutputMarkupId(true);
		@SuppressWarnings({ "rawtypes" })
		IModel<Map<String, Object>> variablesModel = new AbstractReadOnlyModel<Map<String, Object>>() {
			public Map getObject() {
				Map<String, CharSequence> variables = new HashMap<String, CharSequence>(7);
				variables.put("dateChooser", DateChooser.this.getMarkupId());
				variables.put("day", dayDDC.getMarkupId());
				variables.put("month", monthDDC.getMarkupId());
				variables.put("year", yearDDC.getMarkupId());
				return variables;
			}
		};
		add(JavascriptPackageResource.getHeaderContribution("js/lib/dateUtils.js"));
		add(TextTemplateHeaderContributor.forJavaScript(getClass(), "dateChooser.js", variablesModel));
	}

	/**
	 * Gets the months.
	 * 
	 * @return the months
	 */
	private List<Integer> getMonths() {
		List<Integer> months = new ArrayList<Integer>(12);
		for (int i = 0; i < 12; i++) {
			months.add(i);
		}
		return months;
	}

	/**
	 * Gets the days.
	 * 
	 * @return the days
	 */
	protected final List<Integer> getDays() {
		List<Integer> days = new ArrayList<Integer>(31);
		int totalDays = 31;
		if (yearDDC.getModelObject() != null && monthDDC.getModelObject() != null) {
			Calendar cal = new GregorianCalendar(yearDDC.getModelObject(), monthDDC.getModelObject(), 1);
			totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		for (int i = 1; i <= totalDays; i++) {
			days.add(i);
		}
		return days;
	}

	/**
	 * Gets the years.
	 * 
	 * @return the years
	 */
	protected final List<Integer> getYears() {
		List<Integer> years = new ArrayList<Integer>(10);
		Calendar cal = Calendar.getInstance();
		int endYear = cal.get(Calendar.YEAR);
		for (int i = endYear; i >= startYear; i--) {
			years.add(i);
		}
		return years;
	}

	@Override
	protected void onBeforeRender() {
		Date date = (Date) getModelObject();
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			this.day = cal.get(Calendar.DAY_OF_MONTH);
			this.month = cal.get(Calendar.MONTH);
			this.year = cal.get(Calendar.YEAR);
		}

		super.onBeforeRender();
	}

	@Override
	protected void convertInput() {
		Integer inputYear = 0;
		Integer inputMonth = 0;
		Integer inputDay = 0;
		boolean validDate = true;
		if (isFormProcessingEnabled(this)) {
			inputYear = (Integer) yearDDC.getConvertedInput();
			inputMonth = (Integer) monthDDC.getConvertedInput();
			inputDay = (Integer) dayDDC.getConvertedInput();
		} else {
			try {
				inputYear = Integer.parseInt(yearDDC.getInput());
				inputMonth = Integer.parseInt(monthDDC.getInput());
				inputDay = Integer.parseInt(dayDDC.getInput());
				validDate = validDate && (inputYear >= 0) && (inputMonth >= 0) && (inputDay >= 0);
			} catch (NumberFormatException e) {
				validDate = false;
			}
		}
		validDate = validDate && (inputYear != null) && (inputMonth != null) && (inputDay != null);
		if (validDate) {
			Calendar cal = Calendar.getInstance(getLocale());
			cal.set(inputYear, inputMonth, inputDay, 0, 0, 0);
			Date convertedInput = cal.getTime();
			setConvertedInput(convertedInput);
		} else {
			setConvertedInput(null);
		}
	}

	@Override
	public String getInput() {
		return yearDDC.getInput() + "/" + monthDDC.getInput() + "/" + dayDDC.getInput();
	}

	/**
	 * Checks if the field is empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		String inputVal = getInput();
		return ("//".equals(inputVal) || "-1/-1/-1".equals(inputVal));
	}

	public FormComponent<?> getFirstInput() {
		return dayDDC;
	}

}