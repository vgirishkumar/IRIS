package com.temenos.ebank.common.wicket.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.IClusterable;
import org.apache.wicket.extensions.wizard.AbstractWizardModel;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardStep;
import org.apache.wicket.util.collections.ArrayListStack;

import com.temenos.ebank.domain.ProductType;

/**
 * Default implementation of {@link IWizardModel}, which models a semi-static wizard. This means
 * that all steps should be known upfront, and added to the model on construction. Steps can be
 * optional by using {@link ICondition}. The wizard is initialized with a wizard model through
 * calling method {@link Wizard#init(IWizardModel)}.
 * <p>
 * Steps can be added to this model directly using either the {@link #add(IWizardStep) normal add
 * method} or {@link #add(IWizardStep, ICondition) the conditional add method}.
 * </p>
 * 
 * <p>
 * <a href="https://wizard-framework.dev.java.net/">Swing Wizard Framework</a> served as a valuable source of
 * inspiration.
 * </p>
 * 
 * @author Eelco Hillenius
 */
public class EbankWizardModel extends AbstractWizardModel {
	protected Log logger = LogFactory.getLog(getClass());

	/**
	 * Interface for conditional displaying of wizard steps.
	 */
	public interface ICondition extends IClusterable {
		/**
		 * Evaluates the current state and returns whether the step that is coupled to this
		 * condition is available.
		 * 
		 * @return True if the step this condition is coupled to is available, false otherwise
		 */
		public boolean evaluate();
	}

	/**
	 * Condition that always evaluates true.
	 */
	public static final ICondition TRUE = new ICondition() {
		private static final long serialVersionUID = 1L;

		/**
		 * Always returns true.
		 * 
		 * @return True
		 */
		public boolean evaluate() {
			return true;
		}
	};

	private static final long serialVersionUID = 1L;

	/** The currently active step. */
	private IWizardStep activeStep;

	/** Conditions with steps. */
	private final List<ICondition> conditions = new ArrayList<ICondition>();

	/** State history. */
	private final ArrayListStack<IWizardStep> history = new ArrayListStack<IWizardStep>();

	/** The wizard steps. */
	private final List<IWizardStep> steps = new ArrayList<IWizardStep>();

	private ProductType productType;
	
	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	/**
	 * Construct.
	 */
	public EbankWizardModel() {
	}

	/**
	 * Adds the next step to the wizard. If the {@link WizardStep} implements {@link ICondition},
	 * then this method is equivalent to calling {@link #add(IWizardStep, ICondition) add(step,
	 * (ICondition)step)}.
	 * 
	 * @param step
	 *            the step to added.
	 */
	public void add(IWizardStep step) {
		if (step instanceof ICondition)
			add(step, (ICondition) step);
		else
			add(step, TRUE);
	}

	/**
	 * Adds an optional step to the model. The step will only be displayed if the specified
	 * condition is met.
	 * 
	 * @param step
	 *            The step to add
	 * @param condition
	 *            the {@link ICondition} under which it should be included in the wizard.
	 */
	public void add(IWizardStep step, ICondition condition) {
		steps.add(step);
		conditions.add(condition);
	}

	/**
	 * Gets the current active step the wizard should display.
	 * 
	 * @return the active step.
	 */
	public final IWizardStep getActiveStep() {
		return activeStep;
	}

	/**
	 * Checks if the last button should be enabled.
	 * 
	 * @return <tt>true</tt> if the last button should be enabled, <tt>false</tt> otherwise.
	 * @see IWizardModel#isLastVisible
	 */
	public boolean isLastAvailable() {
		return allStepsComplete() && !isLastStep(activeStep);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#isLastStep(org.apache.wicket.extensions.wizard.IWizardStep)
	 */
	public boolean isLastStep(IWizardStep step) {
		return findLastStep().equals(step);
	}

	/**
	 * Checks if the next button should be enabled.
	 * 
	 * @return <tt>true</tt> if the next button should be enabled, <tt>false</tt> otherwise.
	 */
	public boolean isNextAvailable() {
		return activeStep.isComplete() && !isLastStep(activeStep);
	}

	/**
	 * Checks if the previous button should be enabled.
	 * 
	 * @return <tt>true</tt> if the previous button should be enabled, <tt>false</tt> otherwise.
	 */
	public boolean isPreviousAvailable() {
		return !history.isEmpty();
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#last()
	 */
	public void last() {
		history.push(getActiveStep());
		IWizardStep lastStep = findLastStep();
		setActiveStep(lastStep);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#next()
	 */
	public void next() {
		history.push(getActiveStep());
		IWizardStep step = findNextVisibleStep();
		setActiveStep(step);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#previous()
	 */
	public void previous() {
		IWizardStep step = history.pop();
		setActiveStep(step);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardModel#reset()
	 */
	public void reset() {
		history.clear();
		activeStep = null;
		setActiveStep(findNextVisibleStep());
	}

	/**
	 * Sets the active step.
	 * 
	 * @param step
	 *            the new active step step.
	 */
	public void setActiveStep(IWizardStep step) {
		if (activeStep != null && step != null && activeStep.equals(step)) {
			return;
		}

		activeStep = step;

		fireActiveStepChanged(step);
	}

	/**
	 * Sets the active step.
	 * 
	 * @param stepIndex
	 *            the new active step index.
	 */
	public void setActiveStep(int stepIndex) {

		int noOfSteps = steps.size();
		if (stepIndex >= noOfSteps || stepIndex < 0) {
			logger.warn("Step index out of bounds: " + stepIndex);
			setActiveStep(steps.get(0));
		}

		setActiveStep(steps.get(stepIndex));
	}

	/**
	 * Resumes the wizard from a step index.
	 * 
	 * @param stepIndex
	 *            the index of the step to resume
	 */
	public void resumeToStep(int stepIndex) {
		int noOfSteps = steps.size();
		if (stepIndex >= noOfSteps || stepIndex < 0) {
			logger.warn("Step index out of bounds: " + stepIndex);
			resumeToStep(steps.get(0));
		}
		IWizardStep stepToResume = steps.get(stepIndex);
		resumeToStep(stepToResume);
	}

	/**
	 * Resumes the wizard from a step.
	 * 
	 * @param stepToResume
	 *            The step to resume
	 */
	public void resumeToStep(IWizardStep stepToResume) {
		reset();
		if (this.activeStep == stepToResume) {
			return;
		}
		IWizardStep currStep = null;
		// I use for for paranoiac reasons, in order to prevent an endless loop
		// TODO check this, then replace paranoid "for" with something more decent
		for (int i = 0; i < 100; i++) {
			try {
				currStep = findNextVisibleStep();
				history.push(this.activeStep);
			} catch (RuntimeException e) {
				// no more steps
				setActiveStep(this.activeStep);
				break;
			}
			if (currStep == stepToResume) {
				setActiveStep(stepToResume);
				break;
			} else {
				this.activeStep = currStep;
			}

		}

	}

	/**
	 * Returns the steps of the wizard
	 * 
	 * @return
	 */
	public final List<IWizardStep> getSteps() {
		return Collections.unmodifiableList(steps);
	}

	/**
	 * @see IWizardModel#stepIterator()
	 */
	public final Iterator<IWizardStep> stepIterator() {
		return steps.iterator();
	}

	/**
	 * Returns true if all the steps in the wizard return <tt>true</tt> from {@link IWizardStep#isComplete}. This is
	 * primarily used to determine if the last button can be
	 * enabled.
	 * 
	 * @return <tt>true</tt> if all the steps in the wizard are complete, <tt>false</tt> otherwise.
	 */
	protected final boolean allStepsComplete() {
		for (IWizardStep step : steps) {
			if (!step.isComplete()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Finds the last step in this model.
	 * 
	 * @return The last step
	 */
	protected final IWizardStep findLastStep() {
		for (int i = conditions.size() - 1; i >= 0; i--) {
			ICondition condition = conditions.get(i);
			if (condition.evaluate()) {
				return steps.get(i);
			}
		}

		throw new IllegalStateException("Wizard contains no visible steps");
	}

	/**
	 * Finds the next visible step based on the active step.
	 * 
	 * @return The next visible step based on the active step
	 */
	protected final IWizardStep findNextVisibleStep() {
		int startIndex = (activeStep == null) ? 0 : steps.indexOf(activeStep) + 1;

		for (int i = startIndex; i < conditions.size(); i++) {
			ICondition condition = conditions.get(i);
			if (condition.evaluate()) {
				return steps.get(i);
			}
		}

		throw new IllegalStateException("Wizard contains no more visible steps");
	}

	/**
	 * Gets conditions.
	 * 
	 * @return unmodifiable list of conditions
	 */
	public List<ICondition> getConditions() {
		return Collections.unmodifiableList(conditions);
	}

}