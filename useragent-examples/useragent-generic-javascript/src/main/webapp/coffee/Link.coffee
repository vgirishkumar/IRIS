class @Link

  constructor: (@resource,  @model) ->

    @formModel = {}
    @hyperLink = $('<a></a>').attr('href', @model.href).text(@model.name + '-' + @model.method)
    @errorHandler = (XMLHttpRequest, textStatus, errorThrown) => alert "Error! Status = #{XMLHttpRequest.status}"

    switch @model.method
      when 'GET'
        @hyperLink.click => @doGet()
        @successHandler = (model, textStatus, jqXHR) => new ResourceView(this)
      when 'DELETE'
        @hyperLink.click => @doDelete()
        @successHandler = (model, textStatus, jqXHR) => 
            if jqXHR.status is (205 or 404)
                new ResourceView({rel: 'self', href: this.resource.selfLink.model.href, method: 'GET'})
            else
                new ResourceView({rel: 'self', href: jqXHR.getResponseHeader('Location'), method: 'GET'})
      when 'PUT'
        @hyperLink.click => @doPut()
        @formModel = @cloneModel @resource.model
        @successHandler = (model, textStatus, jqXHR) => new ResourceView(@resource.selfLink)
#      when 'POST'
#        @hyperLink.click => @doPost()
#        @formModel = @model.template
#        @successHandler = (model, textStatus, jqXHR) => new ResourceView({rel: 'self', href: jqXHR.getResponseHeader('Location'), method: 'GET'})
      when 'POST'
        @hyperLink.click => @doPost()
        @successHandler = (model, textStatus, jqXHR) => @handlePOSTResponse(model)

  cloneModel: (model)->
    clone = {}
    $.extend true, clone, model
    if clone.links? then delete clone.links
    clone

  trigger: =>
    $.ajax {
      headers: { 
          Accept : "application/hal+json; charset=utf-8"
          "Content-Type" : "application/hal+json; charset=utf-8"
      }
      url: @model.href,
      data: JSON.stringify(@formModel),
      type: @model.method,
      contentType: @model.consumes,
      success: @successHandler,
      error: @errorHandler
    }

  doGet: =>
    @trigger()
    false

  doPut: =>
    @resource.renderForm(this)
    false

#  doPost: =>
#    @resource.renderForm(this)
#    false

  doDelete: =>
    if confirm("R U sure?") then @trigger()
    false

  doPost: =>
    $.ajax {
      headers: { 
          Accept : "application/hal+json; charset=utf-8"
      }
      url: @model.href,
      data: "{ 'body' : 'FT0001', 'id' : 1}",
      type: "POST",
      contentType: "application/hal+json; charset=utf-8",
      success: @successHandler
      error: @errorHandler
    }
    false

  handlePOSTResponse: (model)->
    @resource.renderPOSTResponseForm(model)
    false

  triggerXML: =>
    $.ajax {
      headers: { 
          Accept : "application/hal+xml; charset=utf-8"
          "Content-Type" : "application/hal+xml; charset=utf-8"
      }
      url: @model.href,
      data: @model.body,
      type: @model.method,
      contentType: @model.consumes,
      success: @successHandler,
      error: @errorHandler
    }

  triggerJSON: =>
    $.ajax {
      headers: { 
          Accept : "application/hal+json; charset=utf-8"
          "Content-Type" : "application/hal+json; charset=utf-8"
      }
      url: @model.href,
      data: @model.body,
      type: @model.method,
      contentType: @model.consumes,
      success: @successHandler,
      error: @errorHandler
    }
