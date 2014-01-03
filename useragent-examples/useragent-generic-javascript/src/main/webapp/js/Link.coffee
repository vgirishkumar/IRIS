#
# A Link object constructs the appropriate HTML to handle different types of
# links supplied by the server.  It uses the 'href' from the model as the target 
# and the 'rel' to work out what to do.  A call to getHyperLink builds the HTML
# anchor.
#
define 'cs!Link', ['exports', 'jquery'], (exports, $) ->

  class Link

    constructor: (@currentView,  @model, method) ->
      if this not instanceof Link
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

  exports.Link = Link
  return