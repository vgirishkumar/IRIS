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
import com.temenos.interaction.rimdsl.rim.UriLinkageEntityKeyReplace
import com.temenos.interaction.rimdsl.rim.OKFunction;
import com.temenos.interaction.rimdsl.rim.NotFoundFunction
import com.temenos.interaction.rimdsl.rim.Function
import com.temenos.interaction.rimdsl.rim.Expression

class RIMDslGenerator implements IGenerator {
	
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
        val rim = resource.contents.head as ResourceInteractionModel;
        // generate Behaviour class
		fsa.generateFile(resource.className + "Model" + "/" + resource.className+"Behaviour.java", toJavaCode(rim))
        // generate resource classes
        for (resourceState : rim.states) {
            fsa.generateFile(resource.className + "Model" + "/" + resourceState.name+"ResourceState.java", toJavaCode(rim, resourceState))
        }
	}
		
	def className(Resource res) {
		var name = res.URI.lastSegment
		return name.substring(0, name.indexOf('.'))
	}
	
	def toJavaCode(ResourceInteractionModel rim, State state) '''
        package «rim.eResource.className»Model;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.Properties;

        import com.temenos.interaction.core.hypermedia.UriSpecification;
        import com.temenos.interaction.core.hypermedia.Action;
        import com.temenos.interaction.core.hypermedia.CollectionResourceState;
        import com.temenos.interaction.core.hypermedia.LazyResourceLoader;
        import com.temenos.interaction.core.hypermedia.ResourceFactory;
        import com.temenos.interaction.core.hypermedia.ResourceState;
        import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
        import com.temenos.interaction.core.hypermedia.validation.HypermediaValidator;
        import com.temenos.interaction.core.hypermedia.expression.Expression;
        import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;
        
        public class «state.name»ResourceState extends «IF state.entity.isCollection»Collection«ENDIF»ResourceState implements LazyResourceLoader {
            
            private ResourceFactory factory = null;

            public «state.name»ResourceState() {
                this(new ResourceFactory());
            }

            public «state.name»ResourceState(ResourceFactory factory) {
                «IF state.entity.isCollection»
                super("«state.entity.name»", "«state.name»", createActions(), "«if (state.path != null) { state.path.name } else { "/" + state.name }»", createLinkRelations(), null, «if (state.errorState != null) { "factory.getResourceState(\"" + rim.eResource.className + "Model." + state.errorState.name + "\")" } else { "null" }»);
                «ELSEIF state.entity.isItem»
                super("«state.entity.name»", "«state.name»", createActions(), "«if (state.path != null) { state.path.name } else { "/" + state.name }»", createLinkRelations(), «if (state.path != null) { "new UriSpecification(\"" + state.name + "\", \"" + state.path.name + "\")" } else { "null" }», «if (state.errorState != null) { "factory.getResourceState(\"" + rim.eResource.className + "Model." + state.errorState.name + "\")" } else { "null" }»);
                «ENDIF»
                this.factory = factory;
            }
            
            public boolean initialise() {
                Map<String, String> uriLinkageEntityProperties = new HashMap<String, String>();
                Map<String, String> uriLinkageProperties = new HashMap<String, String>();
                List<Expression> conditionalLinkExpressions = null;
                «IF state.entity.isCollection»Collection«ENDIF»ResourceState s«state.name» = this;
                «
                val resources = newArrayList()
                »
                «IF !resources.contains(state.name) && resources.add(state.name)»«ENDIF»
                // create regular transitions
                «FOR t : state.transitions»
                    «IF !resources.contains(t.state.name) && resources.add(t.state.name)»
                    ResourceState s«t.state.name» = factory.getResourceState("«rim.eResource.className»Model.«t.state.name»");
                    «ENDIF»
                    «produceTransitions(state, t)»
                «ENDFOR»

                // create foreach transitions
                «FOR t : state.transitionsForEach»
                    «IF !resources.contains(t.state.name) && resources.add(t.state.name)»
                    ResourceState s«t.state.name» = factory.getResourceState("«rim.eResource.className»Model.«t.state.name»");
                    «ENDIF»
                    «produceTransitionsForEach(state, t)»
                «ENDFOR»

                // create AUTO transitions
                «FOR t : state.transitionsAuto»
                    «IF !resources.contains(t.state.name) && resources.add(t.state.name)»
                    ResourceState s«t.state.name» = factory.getResourceState("«rim.eResource.className»Model.«t.state.name»");
                    «ENDIF»
                    «produceTransitionsAuto(state, t)»
                «ENDFOR»
                return true;
            }
            
            private static List<Action> createActions() {
                Properties actionViewProperties = null;
                «produceActionSet(state, state.view, state.actions)»
                return «state.name»Actions;
            }

            private static String[] createLinkRelations() {
                «produceRelations(state)»
                return «state.name»Relations;
            }
            
        }
	'''
	
	def toJavaCode(ResourceInteractionModel rim) '''
		package «rim.eResource.className»Model;

		import java.util.ArrayList;
		import java.util.HashMap;
		import java.util.List;
		import java.util.Map;
		import java.util.Properties;

		import com.temenos.interaction.core.hypermedia.UriSpecification;
		import com.temenos.interaction.core.hypermedia.Action;
		import com.temenos.interaction.core.hypermedia.CollectionResourceState;
		import com.temenos.interaction.core.hypermedia.ResourceFactory;
		import com.temenos.interaction.core.hypermedia.ResourceState;
		import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
		import com.temenos.interaction.core.hypermedia.validation.HypermediaValidator;
		import com.temenos.interaction.core.hypermedia.expression.Expression;
		import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;
		import com.temenos.interaction.core.resource.ResourceMetadataManager;
		
		public class «rim.eResource.className»Behaviour {
		
		    public static void main(String[] args) {
		        «rim.eResource.className»Behaviour behaviour = new «rim.eResource.className»Behaviour();
		        ResourceStateMachine hypermediaEngine = new ResourceStateMachine(behaviour.getRIM(), behaviour.getExceptionResource());
		        HypermediaValidator validator = HypermediaValidator.createValidator(hypermediaEngine, new ResourceMetadataManager(hypermediaEngine).getMetadata());
		        System.out.println(validator.graph());
		    }
		
		    public ResourceState getRIM() {
		        Map<String, String> uriLinkageEntityProperties = new HashMap<String, String>();
		        Map<String, String> uriLinkageProperties = new HashMap<String, String>();
		        List<Expression> conditionalLinkExpressions = null;
		        Properties actionViewProperties;

		        ResourceFactory factory = new ResourceFactory();
		        ResourceState initial = null;
		        // create states
		        «FOR c : rim.states»
		        	«IF c.isInitial»
		        	// identify the initial state
		        	initial = factory.getResourceState("«rim.eResource.className»Model.«c.name»");
					«ENDIF»
				«ENDFOR»
		        return initial;
		    }

		    public ResourceState getExceptionResource() {
		        ResourceFactory factory = new ResourceFactory();
		        ResourceState exceptionState = null;
		        «FOR c : rim.states»
		        	«IF c.isException»
		        	exceptionState = factory.getResourceState("«rim.eResource.className»Model.«c.name»");
					«ENDIF»
				«ENDFOR»
		        return exceptionState;
		    }
		}
	'''
	
	def produceResourceStates(State state) '''
            «produceActionSet(state, state.view, state.actions)»
            «produceRelations(state)»
            «IF state.entity.isCollection»
            CollectionResourceState s«state.name» = new CollectionResourceState("«state.entity.name»", "«state.name»", «state.name»Actions, "«if (state.path != null) { state.path.name } else { "/" + state.name }»", «state.name»Relations, null);
            «ELSEIF state.entity.isItem»
            ResourceState s«state.name» = new ResourceState("«state.entity.name»", "«state.name»", «state.name»Actions, "«if (state.path != null) { state.path.name } else { "/" + state.name }»", «state.name»Relations«if (state.path != null) { ", new UriSpecification(\"" + state.name + "\", \"" + state.path.name + "\")" }»);
            «ENDIF»
            «IF state.isException»
            s«state.name».setException(true);
            «ENDIF»
	'''

    def produceRelations(State state) '''
        «IF state.relations != null && state.relations.size > 0»
        String «state.name»RelationsStr = "";
        «FOR relation : state.relations»
        «state.name»RelationsStr += "«relation.name» ";
        «ENDFOR»
        String[] «state.name»Relations = «state.name»RelationsStr.trim().split(" ");
        «ELSE»
        String[] «state.name»Relations = null;
        «ENDIF»
    '''

    def produceActionSet(State state, ResourceCommand view, EList<ResourceCommand> actions) '''
        List<Action> «state.name»Actions = new ArrayList<Action>();
        «IF view != null && (view.command.properties.size > 0 || view.parameters.size > 0)»
            actionViewProperties = new Properties();
            «FOR commandProperty :view.command.properties»
            actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
            «ENDFOR»
            «FOR commandProperty :view.parameters»
            actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
            «ENDFOR»
        «ENDIF»
        «IF view != null»
        «state.name»Actions.add(new Action("«view.command.name»", Action.TYPE.VIEW, «if (view != null && (view.command.properties.size > 0 || view.parameters.size > 0)) { "actionViewProperties" } else { "new Properties()" }»));
        «ENDIF»
        «IF actions != null»
            «FOR action : actions»
            actionViewProperties = new Properties();
            «IF action != null && (action.command.properties.size > 0 || action.parameters.size > 0)»
                «FOR commandProperty :action.command.properties»
                actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
                «ENDFOR»
                «FOR commandProperty :action.parameters»
                actionViewProperties.put("«commandProperty.name»", "«commandProperty.value»");
                «ENDFOR»
            «ENDIF»
            «state.name»Actions.add(new Action("«action.command.name»", Action.TYPE.ENTRY, actionViewProperties));
            «ENDFOR»
        «ENDIF»'''
    
	def produceTransitions(State fromState, Transition transition) '''
            «produceUriLinkage(transition.uriLinks)»
            «IF transition.eval != null»
            «produceExpressions(transition.eval)»
            «ELSE»
            conditionalLinkExpressions = null;
            «ENDIF»
            s«fromState.name».addTransition("«transition.event.httpMethod»", s«transition.state.name», uriLinkageEntityProperties, uriLinkageProperties, 0, conditionalLinkExpressions, «if (transition.title != null) { "\"" + transition.title.name + "\"" } else { "\"" + transition.state.name + "\"" }»);
	'''

    def produceExpressions(Expression conditionExpression) '''
        conditionalLinkExpressions = new ArrayList<Expression>();
        «FOR function : conditionExpression.expressions»
            conditionalLinkExpressions.add(«produceExpression(function)»);
        «ENDFOR»
    '''

    def produceExpression(Function expression) '''
        «IF expression instanceof OKFunction»
            new ResourceGETExpression("«(expression as OKFunction).state.name»", ResourceGETExpression.Function.OK)«
        ELSE»
            new ResourceGETExpression("«(expression as NotFoundFunction).state.name»", ResourceGETExpression.Function.NOT_FOUND)«
        ENDIF»'''

    def produceTransitionsForEach(State fromState, TransitionForEach transition) '''
            «produceUriLinkage(transition.uriLinks)»
            «IF transition.eval != null»
            «produceExpressions(transition.eval)»
            «ELSE»
            conditionalLinkExpressions = null;
            «ENDIF»
            s«fromState.name».addTransitionForEachItem("«transition.event.httpMethod»", s«transition.state.name», uriLinkageEntityProperties, uriLinkageProperties, conditionalLinkExpressions, «if (transition.title != null) { "\"" + transition.title.name + "\"" } else { "\"" + transition.state.name + "\"" }»);
    '''
		
    def produceTransitionsAuto(State fromState, TransitionAuto transition) '''
            «produceUriLinkage(transition.uriLinks)»
            «IF transition.eval != null»
            «produceExpressions(transition.eval)»
            s«fromState.name».addTransition(s«transition.state.name», uriLinkageEntityProperties, uriLinkageProperties, conditionalLinkExpressions);
            «ELSE»
            s«fromState.name».addTransition(s«transition.state.name», uriLinkageEntityProperties, uriLinkageProperties);
            «ENDIF»
    '''

    def produceUriLinkage(EList<UriLink> uriLinks) '''
        «IF uriLinks != null»
            uriLinkageEntityProperties.clear();
            uriLinkageProperties.clear();
            «FOR prop : uriLinks»
            «IF prop.entityProperty instanceof UriLinkageEntityKeyReplace»
            uriLinkageEntityProperties.put("«prop.templateProperty»", "«prop.entityProperty.name»");
            «ELSE»
            uriLinkageProperties.put("«prop.templateProperty»", "«prop.entityProperty.name»");
            «ENDIF»
            «ENDFOR»«
        ENDIF»
    '''

}

