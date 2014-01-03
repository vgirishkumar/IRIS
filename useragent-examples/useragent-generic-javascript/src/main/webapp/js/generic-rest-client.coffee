#
#
# Application bootstrap
#
#
define ['jquery', 'cs!views', 'cs!actions'], ($, views, actions) ->

  $ ->
    $('#hateoas').append '<div id="entry-point-wrapper"></div><div id="resource-wrapper"></div>'

    factory = new actions.ActionFactory()
    action = factory.createActions(null, {rel: 'self', href: 'api/', method: 'GET'})
    view = new views.ResourceView(action, '#entry-point-wrapper')
    view.refresh()
    
#  return