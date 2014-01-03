#
#
# Application bootstrap
#
#
define ['jquery', 'cs!views'], ($, views) ->

  $ ->
    $('#hateoas').append '<div id="entry-point-wrapper"></div><div id="resource-wrapper"></div>'

    new views.ResourceView({rel: 'self', href: 'api/', method: 'GET'}, '#entry-point-wrapper')
    
#  return