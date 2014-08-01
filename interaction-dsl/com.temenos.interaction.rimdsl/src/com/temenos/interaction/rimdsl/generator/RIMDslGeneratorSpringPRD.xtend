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
import com.temenos.interaction.rimdsl.rim.MethodRef

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
        // generate resource classes
        for (resourceState : rim.states) {
            val stateName = resourceState.fullyQualifiedName.toString("_")
            fsa.generateFile("IRIS-" + stateName + "-PRD.xml", toSpringXML(rim, resourceState))
        }
        val rimName = rim.fullyQualifiedName.toString("_")
        fsa.generateFile("IRIS-" + rimName + ".properties", toBeanMap(rim))
	}

	
	def className(Resource res) {
		var name = res.URI.lastSegment
		return name.substring(0, name.indexOf('.'))
	}
	
	def toBeanMap(ResourceInteractionModel rim) '''
		# «System::currentTimeMillis»
		«FOR state : rim.states»
		«stateVariableName(state)»=«produceMethods(rim, state)» «producePath(rim, state)»
		«ENDFOR»
	'''
    	
	def toSpringXML(ResourceInteractionModel rim, State state) '''
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
				http://www.springframework.org/schema/context/spring-context-3.0.xsd"
				default-lazy-init="true">

		«IF state.type.isCollection»
		<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", "createLinkRelations()", null,                     «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->   
		«ELSEIF state.type.isItem»
		<!-- super("«state.entity.name»", "«state.name»", createActions(state), "«producePath(rim, state)»", "createLinkRelations()", «if (state.path != null) { "new UriSpecification(\"" + state.name + "\", \"" + producePath(rim, state) + "\")" } else { "null" }», «if (state.errorState != null) { "factory.getResourceState(\"" + state.errorState.fullyQualifiedName + "\")" } else { "null" }»);  -->
		«ENDIF»
		<!-- Define Spring bean for resource : «state.name» -->
		<!-- begin spring bean for resource : «stateVariableName(state)» -->
		<bean id="«stateVariableName(state)»" class="«produceResourceStateType(state)»">
			<constructor-arg name="entityName" value="«state.entity.name»" />
			<constructor-arg name="name" value="« state.name »" />
			<constructor-arg>
				<list>
				«produceActionList(state, state.impl)»
				</list>
			</constructor-arg>
			<constructor-arg name="path" value="«producePath(rim, state)»" />
			<constructor-arg name="rels">
				«produceRelations(state)»
			</constructor-arg>
			<constructor-arg name="uriSpec">«IF state.path != null »<bean class="com.temenos.interaction.core.hypermedia.UriSpecification"><constructor-arg name="name" value="«state.name»" /><constructor-arg name="template" value="«producePath(rim, state)»" /></bean>«ELSE»<null />«ENDIF»</constructor-arg>
			<constructor-arg name="errorState"><null /></constructor-arg>

			«IF state.isInitial»
			<property name="initial" value="true" />
			«ENDIF»
			«IF state.isException»
			<property name="exception" value="true" />
			«ENDIF»

			<!-- Start property transitions list -->
			<property name="transitions">
				<list>
				<!-- create transitions  -->
			«FOR t : state.transitions»
				«IF (t.state != null && t.state.name != null) || (t.name != null ) »
					<!-- begin transition : «transitionTargetStateVariableName(t)» -->
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
					<!-- end transition : «transitionTargetStateVariableName(t)» -->
				«ENDIF»
			«ENDFOR»
				</list>
			</property>
		</bean>
		</beans><!-- end spring bean for resource : «stateVariableName(state)» -->
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
	
	def produceResourceStateType(State state) '''«IF state.type.isCollection»com.temenos.interaction.core.hypermedia.CollectionResourceState«ELSE»com.temenos.interaction.core.hypermedia.ResourceState«ENDIF»'''

	def produceLazyResourceStateType(State state) '''«IF state != null && state.type.isCollection»com.temenos.interaction.core.hypermedia.LazyCollectionResourceState«ELSE»com.temenos.interaction.core.hypermedia.LazyResourceState«ENDIF»'''
	
    def produceMethods(ResourceInteractionModel rim, State state) '''«
		if (state.impl.methods == null || state.impl.methods.size == 0) {
			if (state.impl != null && state.impl.view != null) {
			"GET"
			} else if (state.impl != null && state.impl.actions != null) {
			"POST"
			}
		}
		»«IF state.impl.methods != null && state.impl.methods.size > 0»«FOR method : state.impl.methods SEPARATOR ',' »«method.event.httpMethod»«ENDFOR»«ENDIF»'''
	
    def producePath(ResourceInteractionModel rim, State state) '''«
    	// prepend the basepath
	    if (rim.basepath != null) {
		    if (state.path != null) { rim.basepath.name + state.path.name } else { rim.basepath.name + "/" + state.name }
		} else {
		    if (state.path != null) { state.path.name } else { "/" + state.name }
		}
    »'''
    
    def produceRelations(State state) ''' 
	«IF state.relations != null && state.relations.size > 0»
		<array>
		«FOR relation : state.relations»
			«IF relation instanceof RelationConstant»
			<value><![CDATA[«(relation as RelationConstant).name»]]></value>
			«ELSE»
			<value><![CDATA[«(relation.relation as Relation).fqn»]]></value>
			«ENDIF»
		«ENDFOR»
		</array>
	«ELSE»
		<null />
	«ENDIF»
    '''

    def produceActionList(State state, ImplRef impl) {
    	if (impl != null) {
   			produceActionList(state, impl.view, impl.actions, impl.methods);
    	}
    }

    def produceActionList(State state, ResourceCommand view, EList<ResourceCommand> actions, EList<MethodRef> methods) '''
		«IF view != null»
			<bean class="com.temenos.interaction.core.hypermedia.Action">
				<constructor-arg value="« view.command.name »" />
				<constructor-arg value="VIEW" />
				«IF view != null && ((view.command.spec != null && view.command.spec.properties.size > 0) || view.properties.size > 0)»
				<constructor-arg>
					<props>
					«produceActionProperties(view)»
					</props>
				</constructor-arg>
				«ENDIF»
			</bean>
		«ENDIF»
		«IF actions != null»
			«FOR action : actions»
			<!--            
			Action(«action.command.name», Action.TYPE.ENTRY, actionViewProperties)
			-->
			<bean class="com.temenos.interaction.core.hypermedia.Action">
				<constructor-arg value="« action.command.name »" />
				<constructor-arg value="ENTRY" />
				«IF action != null && ((action.command.spec != null && action.command.spec.properties.size > 0) || action.properties.size > 0)»
				<constructor-arg>
					<props>
					«produceActionProperties(action)»
					</props>
				</constructor-arg>
				«ENDIF»
			</bean>
			«ENDFOR»
		«ENDIF»
		«IF methods != null»
			«FOR method : methods»
			<bean class="com.temenos.interaction.core.hypermedia.Action">
				<constructor-arg name="name" value="« method.command.command.name »" />
				«IF method.event.httpMethod.equals("GET")»
				<constructor-arg name="type" value="VIEW" />
				«ELSE»
				<constructor-arg name="type" value="ENTRY" />
				«ENDIF»
				«IF method.command != null && ((method.command.command.spec != null && method.command.command.spec.properties.size > 0) || method.command.properties.size > 0)»
				<constructor-arg name="props">
					<props>
					«produceActionProperties(method.command)»
					</props>
				</constructor-arg>
				«ELSE»
				<constructor-arg name="props"><null /></constructor-arg>
				«ENDIF»
				<constructor-arg name="method" value="« method.event.httpMethod »" />
			</bean>
			«ENDFOR»
		«ENDIF»
		'''
    
    def produceActionProperties(ResourceCommand rcommand) '''
		«IF rcommand.command.spec != null && rcommand.command.spec.properties.size > 0»
			«FOR commandProperty :rcommand.command.spec.properties»
				<prop key="«commandProperty.name»">«commandProperty.value»</prop>
			«ENDFOR»
		«ENDIF»
		«FOR commandProperty :rcommand.properties»
			<prop key="«commandProperty.name»">«commandProperty.value»</prop>
		«ENDFOR»
    '''
	def produceTransitions(ResourceInteractionModel rim, State fromState, Transition transition) '''
			<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
				<property name="method" value="«transition.event.httpMethod»" />
				<property name="target"><bean class="«produceLazyResourceStateType(transition.state)»"><constructor-arg name="name" value="«transitionTargetStateVariableName(transition)»" /></bean></property>
		«IF transition.spec != null»
				<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
				<property name="evaluation">
					«produceEvaluation(rim, fromState, transition.spec.eval)»
				</property>
		«ENDIF»
		«IF transition.spec == null»
				<property name="uriParameters"><util:map></util:map></property>
				<property name="evaluation"><null /></property>
		«ENDIF»

				<property name="label" value="«RIMDslGenerator::getTransitionLabel(transition)»" />
			</bean>

		<!--
			«stateVariableName(fromState)».addTransition(new Transition.Builder()
				.method("«transition.event.httpMethod»").target(«transitionTargetStateVariableName(transition)»).uriParameters(uriLinkageProperties).
				evaluation( conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null).
				label("«RIMDslGenerator::getTransitionLabel(transition)»")
				.build());
		-->
	'''

    def produceEvaluation(ResourceInteractionModel rim, State state, Expression conditionExpression) '''
		«IF conditionExpression != null»
		<bean class="com.temenos.interaction.core.hypermedia.expression.SimpleLogicalExpressionEvaluator">
		    <constructor-arg name="expressions">
		    <util:list>
			«FOR function : conditionExpression.expressions»
			«produceExpression( rim,  state, function)»
			«ENDFOR»
		    </util:list>
		    </constructor-arg>
		</bean>
		«ELSE»
		<null />
        «ENDIF»
	'''
    
    def expressionTargetState(Function expression) {
    	if (expression instanceof OKFunction) {
    		return (expression as OKFunction).state;
    	} else {
    		return (expression as NotFoundFunction).state;
    	}
    }
    
    def produceExpression(ResourceInteractionModel rim, State state, Function expression) '''
		<bean class="com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression">
			<constructor-arg name="target"><bean class="«produceLazyResourceStateType(expressionTargetState(expression))»"><constructor-arg name="name" value="«stateVariableName(expressionTargetState(expression))»" /></bean></constructor-arg>
		    <constructor-arg name="function">«produceFunction(expression)»</constructor-arg>
		</bean>
	'''
	
	def produceFunction(Function expression) '''
		«IF expression instanceof OKFunction»
		<util:constant static-field="com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression.Function.OK"/>
		«ELSE»
		<util:constant static-field="com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression.Function.NOT_FOUND"/>
		«ENDIF»
	'''
	
	def produceTransitionsForEach(ResourceInteractionModel rim, State fromState, TransitionForEach transition) '''
		<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
			<property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.FOR_EACH"/></property>
			<property name="method" value="« transition.event.httpMethod»" />
				<property name="target"><bean class="«produceLazyResourceStateType(transition.state)»"><constructor-arg name="name" value="«transitionTargetStateVariableName(transition)»" /></bean></property>

			«IF transition.spec != null»
			<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
			<property name="evaluation">
				«produceEvaluation(rim, fromState, transition.spec.eval)»
			</property>
			«ENDIF»
			«IF transition.spec == null»
			<property name="uriParameters"><util:map></util:map></property>
			«ENDIF»
			<property name="label" value="«RIMDslGenerator::getTransitionLabel(transition)»" />
			<property name="linkId" value="«RIMDslGenerator::getTransitionLinkId(transition)»" />
		</bean>

		<!--
			«stateVariableName(fromState)».addTransition(new Transition.Builder()
				.flags(Transition.FOR_EACH)
				.method("«transition.event.httpMethod»")
				.target(«transitionTargetStateVariableName(transition)»)
				.uriParameters(uriLinkageProperties)
				.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
				.label("«RIMDslGenerator::getTransitionLabel(transition)»")
				.linkId("«RIMDslGenerator::getTransitionLinkId(transition)»")
				.build());            		            	
		-->
	'''
		
	def produceTransitionsAuto(ResourceInteractionModel rim, State fromState, TransitionAuto transition) '''
		<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
			<property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.AUTO"/></property>
			<property name="target"><bean class="«produceLazyResourceStateType(transition.state)»"><constructor-arg name="name" value="«transitionTargetStateVariableName(transition)»" /></bean></property>
			<!-- <property name="method" value="« transition.event.httpMethod»" /> -->
			«IF transition.spec != null»
			<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
			<property name="evaluation">
				«produceEvaluation(rim, fromState, transition.spec.eval)»
			</property>
			«ENDIF»
			«IF transition.spec == null»
			<property name="uriParameters"><util:map></util:map></property>
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
		<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
			<property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.REDIRECT"/></property>
			<property name="method" value="« transition.event.httpMethod»" />
			<property name="target"><bean class="«produceLazyResourceStateType(transition.state)»"><constructor-arg name="name" value="«transitionTargetStateVariableName(transition)»" /></bean></property>
			«IF transition.spec != null»
			<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
			<property name="evaluation">
				«produceEvaluation(rim, fromState, transition.spec.eval)»
			</property>
			«ENDIF»
			«IF transition.spec == null»
			<property name="uriParameters"><util:map></util:map></property>
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
		<bean class="com.temenos.interaction.springdsl.TransitionFactoryBean">
			<property name="flags"><util:constant static-field="com.temenos.interaction.core.hypermedia.Transition.EMBEDDED"/></property>
			<property name="method" value="« transition.event.httpMethod»" />
			<property name="target"><bean class="«produceLazyResourceStateType(transition.state)»"><constructor-arg name="name" value="«transitionTargetStateVariableName(transition)»" /></bean></property>
			«IF transition.spec != null»
			<property name="uriParameters">«addUriMapValues(transition.spec.uriLinks)»</property>
			<property name="evaluation">
				«produceEvaluation(rim, fromState, transition.spec.eval)»
			</property>
			«ENDIF»
			«IF transition.spec == null»
			<property name="uriParameters"><util:map></util:map></property>
			«ENDIF»
			<property name="label" value="«RIMDslGenerator::getTransitionLabel(transition)»" />
		</bean>
		<!--
			«stateVariableName(fromState)».addTransition(new Transition.Builder()
				.flags(Transition.EMBEDDED)
				.method("«transition.event.httpMethod»")
				.target(«transitionTargetStateVariableName(transition)»)
				.uriParameters(uriLinkageProperties)
				.evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)
				.label("«RIMDslGenerator::getTransitionLabel(transition)»")
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

