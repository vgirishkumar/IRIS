#
# The CRUDLinkFactory understand a few link relations related to CRUD operations
# and will create the appropriate Links
#
define 'cs!CRUDLinkFactory', ['exports', 'cs!GETLink'], (exports, GETLink) ->

  class CRUDLinkFactory
    constructor: () ->
      if this not instanceof CRUDLinkFactory
        throw 'Remember to use new on constructors!'
      
    createLink: (currentView, linkModel) ->
      if linkModel == null
        throw "Precondition failed:  No model"
      if linkModel.rel == null
        throw "Precondition failed:  No model.rel"
  	  
      switch linkModel.rel
        when 'edit'
        else
          return new GETLink.GETLink(currentView, linkModel)

  exports.CRUDLinkFactory = CRUDLinkFactory
  return