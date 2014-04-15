package com.temenos.interaction.rimdsl.scoping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider.AbstractImpl;
import org.eclipse.xtext.naming.QualifiedName;

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

	public QualifiedName getFullyQualifiedName(EObject obj) {
		QualifiedName partialQualifiedName = null;
		if (obj instanceof DomainDeclaration) {
			DomainDeclaration d = (DomainDeclaration) obj;
			return QualifiedName.create(d.getName());
		} else if (obj instanceof ResourceInteractionModel) {
			ResourceInteractionModel r = (ResourceInteractionModel) obj;
			partialQualifiedName = QualifiedName.create(r.getName());
		} else if (obj instanceof State) {
			State s = (State) obj;
			partialQualifiedName = QualifiedName.create(s.getName());
		} else if (obj instanceof Event) {
			Event e = (Event) obj;
			return QualifiedName.create(e.getName());
		} else if (obj instanceof Command) {
			Command c = (Command) obj;
			return QualifiedName.create(c.getName());
		} else if (obj instanceof Relation) {
			Relation r = (Relation) obj;
			return QualifiedName.create(r.getName());
		}
		
		if (partialQualifiedName != null) {
			EObject temp = obj;
			while (temp.eContainer() != null) {
				temp = temp.eContainer();
				QualifiedName parentsQualifiedName = getFullyQualifiedName(temp);
				if (parentsQualifiedName != null)
					return parentsQualifiedName.append(partialQualifiedName);
			}
		}
		
		return null;
	}

}
