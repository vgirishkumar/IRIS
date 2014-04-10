package com.temenos.interaction.rimdsl.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider.AbstractImpl;
import org.eclipse.xtext.naming.QualifiedName;

import com.temenos.interaction.rimdsl.rim.DomainDeclaration;
import com.temenos.interaction.rimdsl.rim.Event;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

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

	@Override
	public QualifiedName getFullyQualifiedName(EObject obj) {
		if (obj instanceof DomainDeclaration) {
			DomainDeclaration d = (DomainDeclaration) obj;
			return QualifiedName.create(d.getName());
		} else if (obj instanceof Event) {
			Event e = (Event) obj;
			ResourceInteractionModel parent = (ResourceInteractionModel) e.eContainer();
			return QualifiedName.create(parent.getName(), e.getName());
		// TODO more ... } else if (obj instanceof Event) {
		}
		return null;
	}

}
