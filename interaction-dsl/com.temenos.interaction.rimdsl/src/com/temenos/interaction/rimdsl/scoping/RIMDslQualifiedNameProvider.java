package com.temenos.interaction.rimdsl.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider.AbstractImpl;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.Command;
import com.temenos.interaction.rimdsl.rim.DomainDeclaration;
import com.temenos.interaction.rimdsl.rim.Event;
import com.temenos.interaction.rimdsl.rim.Relation;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import com.temenos.interaction.rimdsl.rim.State;

/**
 * IRIS QualifiedNameProvider.
 * 
 * Don't use the DefaultDeclarativeQualifiedNameProvider, as its polymorphic
 * thing seems to be a slower (measured), and its cache seems to have memory
 * impacts; simply coding this out explicit/clear seems to be better.
 * 
 * @see http://www.eclipse.org/Xtext/documentation.html#scoping
 * 
 * @author Michael Vorburger
 */
public class RIMDslQualifiedNameProvider extends AbstractImpl {

	@Inject
	private IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

	public QualifiedName getFullyQualifiedName(EObject obj) {
		QualifiedName partialQualifiedName = null;
		if (obj instanceof DomainDeclaration) {
			DomainDeclaration d = (DomainDeclaration) obj;
			partialQualifiedName = toQualifiedName(d.getName());
		} else if (obj instanceof ResourceInteractionModel) {
			ResourceInteractionModel r = (ResourceInteractionModel) obj;
			partialQualifiedName = toQualifiedName(r.getName());
		} else if (obj instanceof State) {
			State s = (State) obj;
			partialQualifiedName = toQualifiedName(s.getName());
		} else if (obj instanceof Event) {
			Event e = (Event) obj;
			partialQualifiedName = toQualifiedName(e.getName());
		} else if (obj instanceof Command) {
			Command c = (Command) obj;
			partialQualifiedName = toQualifiedName(c.getName());
		} else if (obj instanceof Relation) {
			Relation r = (Relation) obj;
			partialQualifiedName = toQualifiedName(r.getName());
		} else {
			return null;
		}
		
		if (partialQualifiedName != null) {
			EObject parent = obj.eContainer();
			QualifiedName parentsQualifiedName = getFullyQualifiedName(parent);
			if (parentsQualifiedName != null)
				partialQualifiedName = parentsQualifiedName.append(partialQualifiedName);
		}
		
		return partialQualifiedName;
	}

	private QualifiedName toQualifiedName(String name) {
		if (name != null)
			return converter.toQualifiedName(name);
		return null;
	}
}
