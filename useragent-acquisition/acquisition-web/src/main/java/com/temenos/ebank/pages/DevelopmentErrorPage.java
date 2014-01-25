package com.temenos.ebank.pages;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.wicket.markup.html.basic.Label;

public class DevelopmentErrorPage extends EbankInternalErrorPage {

	public DevelopmentErrorPage( Exception exception ){

		//do something about the exception
		final Writer stackWriter = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(stackWriter);
	    exception.printStackTrace(printWriter);
		Label label = new Label( "stackTrace", stackWriter.toString() );
		add(label);
	}
}
