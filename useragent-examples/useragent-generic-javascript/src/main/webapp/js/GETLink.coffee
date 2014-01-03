define 'cs!GETLink', ['exports', 'jquery', 'cs!Link', 'cs!ResourceView'], (exports, $, Link, ResourceView) ->

  class GETLink extends Link.Link
  
    constructor: (currentView,  linkModel) ->
      super(currentView, linkModel, 'GET')
      if this not instanceof GETLink
        throw 'Remember to use new on constructors!'
      @successHandler = (model, textStatus, jqXHR) => new ResourceView.ResourceView(this)

    # overrides the default implementation of Link#clicked()
    clicked: () ->
      return true
      
  exports.GETLink = GETLink
  return