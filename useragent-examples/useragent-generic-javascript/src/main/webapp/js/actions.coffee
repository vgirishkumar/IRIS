define 'cs!actions', ['exports', 'cs!views'], (exports, views) ->

#
# This ActionFactory understands a few link relations related to CRUD operations
# and will create the appropriate actions for the supplied link model, using 'rel'
# to work out what to do
#
  class ActionFactory
    constructor: () ->
      if this not instanceof ActionFactory
        throw 'Remember to use new on constructors!'
      
    createActions: (currentView, linkModel) ->
      if linkModel instanceof Action
      	return new RefreshAction(currentView, linkModel.model)
      if linkModel == null
        throw "Precondition failed:  No model"
      if linkModel.rel == null
        throw "Precondition failed:  No model.rel"
  	  
      switch linkModel.rel
        when 'edit'
        else
          return new ViewAction(currentView, linkModel)



#
# A Action object is an abstract class to help construct the appropriate HTML to 
# handle different types of links supplied by the server.  It uses the 'href' from 
# the model as the target; a call to getHyperLink builds the HTML anchor for use
# in a View.
#
  class Action

    constructor: (@currentView,  @model, method) ->
      if this not instanceof Action
        throw 'Remember to use new on constructors!'
      if @model == null
        throw "Precondition failed:  No model"
      if @model.href == null or not @model.href?
        throw "Precondition failed:  No model.href"
      @model.method = method
      if @model.method == null or not @model.method?
        throw "Precondition failed:  No model.method"
      @errorHandler = (XMLHttpRequest, textStatus, errorThrown) =>
        console.log("HTTP GET #{this.url} Error: #{jqXHR.status} (#{errorThrown})")
        alert("HTTP Error: #{jqXHR.status} #{this.url} (#{errorThrown})")
      @hyperLink = $('<a></a>').attr('href', @model.href).text(@model.method + '-' + (if @model.name? then @model.name else @model.href))
      @hyperLink.click => @click()

    cloneModel: (model)->
      clone = {}
      $.extend true, clone, model
      if clone.links? then delete clone.links
      clone

    # subclasses must implement
    clicked: ->
      throw "Not implemented"
      
    click: =>
      if @clicked? and @clicked()
        @trigger()
      return false

    trigger: ->
      if @successHandler == null or not @successHandler?
        throw "'successHandler' must be defined before calling trigger"

      $.ajax {
        headers: { 
          Accept : "application/hal+json; charset=utf-8"
          "Content-Type" : "application/hal+json; charset=utf-8"
          "Link" : "<" + @model.href + "> ;rel=" + @model.name 
        }
        url: @model.href,
        data: JSON.stringify(@formModel),
        type: @model.method,
        contentType: @model.consumes,
        success: @successHandler,
        error: @errorHandler
      }



#
# A ViewAction is constructed with a link to a resource to view.
#
  class ViewAction extends Action
  
    constructor: (currentView,  linkModel) ->
      super(currentView, linkModel, 'GET')
      if this not instanceof ViewAction
        throw 'Remember to use new on constructors!'
      @successHandler = (model, textStatus, jqXHR) => new views.ResourceView(this).render(model, textStatus, jqXHR)

    # overrides the default implementation of Link#clicked()
    clicked: () ->
      return true


#
# A RefreshAction is constructed with a link to a 'self' resource to view; successHandler is the current view.
#
  class RefreshAction extends Action
  
    constructor: (currentView,  linkModel) ->
      super(currentView, linkModel, 'GET')
      if this not instanceof RefreshAction
        throw 'Remember to use new on constructors!'
      @successHandler = (model, textStatus, jqXHR) => currentView.render(model, textStatus, jqXHR)

    # overrides the default implementation of Link#clicked()
    clicked: () ->
      return true



  exports.ActionFactory = ActionFactory
  exports.Action = Action
  exports.ViewAction = ViewAction
  exports.RefreshAction = RefreshAction
  return