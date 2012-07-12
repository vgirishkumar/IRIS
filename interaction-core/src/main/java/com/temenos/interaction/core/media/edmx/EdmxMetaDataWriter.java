package com.temenos.interaction.core.media.edmx;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.odata4j.core.NamespacedAnnotation;
import org.odata4j.core.PrefixedNamespace;
import org.odata4j.edm.EdmAnnotationAttribute;
import org.odata4j.edm.EdmAnnotationElement;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmDocumentation;
import org.odata4j.edm.EdmEntityContainer;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmFunctionParameter;
import org.odata4j.edm.EdmItem;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmProperty.CollectionKind;
import org.odata4j.edm.EdmSchema;
import org.odata4j.format.xml.XmlFormatWriter;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.XMLFactoryProvider2;
import org.odata4j.stax2.XMLWriter2;

import com.temenos.interaction.core.link.CollectionResourceState;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.Transition;

public class EdmxMetaDataWriter extends XmlFormatWriter {

  public static void write(EdmDataServices services, Writer w, ResourceRegistry resourceRegistry) {

	  //Map<Relation name, Entity relation>
      Map<String, EntityRelation> relations = new HashMap<String, EntityRelation>();
	  
    XMLWriter2 writer = XMLFactoryProvider2.getInstance().newXMLWriterFactory2().createXMLWriter(w);
    writer.startDocument();

    writer.startElement(new QName2(edmx, "Edmx", "edmx"));
    writer.writeAttribute("Version", "1.0");
    writer.writeNamespace("edmx", edmx);
    writer.writeNamespace("d", d);
    writer.writeNamespace("m", m);
    writeExtensionNamespaces(services, writer);

    writer.startElement(new QName2(edmx, "DataServices", "edmx"));
    writer.writeAttribute(new QName2(m, "DataServiceVersion", "m"), "1.0");

    // Schema
    for (EdmSchema schema : services.getSchemas()) {

      writer.startElement(new QName2("Schema"), edm);
      writer.writeAttribute("Namespace", schema.getNamespace());
      writeAnnotationAttributes(schema, writer);
      writeDocumentation(schema, writer);

      // ComplexType
      for (EdmComplexType ect : schema.getComplexTypes()) {
        writer.startElement(new QName2("ComplexType"));

        writer.writeAttribute("Name", ect.getName());
        if (null != ect.getIsAbstract()) {
          writer.writeAttribute("Abstract", ect.getIsAbstract().toString());
        }
        writeAnnotationAttributes(ect, writer);
        writeDocumentation(ect, writer);

        writeProperties(ect.getProperties(), writer);
        writeAnnotationElements(ect, writer);
        writer.endElement("ComplexType");
      }
      // EntityType
      for (EdmEntityType eet : schema.getEntityTypes()) {
        writer.startElement(new QName2("EntityType"));
        String entityName = eet.getName();
        writer.writeAttribute("Name", entityName);
        if (null != eet.getIsAbstract()) {
          writer.writeAttribute("Abstract", eet.getIsAbstract().toString());
        }

        if (Boolean.TRUE.equals(eet.getHasStream())) {
          writer.writeAttribute(new QName2(m, "HasStream", "m"), "true");
        }

        // keys only on base types
        if (eet.isRootType()) {
          writeAnnotationAttributes(eet, writer);
          writeDocumentation(eet, writer);
          writer.startElement(new QName2("Key"));
          for (String key : eet.getKeys()) {
            writer.startElement(new QName2("PropertyRef"));
            writer.writeAttribute("Name", key);
            writer.endElement("PropertyRef");
          }

          writer.endElement("Key");
        } else {
          writer.writeAttribute("BaseType", eet.getBaseType().getFullyQualifiedTypeName());
          writeAnnotationAttributes(eet, writer);
          writeDocumentation(eet, writer);
        }

        writeProperties(eet.getDeclaredProperties(), writer);

        //Obtain the relation between entities and write navigation properties
        Set<String> npNames = new HashSet<String>();
        List<Transition> entityTransitions = resourceRegistry.getEntityTransitions(entityName);
		if(entityTransitions != null) {
			for(Transition entityTransition : entityTransitions) {
				ResourceState sourceState = entityTransition.getSource();
				ResourceState targetState = entityTransition.getTarget();
				int multiplicity = (entityTransition.getTarget().getClass() == CollectionResourceState.class) ? EntityRelation.MULTIPLICITY_TO_MANY : EntityRelation.MULTIPLICITY_TO_ONE;

				//Use the entity names to define the relation
				String relationName = sourceState.getEntityName() + "_" + targetState.getEntityName();
				String invertedRelationName = targetState.getEntityName() + "_" + sourceState.getEntityName();
				if(relations.containsKey(invertedRelationName)) {
					relationName = invertedRelationName;					
				}
				
        		//Obtain the relation between the source and target entities
				EntityRelation relation = new EntityRelation(relationName, schema.getNamespace(), 
	            		sourceState.getEntityName(), targetState.getEntityName(), multiplicity);
				if(!relations.containsKey(relationName)) {
		            relations.put(relationName, relation);
				}

				//Write the navigation properties
				if(!npNames.contains(targetState.getName())) {		//We can have transitions to a resource state from multiple source states
					String npName = targetState.getName();
					writer.startElement(new QName2("NavigationProperty"));
		            writer.writeAttribute("Name", npName);
		            writer.writeAttribute("Relationship", relation.getNamespace() + "." + relation.getName());
		            writer.writeAttribute("FromRole", relation.getSourceEntityName());
		            writer.writeAttribute("ToRole", relation.getTargetEntityName());
		            writer.endElement("NavigationProperty");
		            npNames.add(npName);
				}
			}
		}
        
        writeAnnotationElements(eet, writer);
        writer.endElement("EntityType");

      }

      //Associations
      for(EntityRelation relation : relations.values()) {
          writer.startElement(new QName2("Association"));
      	
          writer.writeAttribute("Name", relation.getName());

          writer.startElement(new QName2("End"));
          writer.writeAttribute("Role", relation.getSourceEntityName());
          writer.writeAttribute("Type", relation.getNamespace() + "." + relation.getSourceEntityName());
          if(relation.getMultiplicity() == EntityRelation.MULTIPLICITY_TO_MANY) {
        	  writer.writeAttribute("Multiplicity", "0..1");
          }
          else {
        	  writer.writeAttribute("Multiplicity", "*");
          }
          writer.endElement("End");

          writer.startElement(new QName2("End"));
          writer.writeAttribute("Role", relation.getTargetEntityName());
          writer.writeAttribute("Type", relation.getNamespace() + "." + relation.getTargetEntityName());
          if(relation.getMultiplicity() == EntityRelation.MULTIPLICITY_TO_MANY) {
        	  writer.writeAttribute("Multiplicity", "*");
          }
          else {
        	  writer.writeAttribute("Multiplicity", "0..1");
          }
          writer.endElement("End");

          writer.endElement("Association");
      }
				
      // EntityContainer
      for (EdmEntityContainer container : schema.getEntityContainers()) {
        writer.startElement(new QName2("EntityContainer"));

        writer.writeAttribute("Name", container.getName());
        writer.writeAttribute(new QName2(m, "IsDefaultEntityContainer", "m"), Boolean.toString(container.isDefault()));
        writeAnnotationAttributes(container, writer);
        writeDocumentation(container, writer);

        for (EdmEntitySet ees : container.getEntitySets()) {
          writer.startElement(new QName2("EntitySet"));
          writer.writeAttribute("Name", ees.getName());
          writer.writeAttribute("EntityType", ees.getType().getFullyQualifiedTypeName());
          writeAnnotationAttributes(ees, writer);
          writeDocumentation(ees, writer);
          writeAnnotationElements(ees, writer);
          writer.endElement("EntitySet");
        }

        for (EdmFunctionImport fi : container.getFunctionImports()) {
          writer.startElement(new QName2("FunctionImport"));
          writer.writeAttribute("Name", fi.getName());
          if (null != fi.getEntitySet()) {
            writer.writeAttribute("EntitySet", fi.getEntitySet().getName());
          }
          if (fi.getReturnType() != null) {
            // TODO: how to differentiate inline ReturnType vs embedded ReturnType?
            writer.writeAttribute("ReturnType", fi.getReturnType().getFullyQualifiedTypeName());
          }
          writer.writeAttribute(new QName2(m, "HttpMethod", "m"), fi.getHttpMethod());
          writeAnnotationAttributes(fi, writer);
          writeDocumentation(fi, writer);

          for (EdmFunctionParameter param : fi.getParameters()) {
            writer.startElement(new QName2("Parameter"));
            writer.writeAttribute("Name", param.getName());
            writer.writeAttribute("Type", param.getType().getFullyQualifiedTypeName());
            if (param.getMode() != null)
              writer.writeAttribute("Mode", param.getMode().toString());
            writeAnnotationAttributes(param, writer);
            writeDocumentation(param, writer);
            writeAnnotationElements(param, writer);
            writer.endElement("Parameter");
          }
          writeAnnotationElements(fi, writer);
          writer.endElement("FunctionImport");
        }

        //Association set 
        for(EntityRelation relation : relations.values()) {
            writer.startElement(new QName2("AssociationSet"));
            writer.writeAttribute("Name", relation.getName());
            writer.writeAttribute("Association", relation.getNamespace() + "." + relation.getName());

            writer.startElement(new QName2("End"));
            writer.writeAttribute("Role", relation.getSourceEntityName());
            writer.writeAttribute("EntitySet", relation.getSourceEntityName());
            writer.endElement("End");

            writer.startElement(new QName2("End"));
            writer.writeAttribute("Role", relation.getTargetEntityName());
            writer.writeAttribute("EntitySet", relation.getTargetEntityName());
            writer.endElement("End");

            writer.endElement("AssociationSet");
          }
        
        writeAnnotationElements(container, writer);
        writer.endElement("EntityContainer");
      }

      writeAnnotationElements(schema, writer);
      writer.endElement("Schema");

    }

    writer.endDocument();
  }

  /**
   * Extensions to CSDL like Annotations appear in an application specific set
   * of namespaces.
   */
  private static void writeExtensionNamespaces(EdmDataServices services, XMLWriter2 writer) {
    if (null != services.getNamespaces()) {
      for (PrefixedNamespace ns : services.getNamespaces()) {
        writer.writeNamespace(ns.getPrefix(), ns.getUri());
      }
    }
  }

  private static void writeProperties(Iterable<EdmProperty> properties, XMLWriter2 writer) {
    for (EdmProperty prop : properties) {
      writer.startElement(new QName2("Property"));

      writer.writeAttribute("Name", prop.getName());
      writer.writeAttribute("Type", prop.getType().getFullyQualifiedTypeName());
      writer.writeAttribute("Nullable", prop.isNullable() ? "false" : "true");		//odata4j bug - inverted values when loaded
      if (prop.getMaxLength() != null) {
        writer.writeAttribute("MaxLength", Integer.toString(prop.getMaxLength()));
      }
      if (!prop.getCollectionKind().equals(CollectionKind.NONE)) {
        writer.writeAttribute("CollectionKind", prop.getCollectionKind().toString());
      }
      if (prop.getDefaultValue() != null) {
        writer.writeAttribute("DefaultValue", prop.getDefaultValue());
      }
      if (prop.getPrecision() != null) {
        writer.writeAttribute("Precision", Integer.toString(prop.getPrecision()));
      }
      if (prop.getScale() != null) {
        writer.writeAttribute("Scale", Integer.toString(prop.getPrecision()));
      }
      writeAnnotationAttributes(prop, writer);
      writeAnnotationElements(prop, writer);
      writer.endElement("Property");
    }
  }

  private static void writeAnnotationAttributes(EdmItem item, XMLWriter2 writer) {
    if (null != item.getAnnotations()) {
      for (NamespacedAnnotation<?> a : item.getAnnotations()) {
        if (a instanceof EdmAnnotationAttribute) {
          writer.writeAttribute(
              new QName2(a.getNamespace().getUri(), a.getName(), a.getNamespace().getPrefix()),
              a.getValue() == null ? "" : a.getValue().toString());
        }
      }
    }
  }

  private static void writeAnnotationElements(EdmItem item, XMLWriter2 writer) {
    if (null != item.getAnnotations()) {
      for (NamespacedAnnotation<?> a : item.getAnnotations()) {
        if (a instanceof EdmAnnotationElement) {
          // TODO: please don't throw an exception here.
          // this totally breaks ODataConsumer even thought it doesn't rely
          // on annotations.  A no-op is a interim approach that allows work
          // to proceed by those using queryable metadata to access annotations.
          // throw new UnsupportedOperationException("Implement element annotations");
        }
      }
    }
  }

  private static void writeDocumentation(EdmItem item, XMLWriter2 writer) {
    EdmDocumentation doc = item.getDocumentation();
    if (null != doc && (null != doc.getSummary() || null != doc.getLongDescription())) {
      QName2 d = new QName2(edm, "Documentation");
      writer.startElement(d);
      {
        if (null != doc.getSummary()) {
          QName2 s = new QName2(edm, "Summary");
          writer.startElement(s);
          writer.writeText(doc.getSummary());
          writer.endElement(s.getLocalPart());
        }
        if (null != doc.getLongDescription()) {
          QName2 s = new QName2(edm, "LongDescription");
          writer.startElement(s);
          writer.writeText(doc.getLongDescription());
          writer.endElement(s.getLocalPart());
        }
      }
      writer.endElement(d.getLocalPart());
    }
  }

}
