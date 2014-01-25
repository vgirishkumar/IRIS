/**
 * 
 */
package com.temenos.ebank.common.wicket.partialSave;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.validation.IFormValidator;

import com.temenos.ebank.common.wicket.IPartiallySavable;
import com.temenos.ebank.common.wicket.components.DateChooser;

/**
 * Utility class for implementing the partial save mechanism.
 * The partial save consists of validating only the required fields for the save process. This is done by
 * disabling default form processing on the save button. Disabling form processing poses some challenges, 
 * because the models do not get updated, so we must take additional care to do this explicitly.
 * @author vionescu
 */
@SuppressWarnings({ "rawtypes", "unchecked"})
public class PartialSaveUtils {
	
	/**
	 * True if all components in a list are valid
	 * 
	 * @param fieldsToValidate
	 * @return
	 */
	private static boolean areComponentsValid(List<FormComponent> fieldsToValidate) {
		boolean valid = true;
		for (FormComponent component : fieldsToValidate) {
			valid = valid && component.isValid();
			if (!valid) {
				break;
			}
		}
		return valid;
	}
	/**
	 * Returns a list of obligatory fields inside a container, by selecting them based on IDs in the supplied list.
	 * If the list is not supplied and the container is an instance of {@link IPartiallySavable}, the list of IDs is
	 * recovered from the
	 * interface's getObligatoryComponentsIds method, otherwise the list of obligatory components is null.
	 * 
	 * @param container
	 *            - container with potentially obligatory components
	 * @param obligatoryComponentsIds
	 *            - list of IDs based on which obligatory components of the container are selected, needs to be supplied
	 *            for non {@link IPartiallySavable} containers
	 * @return
	 */
	public static List<FormComponent> getObligatoryComponents(WebMarkupContainer container,
			List<String> obligatoryComponentsIds, final List<String> excludedComponentsPaths) {
		List<FormComponent> obligatoryComponents = new ArrayList<FormComponent>();
		// test with provided ID list if not empty
		if (CollectionUtils.isNotEmpty(obligatoryComponentsIds)) {
			obligatoryComponents = getObligatoryComponentsWithIds(container, obligatoryComponentsIds,
					excludedComponentsPaths);
		} else {
			// if the ID list is empty, test only possible for IPartiallySavable containers
			// the IPartiallySavable container has a special method which defines the obligatory components IDs
			if (container instanceof IPartiallySavable) {
				obligatoryComponents = getObligatoryComponentsWithIds(container, ((IPartiallySavable) container)
						.getObligatoryComponentsIds(), excludedComponentsPaths);
			}
		}
		return obligatoryComponents;
	}

	/**
	 * Retrieve all obligatory {@link FormComponent} children except those existing in inner {@link IPartiallySavable}
	 * containers
	 * 
	 * @param container
	 * @param obligatoryComponentsIds
	 * @return
	 */
	private static List<FormComponent> getObligatoryComponentsWithIds(WebMarkupContainer container,
			final List<String> obligatoryComponentsIds, final List<String> excludedComponentsPaths) {
		final List<FormComponent> obligatoryComponents = new ArrayList<FormComponent>();

		container.visitChildren(new IVisitor<Component>() {
			public Object component(Component component) {
				if (component instanceof IPartiallySavable) {
					// we don't go deeper into IPartiallySavable, skip its obligatory components
					return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
				} else {
					// for a container which is not a FormComponent, we need to add its components and not go deeper
					if ((component instanceof WebMarkupContainer) && (!(component instanceof FormComponent))) {
						if (!excludedComponentsPaths.contains(component.getPageRelativePath())) {
							obligatoryComponents.addAll(getObligatoryComponentsWithIds((WebMarkupContainer) component,
									obligatoryComponentsIds, excludedComponentsPaths));
						}
						return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
					} else if (component instanceof FormComponent) {
						// if obligatory component, add it to the list and don't go deeper
						// --> no need to get also the components of the component, it can be a composed one
						if (obligatoryComponentsIds.contains(component.getId()) && component.isEnabledInHierarchy()
								&& component.isVisibleInHierarchy()
								&& !excludedComponentsPaths.contains(component.getPageRelativePath())) {
							obligatoryComponents.add((FormComponent) component);
						}
						return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
					}
					return CONTINUE_TRAVERSAL;
				}
			}
		});

		return obligatoryComponents;
	}

	/**
	 * Retrieves the list of obligatory fields in a WebMarkupContainer and his {@link IPartiallySavable} child
	 * containers, if specified.
	 * Obligatory fields in a container are composed of the obligatory fields of each inner IPartiallySavable container
	 * (which defines his own list of obligatory fields) and the container's obligatory fields, whose ids are specified
	 * in the list obligatoryComponentsIds.
	 * 
	 * @param container
	 *            - container on which we determine the list of obligatory fields
	 * @param obligatoryComponentsIds
	 *            - ids of fields / components in the form which are not included in any IPartiallySavable child
	 *            container
	 * @param excludedComponentsPaths
	 *            - page relative paths for the components to be excluded from validation, even if present in the
	 *            obligatoryFieldsIds list
	 * @return
	 */
	private static List<FormComponent> getObligatoryComponentsInContainerAndChildren(WebMarkupContainer container,
			List<String> obligatoryComponentsIds, final List<String> excludedComponentsPaths) {
		final List<FormComponent> obligatoryComponents = new ArrayList<FormComponent>();

		// first add obligatory components existing in the container and not in any IPartiallySavable child containers
		obligatoryComponents
				.addAll(getObligatoryComponents(container, obligatoryComponentsIds, excludedComponentsPaths));

		// retrieve and add obligatory fields of each IPartiallySavable container, these are distinct FormComponent
		// groups
		container.visitChildren(IPartiallySavable.class, new IVisitor() {
			public Object component(Component component) {
				if (component instanceof WebMarkupContainer
						&& !excludedComponentsPaths.contains(component.getPageRelativePath())) {
					obligatoryComponents.addAll(getObligatoryComponents((WebMarkupContainer) component, null,
							excludedComponentsPaths));
				}
				return CONTINUE_TRAVERSAL;
			}
		});

		return obligatoryComponents;
	}

	/**
	 * Retrieve obligatory fields in Form
	 * 
	 * @param frm
	 * @param obligatoryComponentsIdsInForm
	 * @return
	 */
	private static List<FormComponent> getObligatoryComponentsInForm(Form frm,
			final List<String> obligatoryComponentsIdsInForm, List<String> excludedFieldsPaths) {
		return getObligatoryComponentsInContainerAndChildren(frm, obligatoryComponentsIdsInForm, excludedFieldsPaths);
	}

	/**
	 * Partial validation of a form - possible provided the submitting button has DefaultFormProcessing set on false
	 * 
	 * @param frm
	 *            - form to be partially validated
	 * @param obligatoryFields
	 *            - list of FormComponents obligatory for validation
	 * @return
	 * @see https://cwiki.apache.org/WICKET/conditional-validation.html
	 */
	private static boolean validatePartial(Form frm, List<String> obligatoryFieldsIdsInForm,
			List<String> excludedFieldsPaths) {
		boolean valid = true;

		if (excludedFieldsPaths == null) {
			excludedFieldsPaths = new ArrayList<String>();
		}

		List<FormComponent> obligatoryFields = new ArrayList<FormComponent>();
		// if obligatoryFieldsIdsInForm is null, don't want to search for obligatory fields in sub-containers
		// if there are no obligatory fields in the form itself but you want to search them in sub-containers, use an
		// empty obligatoryFieldsIdsInForm
		if (obligatoryFieldsIdsInForm != null) {
			obligatoryFields = (List<FormComponent>) getObligatoryComponentsInForm(frm, obligatoryFieldsIdsInForm,
					excludedFieldsPaths);
		}

		// aside from the obligatory fields on form, we also need to validate the non-empty fields
		List<FormComponent> nonEmptyComponents = getNonEmptyNonExcludedComponents(frm);

		final List<FormComponent> fieldsToValidate = (List<FormComponent>) CollectionUtils.union(obligatoryFields,
				nonEmptyComponents);
		// for (FormComponent comp: fieldsToValidate) {
		// comp.validate();
		// }

		frm.visitChildren(FormComponent.class, new IVisitor() {
			public Object component(Component component) {
				// boolean isCompositeFormComponent = (component instanceof PhoneAndPrefix);
				if (fieldsToValidate.contains((FormComponent) component)) {
					// if (!isCompositeFormComponent) {
					((FormComponent) component).validate();
					// }
				}
				// if (isCompositeFormComponent) {
				// return CONTINUE_TRAVERSAL;
				// }
				// return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
				return CONTINUE_TRAVERSAL;

			}
		});

		// if any of the obligatory or non-empty fields is invalid (validated individually), the form is invalid
		if (!areComponentsValid(fieldsToValidate)) {
			valid = false;
		} else {
			// if the fields are individually valid, form validators containing those fields should be tested
			// form validation / between fields (ex: email must be = confirm email)
			Collection<IFormValidator> formValidators = frm.getFormValidators();
			for (IFormValidator validator : formValidators) {
				// test if the form validator can be applied (all the dependent fields that are required are included in
				// fieldsToValidate)
				List<FormComponent> validatorComponents = new ArrayList<FormComponent>();
				CollectionUtils.addAll(validatorComponents, validator.getDependentFormComponents());
				if (validatorComponents.size() > 0 && fieldsToValidate.containsAll(validatorComponents)) {
					// apply form validator
					validator.validate(frm);
				}
			}
			// if any of the tested form validators fails --> form will present itself with error, so form is invalid
			if (frm.hasError()) {
				valid = false;
			} else {
				// if the applied form validators didn't generate an error, the filled information is valid
				// --> need to push that data into models, since wicket doesn't do that for us when
				// DefaultFormProcessing = false
				//TODO: since form validation occurs before updating the models, make sure that form validators work on save  
				for (FormComponent fc : getAllEnabledFieldsInForm(frm)) {
					fc.updateModel();
				}
			}
		}
		return valid;
	}

	public static boolean validatePartialForm(Form frm, List<String> obligatoryFieldsIdsInForm,
			List<String> excludedFieldsPaths) {
		return validatePartial(frm, obligatoryFieldsIdsInForm, excludedFieldsPaths);
	}

	/**
	 * Retrieve all non-empty and validatable form components in container. Search doesn't go deeper, if a composite
	 * FormComponent is found.
	 * 
	 * @param container
	 * @return
	 */
	private static List<FormComponent> getNonEmptyNonExcludedComponents(WebMarkupContainer container) {
		final List<FormComponent> nonEmptyFields = new ArrayList<FormComponent>();

		// retrieve the FormComponents existing in container
		container.visitChildren(FormComponent.class, new IVisitor<FormComponent>() {
			public Object component(FormComponent component) {
				// TODO: more conditions for a component to be validatable && non-empty??
				if (component.isEnabledInHierarchy() && component.isVisibleInHierarchy()
						&& !isEmptyComponent(component)
				/* && !excludedComponentsPaths.contains(component.getPageRelativePath()) */) {
					nonEmptyFields.add(component);
				}
				// if (component instanceof PhoneAndPrefix) {
				// return CONTINUE_TRAVERSAL;
				// }
				// return CONTINUE_TRAVERSAL_BUT_DONT_GO_DEEPER;
				return CONTINUE_TRAVERSAL;
			}
		});

		return nonEmptyFields;
	}

	/**
	 * Retrieve all visible and enabled form components in container. Search doesn't go deeper, if a composite
	 * FormComponent is found.
	 * @param container
	 * @return
	 */
	private static List<FormComponent> getAllEnabledFieldsInForm(WebMarkupContainer container) {
		final List<FormComponent> enabledFields = new ArrayList<FormComponent>();
		// retrieve the FormComponents existing in container
		container.visitChildren(FormComponent.class, new IVisitor<FormComponent>() {
			public Object component(FormComponent component) {
				if (component.isEnabledInHierarchy() && component.isVisibleInHierarchy()) {
					enabledFields.add(component);
				}
				return CONTINUE_TRAVERSAL;
			}
		});
		return enabledFields;
	}
	
	/**
	 * True if the FormComponent is empty, evaluation according to the components instance type
	 * @param component
	 * @return
	 */
	private static boolean isEmptyComponent(FormComponent component) {
		boolean isEmpty = StringUtils.isBlank(component.getInput());
		if (isEmpty) {
			return true;
		}
		if (component instanceof RadioChoice<?>) {
			String input  = ((RadioChoice<?>)component).getInput();
			return "-1".equals(input);
		} 
		if (component instanceof DropDownChoice) {
			String input  = ((DropDownChoice) component).getInput();
			return "-1".equals(input);
		} 
		if (component instanceof CheckBox) {
			return "false".equals(component.getInput()) || "off".equals(component.getInput());
		}
		if (component instanceof DateChooser) {
			DateChooser dc = (DateChooser) component;
			return dc.isEmpty();
		}
		return false;
	}
}