package com.temenos.ebank.common.wicket.feedback;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.FeedbackMessagesModel;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * A panel that displays alerts in a list view. The
 * maximum number of messages to show can be set with setMaxMessages().
 * 
 * @see org.apache.wicket.feedback.FeedbackMessage
 * @see org.apache.wicket.feedback.FeedbackMessages
 * @author Jonathan Locke
 * @author Eelco Hillenius
 * @author vionescu
 */
public class AlertFeedbackPanel extends Panel implements IFeedback {
	protected static Log logger = LogFactory.getLog(AlertFeedbackPanel.class);

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
				 * Returns feedbackPanel + the message level, eg 'feedbackPanelERROR'. This is used
				 * as the class of the li / span elements.
				 * 
				 * @see org.apache.wicket.model.IModel#getObject()
				 */
				@Override
				public String getObject() {
					return getCSSClass(message);
				}
			};

			final Component alertMessagePanel = newMessageDisplayComponent("alertMessage", message);
			final AttributeModifier levelModifier = new AttributeModifier("class", replacementModel);
			alertMessagePanel.add(levelModifier);
			listItem.add(levelModifier);
			listItem.add(alertMessagePanel);
		}
	}

	private static final long serialVersionUID = 1L;

	/** Message view */
	private final MessageListView messageListView;

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 */
	public AlertFeedbackPanel(final String id) {
		this(id, null);
	}

	/**
	 * @see org.apache.wicket.Component#Component(String)
	 * 
	 * @param id
	 * @param filter
	 */
	public AlertFeedbackPanel(final String id, IFeedbackMessageFilter filter) {
		super(id);
		WebMarkupContainer messagesContainer = new WebMarkupContainer("alertul") {
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

		if (filter != null) {
			setFilter(filter);
		}
	}

	/**
	 * Search messages that this panel will render, and see if there is any message of level ERROR
	 * or up. This is a convenience method; same as calling 'anyMessage(FeedbackMessage.ERROR)'.
	 * 
	 * @return whether there is any message for this panel of level ERROR or up
	 */
	public final boolean anyErrorMessage() {
		return anyMessage(FeedbackMessage.ERROR);
	}

	/**
	 * Search messages that this panel will render, and see if there is any message.
	 * 
	 * @return whether there is any message for this panel
	 */
	public final boolean anyMessage() {
		return anyMessage(FeedbackMessage.UNDEFINED);
	}

	/**
	 * Search messages that this panel will render, and see if there is any message of the given
	 * level.
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
	 * @return Model for feedback messages on which you can install filters and other properties
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
	public final AlertFeedbackPanel setFilter(IFeedbackMessageFilter filter) {
		getFeedbackMessagesModel().setFilter(filter);
		return this;
	}

	/**
	 * @param maxMessages
	 *            The maximum number of feedback messages that this feedback panel should show at
	 *            one time
	 * 
	 * @return FeedbackPanel this.
	 */
	public final AlertFeedbackPanel setMaxMessages(int maxMessages) {
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
	public final AlertFeedbackPanel setSortingComparator(Comparator<FeedbackMessage> sortingComparator) {
		getFeedbackMessagesModel().setSortingComparator(sortingComparator);
		return this;
	}

	/**
	 * Gets the css class for the given message.
	 * 
	 * @param message
	 *            the message
	 * @return the css class;
	 */
	protected String getCSSClass(final FeedbackMessage message) {
		return "alertPanel";// + message.getLevelAsString();
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
	 * Generates the corresponding alert panel component that is used to display the alert message inside the feedback
	 * panel.
	 * If the corresponding alert panel could not be located, then a technical error panel is displayed
	 * Note that the created component is expected to respect feedback panel's {@link #getEscapeModelStrings()} value
	 * 
	 * @param id
	 *            parent id
	 * @param message
	 *            feedback message
	 * @return component used to display the message
	 */
	protected Component newMessageDisplayComponent(String id, FeedbackMessage message) {
		if (!(message.getMessage() instanceof Alert)) {
			throw new RuntimeException("Invalid alert: " + String.valueOf(message.getMessage()));
		}
		Alert alert = (Alert) message.getMessage();
		String alertCode = alert.getResponseCode().getCode();
		String alertClassName = String.format("com.temenos.ebank.common.wicket.feedback.Alert%sPanel", alertCode);
		try {
			Class<?> alertPanelClass = Class.forName(alertClassName);
			Constructor<?> c = null;
			Panel alertPanel = null;
			try {
				try {
					c = alertPanelClass.getConstructor(String.class, Alert.class);
					alertPanel = (Panel) c.newInstance(id, alert);
					return alertPanel;
				} catch (Exception e1) {
					logger.debug(String.format(
							"Could not find constructor with 2 args for panel %s, searching for 1 arg constructor",
							alertClassName));
				}

				c = alertPanelClass.getConstructor(String.class);
				alertPanel = (Panel) c.newInstance(id);
				logger.debug(String.format("Found 1-arg constructor for panel %s, searching for 1 arg constructor",
						alertClassName));
				return alertPanel;
			} catch (Exception e) {
				logger.error("Could not instantiate panel for alert " + alertClassName, e);
			}
		} catch (ClassNotFoundException e) {
			logger.error("No alert panel defined for class " + alertClassName, e);
		}
		// failed to present the propper panel, alert a technical error
		return new Alert0009Panel(id);

	}
	// @Override
	// protected void onBeforeRender() {
	// super.onBeforeRender();
	// //cleanup messages so that they won't show in subsequent feedback panels
	// Session.get().getFeedbackMessages().clear(getFeedbackMessagesModel().getFilter());
	//
	// }
}
