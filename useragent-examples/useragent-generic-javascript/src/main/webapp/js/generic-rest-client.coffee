#
#
# Application bootstrap
#
#
define ['jquery', 'cs!ResourceView'], ($, ResourceView) ->

  $ ->
    $('#hateoas').append '<div id="entry-point-wrapper"></div><div id="resource-wrapper"></div>'

    new ResourceView.ResourceView({rel: 'self', href: 'api/', method: 'GET'}, '#entry-point-wrapper')
    
#  return