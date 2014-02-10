define 'cs!views', ['exports', 'jquery', 'underscore', 'cs!actions'], (exports, $, _, actions) ->
  
#
# View is an abstract class that helps render a region of the screen
#
  class View
    constructor: (@target) ->
      if this not instanceof View
        throw 'Remember to use new on constructors!'
      @factory = new actions.ActionFactory()

    clear: (selector = @target) => $(selector).empty()

    append: (item) =>
      $(item).appendTo @target
      item

    isEntryPoint: => @target is '#entry-point-wrapper'

    hasRows: (model) => model._embedded?

    hasLinks: (model) => model._links?

    createLink: (resource, model) ->
      result = null
      if model instanceof actions.Action
      	result = model
      else
        result = @factory.createActions(resource, model)
      return result

    #
    #
    # HTML element factory methods
    #
    #
    createDiv: (clazz = "") -> $("<div class='#{clazz}'></div>")

    createSpan: (clazz = "") -> $("<span class='#{clazz}'></span>")

    createOl: (clazz = "") -> $("<ol class='#{clazz}'></ol>")

    createLi: (clazz = "") -> $("<li class='#{clazz}'></li>")

    createParagraph:  -> $("<p></p>")

    createLabel: (text) -> $("<label>#{text}</label>")

    createValue: (value) -> $("<span>#{value}</span>")

    createInput: (id, value="", type="text") => $("<input id='#{id}' type='#{type}' value='#{value}'/>")

    createHyperLink: (text) -> $("<a href='javascript: return false'>#{text}</a>")

    createButton: (value, link = null) ->
      btn = $("<input type='button' value='#{value}'/>")
#      if link? then btn.click -> link.trigger()
#      if link? then btn.click -> link.triggerXML()
      if link? then btn.click -> link.triggerJSON()
      btn

    createFormForModel: (formModel, buttons...) ->
      form = $('<form class="border"></form>')
      form.append @renderPropertyList formModel, 'resource-property-list', true
      form.link(formModel);
      if buttons?
        p = @createParagraph()
        p.appendTo form
        _.each buttons, (btn) -> p.append(btn)
      form

    createLabelAndValue: (label, value, editable=false) ->
      p = @createParagraph()
      p.append @createLabel label
      if editable
        p.append @createInput label, value
      else
        p.append @createValue value
      p



#
# The ResourceView class is responsible for rendering the resource information
# to the screen for a person to view.
#
  class ResourceView extends View
  
    constructor: (selfLink, target = '#resource-wrapper') ->
      super target
      if this not instanceof ResourceView
        throw 'Remember to use new on constructors!'
      @selfLink = selfLink
#      @selfLink.successHandler = @render
      # fetch and display this resource view
 #     @selfLink.trigger()

    refresh: =>
      # creates a RefreshAction
      @factory.createActions(this, @selfLink).trigger();
  
    render: (@model, textStatus, jqXHR) =>
      @clear()
      @append @renderHeader jqXHR
      @append @renderLinks @model, false, true, 'horizontal-nav'
      @append @renderResource @model if not @isEntryPoint()
  
  
    renderHeader: ( jqXHR)  =>
      div = @createDiv('header')
  
      if @isEntryPoint()
        div.append @createSpan('title').text 'Entry point'
      else
        title = if @hasRows @model then 'Collection info' else 'Resource info'
        props = {
          'url': @selfLink.model.href,
          'method': @selfLink.model.method,
          'content-type': jqXHR.getResponseHeader('Content-Type'),
          'status': jqXHR.status
        }
        div.append @createSpan('title').text title
        div.append @renderPropertyList(props, 'property-list')
      div
  
    renderResource: (model, isListItem = false) =>
      div = @createDiv 'resource-data'
      div.addClass('border') if !isListItem
      if @hasRows model
        ol = @createOl 'collection-rows'
        ol.appendTo div
        _.each model._embedded, (embeddedResource) =>
          _.each embeddedResource, (item) =>
            ol.append @createLi().append @renderResource(item, true)
      else
        re = @createDiv 'resource'
        re.append @renderPropertyList(model, 'resource-property-list')
        re.append @createDiv('resource-links').append @renderLinks(model, isListItem, false, 'vertical-nav')
        re.appendTo div
      div
  
    renderLinks : (model, isListItem, isNav, clazz) =>
      div = @createDiv clazz
      ol = @createOl('nav-bar').appendTo div
  
      if model? and model._links? and model._links
        for rel, relModel of model._links
          if (relModel.length)
            _.each relModel, (linkModel) =>
              @renderLink(model, ol, isListItem, isNav, linkModel)
          else
            @renderLink(model, ol, isListItem, isNav, relModel)
  
      # render the Json link
      if isNav && not @isEntryPoint()
        li = @createLi('nav-bar-item right').append @renderResourceAsJsonLink()
        li.appendTo ol
  
      div
      
    renderLink : (model, ol, isListItem, isNav, linkModel) =>
      console.log("rel: " + linkModel.name + ", href: " + linkModel.href + ", method: " + linkModel.method)
      if @isEntryPoint()
        ol.append @createLi('nav-bar-item').append @createLink(this, linkModel).hyperLink
      else if @hasRows model
        ol.append @createLi('nav-bar-item').append @createLink(this, linkModel).hyperLink
      else if isListItem
        ol.append @createLi('nav-bar-item').append @createLink(this, linkModel).hyperLink
      else
        if isNav
          if linkModel.name is 'self' then ol.append @createLi('nav-bar-item').append @createLink(this, linkModel).hyperLink
        else
          if linkModel.name isnt 'self' then ol.append @createLi('nav-bar-item').append @createLink(this, linkModel).hyperLink
  
    renderPropertyList: (model, clazz="", editable=false) =>
      div = @createDiv clazz
      for name of model when name isnt '_links' && name isnt '_embedded'
        div.append @createLabelAndValue(name,  model[name], editable)
      div
  
    renderFormHeader: (link) =>
      div = @createDiv('header')
      title = if link.model.method is 'POST' then 'Create resource' else 'Update resource'
      props = {
        'url': link.model.href,
        'method': link.model.method,
        'content-type': link.model.consumes[0]
      }
      div.append @createSpan('title').text title
      div.append @renderPropertyList(props, 'property-list')
      div
  
    renderForm: (formActionLink) =>
      @clear()
      div = @createDiv ""
      div.append @renderFormHeader(formActionLink)
      div.append @createFormForModel(formActionLink.formModel,
                          @createButton('Save', formActionLink),
                          @createButton('Cancel', @selfLink))
      @append div
  
    renderResourceAsJsonLink: =>
      a = @createHyperLink('json').addClass('json-link').click =>
        if $('.json-link').text() is 'json'
          $('.json-link').text('model')
          @clear('.resource-data').append('<pre class="json-view">' + JSON.stringify(@model, null, 3) + '</pre>')
        else
          @refresh()
        false;
      a



  exports.ResourceView = ResourceView
  exports.View = View
  return