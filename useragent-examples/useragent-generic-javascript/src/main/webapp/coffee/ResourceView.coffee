class @ResourceView extends View

  constructor: (selfLink, target = '#resource-wrapper') ->
    super target
    @selfLink = @createLink(this, selfLink)
    $.ajax {
        headers: { 
            Accept : "application/hal+json; charset=utf-8"
        }
        url: @selfLink.model.href, 
        error: (jqXHR, textStatus, errorThrown) ->
            console.log("HTTP GET #{this.url} Error: #{jqXHR.status} (#{errorThrown})")
            alert("HTTP Error: #{jqXHR.status} #{this.url} (#{errorThrown})")
        success: @render
    }
    
  refresh: =>
    @selfLink.trigger()

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

    _.each model._links, (relModel) =>
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
    if (!linkModel.method?)
      if (linkModel.href.substring(0,4) == "http")
        linkModel.method = "GET"
      else
        elements = linkModel.href.split(" ")
        linkModel.method = elements[0]
        linkModel.href = elements[1]

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

# Render a form with the response data obtained from a POST command 
  renderPOSTResponseForm: (model) =>
    _.each model._links, (relModel) =>
      debugger
      if (relModel.length)
        for item in relModel
          debugger
          if (item.href.substring(0,3) == "PUT")
            @commitLink = @getLink(this, item, model.body)
      else
        @commitLink = @getLink(this, relModel, model.body)
    #alert("Link = " + @commitLink.model.href)
    @clear()
    div = @createDiv ""
    div.append @createFormForModel(model,
                        @createButton('Create', @commitLink),
                        @createButton('Cancel', @selfLink))
    @append div    
    

    