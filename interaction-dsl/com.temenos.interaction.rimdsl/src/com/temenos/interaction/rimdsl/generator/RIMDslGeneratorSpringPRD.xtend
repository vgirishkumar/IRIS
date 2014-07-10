/*
 * Our Xtext Java class generator
 */
package com.temenos.interaction.rimdsl.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.generator.IFileSystemAccess
import com.temenos.interaction.rimdsl.rim.ResourceCommand
import com.temenos.interaction.rimdsl.rim.State
import com.temenos.interaction.rimdsl.rim.Transition
import com.temenos.interaction.rimdsl.rim.TransitionForEach
import com.temenos.interaction.rimdsl.rim.TransitionAuto
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel
import org.eclipse.emf.common.util.EList
import com.temenos.interaction.rimdsl.rim.UriLink
import com.temenos.interaction.rimdsl.rim.OKFunction;
import com.temenos.interaction.rimdsl.rim.NotFoundFunction
import com.temenos.interaction.rimdsl.rim.Function
import com.temenos.interaction.rimdsl.rim.Expression
import javax.inject.Inject
import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.temenos.interaction.rimdsl.rim.ImplRef
import com.temenos.interaction.rimdsl.rim.RelationConstant
import com.temenos.interaction.rimdsl.rim.Relation
import com.temenos.interaction.rimdsl.rim.TransitionEmbedded
import com.temenos.interaction.rimdsl.rim.TransitionRedirect
import com.temenos.interaction.rimdsl.rim.TransitionRef

class RIMDslGeneratorSpringPRD implements IGenerator {
	
	@Inject extension IQualifiedNameProvider
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
	    if (resource == null) {
            throw new RuntimeException("Generator called with null resource");	        
	    }
        for (rim : resource.allContents.toIterable.filter(typeof(ResourceInteractionModel))) {
            generate(resource, rim, fsa);
        }
	}
		
	def void generate(Resource resource, ResourceInteractionModel rim, IFileSystemAccess fsa) {
        // generate Behaviour class
        var rimPath = rim.fullyQualifiedName.toString("/")
                  
        // generate resource classes
        for (resourceState : rim.states) {
            val stateName = resourceState.fullyQualifiedName.toString("_")
            fsa.generateFile("IRIS-" + stateName + "-PRD.xml", toSpringXML(rim, resourceState))
        }
               
        fsa.generateFile(rimPath + "ServiceDocumentIRIS-PRD.xml", toSpringServiceDocXML(rim))
  
	}

	
	def className(Resource res) {
		var name = res.URI.lastSegment
		return name.substring(0, name.indexOf('.'))
	}
	
    	
	def toSpringXML(ResourceInteractionModel rim, State state) '''
 «addXmlStart()»
 	
      	<!-- Define Spring bean for resource : «state.name» -->
        «addXMLResourceBean( rim,  state)»
        
        <!-- Start property transitions list -->
        «addXmlStartTransitions»
		         	
       	«IF state.type.isCollection»
        	<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", createLinkRelations(), null,                     «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->   
        «ELSEIF state.type.isItem»
        	<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", createLinkRelations(), «if (state.path != null) { "new UriSpecification(\"" + state.name + "\", \"" + producePath(rim, state) + "\")" } else { "null" }», «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->
        «ENDIF»
             
        «IF state.isException»
        	setException(true);
        «ENDIF»
               
                <!--  xxx  StateName = « state.name » -->
                <!-- create transitions  -->
				«FOR t : state.transitions»
		    	    «IF (t.state != null && t.state.name != null) 
		    	    	|| (t.name != null ) »
				    				
					    «IF t instanceof Transition»                
					    	«produceTransitions(rim, state, t as Transition)»
					    «ENDIF»
					
					    «IF t instanceof TransitionForEach»                
					    	«produceTransitionsForEach(rim, state, t as TransitionForEach)»
					    «ENDIF»
					
					    «IF t instanceof TransitionAuto»                
					    	«produceTransitionsAuto(rim, state, t as TransitionAuto)»
					    «ENDIF»
					
					    «IF t instanceof TransitionRedirect»                
					    	«produceTransitionsRedirect(rim, state, t as TransitionRedirect)»
					    «ENDIF»
					
					    «IF t instanceof TransitionEmbedded»                
					    	«produceTransitionsEmbedded(rim, state, t as TransitionEmbedded)»
					    «ENDIF»
				    «ENDIF»
				«ENDFOR»
	«addXmlEndTransitions»
            <!-- 
               }

            private static String[] createLinkRelations() {
            
                «produceRelations(state)»
              
                return «state.name»Relations;
            } -->
            
«addXmlEnd()»
        
	'''

	// Start XML for service document and resources.
	def addXmlStart() ''' 
<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 -->

<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xsi:schemaLocation="
		http://www.springframework.org/schema/util 
		http://www.springframework.org/schema/util/spring-util-3.0.xsd
		http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
		http://www.springframework.org/schema/context
		http://www.springframework.org/schema/context/spring-context-3.0.xsd">

'''
	
	// End XML for service document and resources.
	def addXmlEnd() ''' 
</beans>
	'''		
	
	// Add util Map for resources.
	def addXmlStartTransitions() ''' 
        <property name="transitions">
	        <list>

'''		
	// Add util Map for resources.
	def addXmlEndTransitions() ''' 
	        </list>
	    </property>
    </bean>

'''		

	// Add Spring bean for resource.
	def addXMLResourceBean(ResourceInteractionModel rim, State state) ''' 
   <bean id="« state.fullyQualifiedName.toString("_") »" class="com.temenos.interaction.core.hypermedia.CollectionResourceState">
        <constructor-arg name="entityName" value="«state.entity.name»" />
        <constructor-arg name="name" value="« state.name »" />
        <constructor-arg>
            <list>
			«produceActionList(state, state.impl)»
            </list>
        </constructor-arg>
        <constructor-arg name="path" value="«producePath(rim, state)»" />
'''		
	// Add Spring TransitionFactoryBean.
	def addXMLTransitionFactoryBean(String target) ''' 
      <bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
          <property name="method" value="GET" />
          <property name="target" ref="« target »" />
      </bean>
'''		

	// Add ServiceDoc Spring bean for initialState.
	def addXMLStartServiceDocInitialBean() ''' 
    <!-- initialState -->
    <bean id="initialState" class="com.temenos.interaction.core.hypermedia.ResourceState">
        <constructor-arg name="entityName" value="ServiceDocument" />
        <constructor-arg name="name" value="ServiceDocument" />
        <constructor-arg>
            <list>
                <bean class="com.temenos.interaction.core.hypermedia.Action">
                    <constructor-arg value="GETServiceDocument" />
                    <constructor-arg value="VIEW" />
                </bean>
            </list>
        </constructor-arg>
        <constructor-arg name="path" value="/" />
'''		

	def addXMLEndServiceDocInitialBean() ''' 
 	        </list>
	    </property>
    </bean>
'''		

	def String transitionTargetStateVariableName(TransitionRef t) {
		if (t.state != null) {
			return stateVariableName(t.state);
		} else {
       		return "" + (t.name).replaceAll("\\.", "_");
		}
	}

	def String stateVariableName(State state) {
		if (state != null && state.name != null) {
			return "" + (state.fullyQualifiedName).toString("_");
		}
		return null;
	}
	
  	// Spring XML definition for Service document
	def toSpringServiceDocXML(ResourceInteractionModel rim) '''
 «addXmlStart()»
 	«addXMLStartServiceDocInitialBean()»
        <!-- Start property transitions list -->
        «addXmlStartTransitions»
        «FOR resourceState :rim.states»
        	«addXMLTransitionFactoryBean(resourceState.name)»
        «ENDFOR»

 	«addXMLEndServiceDocInitialBean()»
«addXmlEnd()»
		
	'''
	
	def produceResourceStates(ResourceInteractionModel rim, State state) '''
            «produceActionList(state, state.impl)»
            «produceRelations(state)»
            «IF state.type.isCollection»
            xxx CollectionResourceState «stateVariableName(state)» = new CollectionResourceState("«state.entity.name»", "«state.name»", «state.name»Actions, "«producePath(rim, state)»", «state.name»Relations, null);
            «ELSEIF state.type.isItem»
            ResourceState «stateVariableName(state)» = new ResourceState("«state.entity.name»", "«state.name»", «state.name»Actions, "«producePath(rim, state)»", «stateVariableName(state)»Relations«if (state.path != null) { ", new UriSpecification(\"" + state.name + "\", \"" + producePath(rim, state) + "\")" }»);
            «ENDIF»
            «IF state.isException»
            «stateVariableName(state)».setException(true);
            «ENDIF»
	'''

    def producePath(ResourceInteractionModel rim, State state) '''«
    	// prepend the basepath
	    if (rim.basepath != null) {
		    if (state.path != null) { rim.basepath.name + state.path.name } else { rim.basepath.name + "/" + state.name }
		} else {
		    if (state.path != null) { state.path.name } else { "/" + state.name }
		}
    »'''

    def produceRelations(State state) '''
 	// produceRelations()    
        «IF state.relations != null && state.relations.size > 0»
        String «state.name»RelationsStr = "";
        «FOR relation : state.relations»
        «IF relation instanceof RelationConstant»
        «state.name»RelationsStr += "«(relation as RelationConstant).name» ";
        «ELSE»
        «state.name»RelationsStr += "«(relation.relation as Relation).fqn» ";
        «ENDIF»
        «ENDFOR»
        String[] «state.name»Relations = «state.name»RelationsStr.trim().split(" ");
        «ELSE»
        String[] «state.name»Relations = null;
        «ENDIF»
    '''

    def produceActionList(State state, ImplRef impl) {
    	if (impl != null) {
   			produceActionList(state, impl.view, impl.actions);
    	}
    }

    def produceActionList(State state, ResourceCommand view, EList<ResourceCommand> actions) '''
        «IF "".equals("DISABLED") 
        	&& view != null && ((view.command.spec != null && view.command.spec.properties.size > 0) || view.properties.size > 0)»
            actionViewProperties = new Properties();
            «IF view.command.spec != null && view.command.spec.properties.size > 0»
            «FOR commandProperty :view.command.spec.properties»
                actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
	        	«ENDFOR»
	        «ENDIF»
            «FOR commandProperty :view.properties»
            actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
            «ENDFOR»
        «ENDIF»
        «IF view != null»
            <bean class="com.temenos.interaction.core.hypermedia.Action">
                <constructor-arg value="« view.command.name »" />
                <constructor-arg value="VIEW" />
            </bean>
        «ENDIF»
        «IF actions != null»
            «FOR action : actions»
            «IF "".equals("DISABLED") 
            	&& action != null && ((action.command.spec != null && action.command.spec.properties.size > 0) || action.properties.size > 0)»
                «IF action.command.spec != null && action.command.spec.properties.size > 0»
                    «FOR commandProperty :action.command.spec.properties»
                    actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
                    «ENDFOR»
		        «ENDIF»
                «FOR commandProperty :action.properties»
                actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
                «ENDFOR»
            «ENDIF»

<!--            
            «state.name»Actions.add(new Action("«action.command.name»", Action.TYPE.ENTRY, actionViewProperties));
-->
            <bean class="com.temenos.interaction.core.hypermedia.Action">
                <constructor-arg value="« action.command.name »" />
                <constructor-arg value="ENTRY" />
            </bean>
            «ENDFOR»
        «ENDIF»'''
    
	def produceTransitions(ResourceInteractionModel rim, State fromState, Transition transition) '''
	<!-- XXX produceTransitions() -->
	<!-- 	create regular transition  -->

            <bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
                <property name="method" value="«transition.event.httpMethod»" />
                <property name="target" ref="«transitionTargetStateVariableName(transition)»" />

 				«IF transition.spec != null»
					<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
					<property name="functionList">«produceExpressions(rim, fromState, transition.spec.eval)»</property>
				«ENDIF»
				«IF transition.spec == null»
					<property name="uriParameters"><util:map></util:map></property>
					<property name="functionList"><util:list></util:list></property>
				«ENDIF»

				<property name="label" value="«if (transition.spec != null && transition.spec.title != null) { transition.spec.title.name } else { if (transition.state != null) { transition.state.name } else { transition.name } }»" />
            </bean>
                
<!--
            «stateVariableName(fromState)».addTransition(new Transition.Builder()
            		.method("«transition.event.httpMethod»").target(«transitionTargetStateVariableName(transition)»).uriParameters(uriLinkageProperties).
            		evaluation( conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null).
            		label("«if (transition.spec != null && transition.spec.title != null) { transition.spec.title.name } else { if (transition.state != null) { transition.state.name } else { transition.name } }»")
            		.build());
-->
	'''

    def produceExpressions(ResourceInteractionModel rim, State state, Expression conditionExpression) '''
       	«IF state.type.isCollection»
        	<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", createLinkRelations(), null,                     «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->   
        «ELSEIF state.type.isItem»
        	<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", createLinkRelations(), «if (state.path != null) { "new UriSpecification(\"" + state.name + "\", \"" + producePath(rim, state) + "\")" } else { "null" }», «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->
        «ENDIF»

 <util:list>
       «IF conditionExpression != null»
         	«FOR function : conditionExpression.expressions»
        		<value>«produceExpression( rim,  state, function)»</value>
         	«ENDFOR»
        «ENDIF»
</util:list>    
'''
    def produceExpression(ResourceInteractionModel rim, State state, Function expression) '''
<!-- START produceExpression() --> 
entityName=«state.entity.name»
name=«state.name»
action=«produceActionSet(state, state.impl)»
        «IF expression instanceof OKFunction»
            function=«(expression as OKFunction).state.fullyQualifiedName» , ResourceGETExpression.Function.OK
		«ELSE»
           function=«(expression as NotFoundFunction).state.fullyQualifiedName» , ResourceGETExpression.Function.NOT_FOUND
		«ENDIF»
<!-- END produceExpression() --> 
'''
   def produceActionSet(State state, ImplRef impl) {
    	if (impl != null) {
   			produceActionSet(state, impl.view, impl.actions);
    	}
    }

    def produceActionSet(State state, ResourceCommand view, EList<ResourceCommand> actions) '''
	<!-- START produceActionSet() -->  
          «IF view != null && ((view.command.spec != null && view.command.spec.properties.size > 0) || view.properties.size > 0)»
			actionViewProperties = new Properties();
            «IF view.command.spec != null && view.command.spec.properties.size > 0»
            	«FOR commandProperty :view.command.spec.properties»
               	 	actionPropertyName = «commandProperty.name»
 					actionPropertyValue = «commandProperty.value»
	        	«ENDFOR»
	        «ENDIF»
            «FOR commandProperty :view.properties»
               	actionPropertyName = «commandProperty.name»
 				actionPropertyValue = «commandProperty.value»
            «ENDFOR»
        «ENDIF»
        «IF view != null»
        	«state.name»Actions.add(new Action("«view.command.name»", Action.TYPE.VIEW, «if (view != null && ((view.command.spec != null && view.command.spec.properties.size > 0) || view.properties.size > 0)) { "actionViewProperties" } else { "new Properties()" }»));
        «ENDIF»
        «IF actions != null»
            «FOR action : actions»
				actionViewProperties = new Properties();

             	«IF action != null && ((action.command.spec != null && action.command.spec.properties.size > 0) || action.properties.size > 0)»
                	«IF action.command.spec != null && action.command.spec.properties.size > 0»
                    	«FOR commandProperty :action.command.spec.properties»
              	 			actionPropertyName = «commandProperty.name»
 							actionPropertyValue = «commandProperty.value»
                 		«ENDFOR»
		     		«ENDIF»
                	«FOR commandProperty :action.properties»
               	 		actionPropertyName = «commandProperty.name»
 						actionPropertyValue = «commandProperty.value»
                	«ENDFOR»
            	«ENDIF»
            	«state.name»Actions.add(new Action("«action.command.name»", Action.TYPE.ENTRY, actionViewProperties));
            «ENDFOR»
        «ENDIF»
	<!-- END produceActionSet() -->  

'''

    def produceTransitionsForEach(ResourceInteractionModel rim, State fromState, TransitionForEach transition) '''
 	<!-- produceTransitionsForEach() -->  
 	<!-- 	create for each transition  -->
                
	    <bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
	        <property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.FOR_EACH"/></property>
	         <property name="method" value="« transition.event.httpMethod»" />
	         <property name="target" ref="«transitionTargetStateVariableName(transition)»" />

 				«IF transition.spec != null»
					<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
					<property name="functionList">«produceExpressions(rim, fromState, transition.spec.eval)»</property>
				«ENDIF»
				«IF transition.spec == null»
					<property name="uriParameters"><util:map></util:map></property>
					<property name="functionList"><util:list></util:list></property>
				«ENDIF»
   			 <property name="label" value=«if (transition.spec != null && transition.spec.title != null) { "\"" + transition.spec.title.name + "\"" } else { "\"" + transition.state.name + "\"" }» />
	     </bean>
           
<!--
        «stateVariableName(fromState)».addTransition(new Transition.Builder()
          		.flags(Transition.FOR_EACH)
           		.method("«transition.event.httpMethod»")
           		.target(«transitionTargetStateVariableName(transition)»)
           		.uriParameters(uriLinkageProperties)
           		.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
           		.label(«if (transition.spec != null && transition.spec.title != null) { "\"" + transition.spec.title.name + "\"" } else { "\"" + transition.state.name + "\"" }»)
           		.build());            		            	
-->
    '''
		
    def produceTransitionsAuto(ResourceInteractionModel rim, State fromState, TransitionAuto transition) '''
 	<!-- produceTransitionsAuto() -->  
	<!-- 	create AUTO transition  -->
        
       	<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
	        <property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.AUTO"/></property>
	         <property name="target" ref="«transitionTargetStateVariableName(transition)»" />
	         <!-- <property name="method" value="« transition.event.httpMethod»" /> -->

 				«IF transition.spec != null»
					<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
					<property name="functionList">«produceExpressions(rim, fromState, transition.spec.eval)»</property>
				«ENDIF»
				«IF transition.spec == null»
					<property name="uriParameters"><util:map></util:map></property>
					<property name="functionList"><util:list></util:list></property>
				«ENDIF»
 	     </bean>
        
<!--
        «stateVariableName(fromState)».addTransition(new Transition.Builder()
        		.flags(Transition.AUTO)
        		.target(«transitionTargetStateVariableName(transition)»)
        		.uriParameters(uriLinkageProperties)
        		.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
        		.build());
-->
    '''

    def produceTransitionsRedirect(ResourceInteractionModel rim, State fromState, TransitionRedirect transition) '''
  	<!-- produceTransitionsRedirect() -->   
  	<!-- 	create REDIRECT transition --> 
        
      	<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
	        <property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.REDIRECT"/></property>
	         <property name="method" value="« transition.event.httpMethod»" />
	         <property name="target" ref="«transitionTargetStateVariableName(transition)»" />

 			«IF transition.spec != null»
					<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
					<property name="functionList">«produceExpressions(rim, fromState, transition.spec.eval)»</property>
			«ENDIF»
			«IF transition.spec == null»
				<property name="uriParameters"><util:map></util:map></property>
				<property name="functionList"><util:list></util:list></property>
			«ENDIF»
	     </bean>
        
<!--
        «stateVariableName(fromState)».addTransition(new Transition.Builder()
        		.flags(Transition.REDIRECT)
        		.target(«transitionTargetStateVariableName(transition)»)
        		.uriParameters(uriLinkageProperties)
        		.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
        		.build());
-->
    '''

    def produceTransitionsEmbedded(ResourceInteractionModel rim, State fromState, TransitionEmbedded transition) '''
  	<!-- produceTransitionsEmbedded() -->  
  	<!-- 	create EMBEDDED transition --> 
       
     	<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
	        <property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.EMBEDDED"/></property>
	         <property name="method" value="« transition.event.httpMethod»" />
	         <property name="target" ref="«transitionTargetStateVariableName(transition)»" />

 			«IF transition.spec != null»
					<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
					<property name="functionList">«produceExpressions(rim, fromState, transition.spec.eval)»</property>
			«ENDIF»
			«IF transition.spec == null»
				<property name="uriParameters"><util:map></util:map></property>
				<property name="functionList"><util:list></util:list></property>
			«ENDIF»
         	 <property name="label" value=«if (transition.spec != null && transition.spec.title != null) { "\"" + transition.spec.title.name + "\"" } else { "\"" + transition.state.name + "\"" }» />
 	     </bean>
        
<!--
        «stateVariableName(fromState)».addTransition(new Transition.Builder()
        		.flags(Transition.EMBEDDED)
        		.method("«transition.event.httpMethod»")
        		.target(«transitionTargetStateVariableName(transition)»)
        		.uriParameters(uriLinkageProperties)
        		.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
        		.label(«if (transition.spec != null && transition.spec.title != null) { "\"" + transition.spec.title.name + "\"" } else { "\"" + transition.state.name + "\"" }»)
        		.build());
-->
    '''
    def addUriMapValues(EList<UriLink> uriLinks) ''' 
<util:map> 	   
	«IF uriLinks != null»  	
		«FOR prop : uriLinks»				
	    <entry key="«prop.templateProperty»" value="«prop.entityProperty.name»"/>	  
        «ENDFOR»
	«ENDIF»
</util:map>
    '''
    

    
}

