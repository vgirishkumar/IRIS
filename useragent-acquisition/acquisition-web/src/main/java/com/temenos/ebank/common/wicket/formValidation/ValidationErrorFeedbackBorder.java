package com.temenos.ebank.common.wicket.formValidation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A border that can be placed around a form component to indicate when the
 * bordered child/children has a validation error. A child of the border named
 * "errorIndicator" will be shown and hidden depending on whether the child has
 * an error. <strong>Note: </strong> Since this border checks its children do
 * not use setTransparentResolver(true) and add the children directly into the
 * border
 * 
 * @author Victor Ionescu
 */
public class ValidationErrorFeedbackBorder extends Border implements IFeedback {
	private static final long serialVersionUID = 1L;

	/**
	 * List for messages.
	 */
	private final class MessageListView extends ListView<FeedbackMessage> {
		private static final long serialVersionUID = 1L;

		/**
		 * @see org.apache.wicket.Component#Component(String)
		 */
		public MessageListView(final String id) {
			super(id);
			setDefaultModel(newFeedbackMessagesModel());
		}

		/**
		 * @see org.apache.wicket.markup.html.list.ListView#populateItem(org.apache.wicket.markup.html .list.ListItem)
		 */
		@Override
		protected void populateItem(final ListItem<FeedbackMessage> listItem) {
			final FeedbackMessage message = listItem.getModelObject();
			message.markRendered();
			final IModel<String> replacementModel = new Model<String>() {
				private static final long serialVersionUID = 1L;

				/**
				 * Returns feedbackPanel + the message level, eg
				 * 'feedbackPanelERROR'. This is used as the class of the li /
				 * span elements.
				 * 
				 * @see org.apache.wicket.model.IModel#getObject()
				 */
				@Override
				public String getObject() {
					return getCSSClass(message);
				}
			};

			final Component label = newMessageDisplayComponent("message", message);
			final AttributeModifier levelModifier = new AttributeModifier("class", replacementModel);
			label.add(levelModifier);
			listItem.add(levelModifier);
			listItem.add(label);
		}
	}

//	private final class FieldLabelVisitor implements IVisitor<FormComponentLabel>, Serializable {
//		/**
//		 * 
//		 */
//		private static final long serialVersionUID = 1L;
//
//		public Object component(FormComponentLabel fieldLabel) {
//
//			final IModel<String> labelErrorClassModel = new Model<String>() {
//				private static final long serialVersionUID = 1L;
//
//				/**
//				 * Returns the css class for the field label
//				 * 
//				 * @see org.apache.wicket.model.IModel#getObject()
//				 */
//				@Override
//				public String getObject() {
//					if (anyErrorMessage()) {
//						return "error";
//					} else {
//						return "";
//					}
//				}
//			};
//
//			fieldLabel.add(new AttributeModifier("class", true, labelErrorClassModel));
//
//			return IVisitor.STOP_TRAVERSAL;
//		}
//	}

	/** Message view */
	private final MessageListView messageListView;

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 * 
	 * @param id
	 * @param filter
	 */
	public ValidationErrorFeedbackBorder(final String id) {
		super(id);
		WebMarkupContainer messagesContainer = new WebMarkupContainer("feedbackContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return anyMessage();
			}
		};
		add(messagesContainer);
		messageListView = new MessageListView("messages");
		messageListView.setVersioned(false);
		messagesContainer.add(messageListView);
		setFilter(new ContainerFeedbackMessageFilter(this));

	}

/*	PMD cries for useless overriding method after commenting visitChildren call
 * 	@Override
	protected void onBeforeRender() {
		//TODO: aparent Realise nu inroseste labelul
		//visitChildren(FormComponentLabel.class, new FieldLabelVisitor());
		super.onBeforeRender();
	}
*/
	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of level ERROR or up. This is a convenience method; same as
	 * calling 'anyMessage(FeedbackMessage.ERROR)'.
	 * 
	 * @return whether there is any message for this panel of level ERROR or up
	 */
	public final boolean anyErrorMessage() {
		return anyMessage(FeedbackMessage.ERROR);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message.
	 * 
	 * @return whether there is any message for this panel
	 */
	public final boolean anyMessage() {
		return anyMessage(FeedbackMessage.UNDEFINED);
	}

	/**
	 * Search messages that this panel will render, and see if there is any
	 * message of the given level.
	 * 
	 * @param level
	 *            the level, see FeedbackMessage
	 * @return whether there is any message for this panel of the given level
	 */
	public final boolean anyMessage(int level) {
		List<FeedbackMessage> msgs = getCurrentMessages();

		for (FeedbackMessage msg : msgs) {
			if (msg.isLevel(level)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return Model for feedback messages on which you can install filters and
	 *         other properties
	 */
	public final FeedbackMessagesModel getFeedbackMessagesModel() {
		return (FeedbackMessagesModel) messageListView.getDefaultModel();
	}

	/**
	 * @return The current message filter
	 */
	public final IFeedbackMessageFilter getFilter() {
		return getFeedbackMessagesModel().getFilter();
	}

	/**
	 * @return The current sorting comparator
	 */
	public final Comparator<FeedbackMessage> getSortingComparator() {
		return getFeedbackMessagesModel().getSortingComparator();
	}

	/**
	 * @see org.apache.wicket.Component#isVersioned()
	 */
	@Override
	public boolean isVersioned() {
		return false;
	}

	/**
	 * Sets a filter to use on the feedback messages model
	 * 
	 * @param filter
	 *            The message filter to install on the feedback messages model
	 * 
	 * @return FeedbackPanel this.
	 */
	public final ValidationErrorFeedbackBorder setFilter(IFeedbackMessageFilter filter) {
		getFeedbackMessagesModel().setFilter(filter);
		return this;
	}

	/**
	 * @param maxMessages
	 *            The maximum number of feedback messages that this feedback
	 *            panel should show at one time
	 * 
	 * @return FeedbackPanel this.
	 */
	public final ValidationErrorFeedbackBorder setMaxMessages(int maxMessages) {
		messageListView.setViewSize(maxMessages);
		return this;
	}

	/**
	 * Sets the comparator used for sorting the messages.
	 * 
	 * @param sortingComparator
	 *            comparator used for sorting the messages.
	 * 
	 * @return FeedbackPanel this.
	 */
	public final ValidationErrorFeedbackBorder setSortingComparator(Comparator<FeedbackMessage> sortingComparator) {
		getFeedbackMessagesModel().setSortingComparator(sortingComparator);
		return this;
	}

	/**
	 * Gets the css class for the given message.
	 * 
	 * @param message
	 *            the message
	 * @return the css class; by default, this returns feedbackPanel + the
	 *         message level, eg 'feedbackPanelERROR', but you can override this
	 *         method to provide your own
	 */
	protected String getCSSClass(final FeedbackMessage message) {
		// TODO: integrate with the final stylesheet
		if (message.isError()) {
			return "errorTxt errorTxtSpan";
		}

		return message.getLevelAsString();
	}

	/**
	 * Gets the currently collected messages for this panel.
	 * 
	 * @return the currently collected messages for this panel, possibly empty
	 */
	protected final List<FeedbackMessage> getCurrentMessages() {
		final List<FeedbackMessage> messages = messageListView.getModelObject();
		return Collections.unmodifiableList(messages);
	}

	/**
	 * Gets a new instance of FeedbackMessagesModel to use.
	 * 
	 * @return Instance of FeedbackMessagesModel to use
	 */
	protected FeedbackMessagesModel newFeedbackMessagesModel() {
		return new FeedbackMessagesModel(this);
	}

	/**
	 * Generates a component that is used to display the message inside the
	 * feedback panel. This component must handle being attached to <code>span</code> tags.
	 * 
	 * By default a {@link Label} is used.
	 * 
	 * Note that the created component is expected to respect feedback panel's {@link #getEscapeModelStrings()} value
	 * 
	 * @param id
	 *            parent id
	 * @param message
	 *            feedback message
	 * @return component used to display the message
	 */
	protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
		Serializable serializable = message.getMessage();
		Label label = new Label(id, (serializable == null) ? "" : serializable.toString());
		label.setEscapeModelStrings(ValidationErrorFeedbackBorder.this.getEscapeModelStrings());
		return label;
	}

}
