$(document).ready(function() {

  // all the functionality is wrapped in the object returned by this helper function
  var helper = function() {

    // global reference data (constants)
    var g = {
      // XML namespaces referred to in the code
      ns: {
        app:  "http://www.w3.org/2007/app",
        atom: "http://www.w3.org/2005/Atom",
        m:    "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata",
        d:    "http://schemas.microsoft.com/ado/2007/08/dataservices"
      },

      // Link relation constants.
      rels: {
        base:           "http://temenostech.temenos.com/rels/",

        contextEnquiry: "http://temenostech.temenos.com/rels/contextenquiry",
        edit:           "http://temenostech.temenos.com/rels/edit",
        input:          "http://temenostech.temenos.com/rels/input",
        new:            "http://temenostech.temenos.com/rels/new",
        metadata:       "http://temenostech.temenos.com/rels/metadata",
        delete:         "http://temenostech.temenos.com/rels/delete"
      }
    };

    // array of URLs fetched 
    var history;






    /* Binder functions
     * There is a single global map of bindings from table row elements to
     * model properties.
     * resetBindings() clears it
     * bind( domProperty, domElement, modelProperty ) creates a binding for
     *   table row "domProperty", binding the value of "domElement" in the view
     *   to the value of "modelProperty" in the model
     * updateBinding( domProperty ) applies a binding, updating the model
     *   to match the view
     */

    // map of bindings of view elements to model elements
    var bindings;

    var Binding = function( domProperty, domElement, modelProperty ) {
      this.boundView = domElement;
      this.boundModel = modelProperty;
      this.containingView = domProperty;
    };

    Binding.prototype.viewValue = function() {
      var el = this.boundView;
      return (el.tagName == "INPUT") ? el.value : el.textContent;
    };

    var resetBindings = function() {
      // bind from view to model
      bindings = { v2m: {} };
    };

    // bind an element within a property entry to a property in the model
    var bind = function( domProperty, domElement, modelProperty ) {
      var binding = new Binding( domProperty, domElement, modelProperty );
      bindings.v2m[ domProperty.getAttribute("id") ] = binding;
      modelProperty.binding( binding );
    };

    var updateBinding = function( domProperty ) {
      var binding = bindings.v2m[ domProperty.getAttribute("id") ], value;
      if ( binding.boundModel.isLeaf() ) {
        value = binding.viewValue();
        if ( binding.boundModel.value() != value ) {
          binding.boundModel.value( value );
        }
      }
    };

    var getBindingForModel = function( modelProperty ) {
      return modelProperty.binding();
    };

 




    /* Metadata functions:
     * The metadata is loaded, but nothing looks at it so far.
     */

    // odata metadata is saved on the document object for later reference
    var saveMetadata = function( data ) {
      document.irisMetadata = data;
    };

    // fetch the odata metadata -- it is not rendered
    var fetchMetadata = function() {
      var service = $("#service")[0].value,
      metadataUrl = service + "$metadata";

      $.ajax({
	url: metadataUrl,
	type: "GET",
        dataType: "xml",
        success: saveMetadata
      });
    }






   /* Action Handling:
    * doAction()       is the main function to perform an action.
    * actionCallback() returns a function that will perform 
    *                  the appropriate action when called.
    */

    // insert a new action into the history list
    var insertHistory = function( action ) {
      history.splice( historyPtr++, 0, action.url );
    };

    // make a GET request
    var getAction = function( action ) {
      var req = {
        type: "GET",
        url: action.url,
        dataType: "xml",
        success: showDoc
      };
      $.ajax( req );
    };

    // make a POST request
    var postEntryAction = function( action ) {
      var req = {
        type: "POST",
        url: action.url,
        dataType: "xml",
        contentType: "application/atom+xml;type=entry",
        data: "",
        success: showDoc
      };
      if ( action["entry"] ) {
        if ( action.entry["eTag"] ) {
          req.headers = { "If-Match": action.entry.eTag };
        }
        req.data = entryToXml( updateModelFromView( action.entry ) );
      }

      $.ajax( req );
    };

    // make a DELETE request
    var deleteAction = function( action ) {
      var req = {
        type: "DELETE",
        url: action.url,
        dataType: "xml",
        contentType: "application/atom+xml;type=entry",
        success: showDoc
      };
      if ( action.entry.eTag ) {
        req.headers = { "If-Match": action.entry.eTag };
      }
      $.ajax( req );
    };

    // make an http request
    var doAction = function( action ) {
      insertHistory( action );
      switch( action.rel ) {
      case "self":
      case g.rels.edit:
        getAction( action );
        break;
      case g.rels.metadata:
      case g.rels.input:
      case g.rels.new:
        postEntryAction( action );
        break;
      case g.rels.delete:
        deleteAction( action );
        break;
      default:
        getAction( action );
      }
    };

    // return a function that can be bound to an event to make an action call
    // on e.g. a button push
    var actionCallback = function( link ) {
      return function() { doAction( link ); };
    };






    /* Model functions:
     * entryToXML() serializes an entry model to XML
     */

    // helper for entryToXML below, serialize a property to XML
    var propertyToXml = function( property ) {
      var str = "<" + property.name();
      if ( property.type() )
        str += " m:type=\"" + property.type() + "\"";

      str += ">";
      if ( Array.isArray( property.value() ) ) {
        property.value().forEach( function( subProperty, index, arr ) {
          str += propertyToXml( subProperty );
        });
      } else {
        str += property.value();
      }
      str += "</" + property.name() + ">";
      return str;
    };

    // serialize an entry model to XML that can be POSTed back to IRIS
    var entryToXml = function( entry ) {
      var result = "<?xml version='1.0' encoding='utf-8'?>\
<entry xmlns=\"http://www.w3.org/2005/Atom\"\
 xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"\
 xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\"\
 xml:base=\"http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/\">\
<id>" + entry.id + "</id>\
<category term=\"" + entry.category.term + "\" scheme=\"" + entry.category.scheme + "\"/>\
<title type=\"text\">" + entry.title + "</title><content type=\"application/xml\">\
<m:properties>";
      entry.content.forEach( function( prop, index, arr ) {
        result += propertyToXml( prop );
      });
      result += "</m:properties></content></entry>";
      return result;
    };

    // add a new element to a bag in the data model
    var addBagElement = function( property ) {
      var bagElements = property.value(), firstElement, newElementVal = [], newElement;

      // we can't create an element if we don't know what should be in it.
      // (well, eventually we will look at the metadata, but until then...)
      if ( bagElements.length == 0 ) return null;

      firstElement = bagElements[0];
      firstElement.value().forEach( function( firstElementProperty ) {
        if ( Array.isArray(firstElementProperty.value()) ) { // another mvgroup!
          newElementVal.push( new Property( firstElementProperty.name(), [] ).type( firstElementProperty.type() ) );
        } else {
          newElementVal.push( new Property( firstElementProperty.name(), "" ) );
        }
      });
      newElement = new Property( "d:element", newElementVal );
      bagElements.push( newElement );
      return newElement;
    };

    var Property = function( name, value, type ) {
      this.pName = name;
      this.pValue = value;
      this.pType = type || "";
      this.pBinding = null;
    };

    Property.prototype.name = function() { return this.pName; }

    Property.prototype.value = function( newVal ) {
      if ( newVal ) {
        this.pValue = newVal;
        return this;
      }
      return this.pValue;
    };

    Property.prototype.binding = function( newBinding ) {
      if ( newBinding ) {
        this.pBinding = newBinding;
        return this;
      }
      return this.pBinding;
    };

    Property.prototype.type = function( newType ) {
      if ( newType ) {
        this.pType = newType;
        return this;
      }
      return this.pType;
    };

    Property.prototype.isLeaf = function() {
      return this.pType.substring(0,3) != "Bag";
    };

    // find the last descendant property of a parent property--
    // needed to know where to insert an additional child
    Property.prototype.lastDescendantProperty = function() {
      var val = this.value();
      if ( Array.isArray( val ) ) 
        return val[val.length - 1].lastDescendantProperty();
      else
        return this;
    };






    /* View functions:
     * entryView() renders an entry
     * feedView() renders a feed
     * updateModelFromView() updates a model to reflect user edits
     */

    // apply the bindings in the HTML view. In the long run it would
    // probably be better to make the bindings "live" rather than batching
    // over them like this at post time.
    var updateModelFromView = function( entry ) {
      var rows;
      if ( entry.view ) {
        entry.view.find("tr.property").each( function( index, propElement ) {
          updateBinding( propElement );
        });
      }
      return entry;
    };

    // add a new element to a bag in the view
    // parent.value() is expected to be an array of <d:element> properties
    // newProperty is the newly created property to insert
    // after is the model of the row to insert after
    var addBagRows = function( parent, newProperty, after ) {
      var tableLocation = $(getBindingForModel(after).containingView),
      inserter = function(start) { 
        var cursor = start; 
        return function(x) { cursor.after(x); cursor = x; };
      }(tableLocation);

      propertiesView( inserter, 
                      [ newProperty ], 
                      $(getBindingForModel(parent).containingView).find(".propkey").text(), 
                      tableLocation.attr("id"),
                      true );
    };

    // callback for the "add element" button: it creates a new element in 
    // the model and then inserts it into the view
    var createElement = function( property ) {
      // We need to find the last descendant property *before* adding a new one!
      var insertPosition = property.lastDescendantProperty(),
      newProperty = addBagElement( property );
      addBagRows( property, newProperty, insertPosition );
    };

    // create a table row for a single atomic property.
    // returned in jQuery form
    var propertyRow = function( property, rowId, label, editable ) {
      if ( editable ) {
        row = $( "<tr class=\"property\" id=\"" + rowId + "\">" + 
                 "<td class=\"propkey\">" + 
                 label + "</td><td class=\"propval\">" +
                 "<input type=\"text\" id=\"" + rowId + "-i\" value=\"" + 
                 property.value() + "\"/></td></tr>" );
        bind( row[0], row.find("input")[0], property );
      } else {
        row = $( "<tr class=\"property\" id=\"" + rowId + "\">" + 
                 "<td class=\"propkey\">" + 
                 label + "</td><td class=\"propval\">" +
                 property.value() + "</td></tr>" );
        bind( row[0], row.find("td.propval")[0], property );
      }
      return row;
    };

    // render a property array (as described in the ATOM parser section below)
    // as HTML rows, and bind the HTML rows back to the array elements.
    // * inserter - function to put the rows into the right place
    // * props    - the property array to render
    // * prefix   - the string to prefix property labels with (for nested properties)
    // * idprefix - the string to prefix property ids with (for nested properties)
    // * editable - whether to render edit boxes or not for the property values
    var propertiesView = function( inserter, props, prefix, idprefix, editable ) {
      props.forEach( function( prop, index, arr ) {
        var key = prop.name(), value = prop.value(), row,
        rowId = idprefix + "-" + index;

        if ( Array.isArray(prop.value()) ) {
          if ( editable && ( prop.type().substr(0,3) == "Bag" ) ) {
            row = $( "<tr class=\"propertybag\" id=\"" + rowId + "\">" +
                     "<td class=\"propkey\">" + prefix + " " + key + "</td>" +
                     "<td class=\"mvadd\"><a>Add element</a></td></tr>" );
            row.find("a").click( function() { createElement( prop ); } );
            bind( row[0], null, prop );
            inserter( row );
          }
          propertiesView( inserter, value, prefix + " " + key, rowId, editable );
        } else {
          row = propertyRow( prop, rowId, prefix + " " + key, editable );
          inserter( row );
        }
      });
    };

    // render a service document into an html node
    var serviceView = function( service, target ) {
      target.append("<div id=\"feedtitle\" class=\"subtitle\"><h3>Service: " +
                    service.title + "</h3></div>");

      service.entries.forEach( function( collection, i, arr ) {
        var linkButton = $("<div id=\"l-" + i + "\" " +
                           "class=\"olink\"><a class=\"linkbutton linkbutton-1\">" +
                           collection.title + "</a><span class=\"olinkrel\"" + 
                           collection.rel + "</span></div>" );
        linkButton.find("a").click( actionCallback( collection ) );
        target.append( linkButton );
      });
    };

    // render an entry model into an html node
    var entryView = function( entry, target ) {
      var linkcount = 0, table;

      resetBindings();

      target.append("<div id=\"feedtitle\" class=\"subtitle\"><h3>" + 
		    entry.title + "</h3></div>");

      entry.links.forEach( function( link, linkcount, array ) {
        var linkButton = $( "<div id=\"feedlink-" + linkcount + "\" class=\"olink\">" +
                            "<a class=\"linkbutton linkbutton-1\">" + link.title +
		            "</a><span class=\"olinkrel\"" + link.rel + "</span></div>" );

        linkButton.find("a").click( actionCallback( link ) );
        target.append( linkButton );
      });

      div = $("<div id=\"singleEntry\" class=\"entry\">");

      div.append( "<table class=\"content\" id=\"content-1\"></table>" );
      table = div.find("table");
      propertiesView( function(x) { table.append(x); }, entry.content,
                      "", "e", entry.linkRels[g.rels.input] );

      // pointer from the model to the view, to allow it to pick up changes
      entry.view = div;

      target.append( div );
    };

    // render a feed model into an html node
    var feedView = function( feed, target ) {
      resetBindings();

      target.append("<div id=\"feedtitle\" class=\"subtitle\"><h3>" + 
		    feed.title + "</h3></div>");

      feed.links.forEach( function( link, linkcount, array ) {
        var linkButton = $( "<div id=\"feedlink-" + linkcount + "\" class=\"olink\">" +
                            "<a class=\"linkbutton linkbutton-1\">" + link.title +
		            "</a><span class=\"olinkrel\"" + link.rel + "</span></div>" );

        linkButton.find("a").click( actionCallback( link ) );
        target.append( linkButton );
      });

      feed.entries.forEach( function(entry, index, array) {
        var linkCount = 0,
        entryId = "entry" + index,
        div = $("<div id=\"" + entryId + "\" class=\"entry\"><p class=\"entryid\">" +
                "Entry " + index + ": " + entry.id + "</p><p class=\"entrytitle\">" +
                entry.title + "</p></div>"), 
        entryTable;

        entry.links.forEach( function( link, linkCount, linkArray ) {
          var linkButton = $("<div id=\"l-" + index + "-" + linkCount + "\" " +
                             "class=\"olink\"><a class=\"linkbutton linkbutton-1\">" +
                             link.title + "</a><span class=\"olinkrel\"" + 
                             link.rel + "</span></div>" );
          linkButton.find("a").click( actionCallback( link ) );
          div.append( linkButton );
        });

        div.append( "<table class=\"content\" id=\"content-" + index + "\"></table>" );
        entryTable = div.find("table");
        propertiesView( function(x) { entryTable.append(x); }, entry.content,
                        "", "e-" + index, feed.linkRels[g.rels.input] );

        // pointer from the model to the view, to allow it to pick up changes
        entry.view = div;

        target.append( div );
      });
    };






    /* ATOM response handler and helpers:
     * handleDoc() takes an ajax response and builds a feed or entry model from it.
     * The content part of the model is an array of Property objects
     *
     * handleServiceDoc() builds a list of links from a service document.
     */

    // scan the space-separated urls in a rel string to find one that makes sense to us
    var parseLinkRels = function( relString ) {
      var fixedRelString = relString.replace( "http://www.temenos.com",
                                              "http://temenostech.temenos.com"),
      rels = fixedRelString.split(" "), i = rels.length;
      while ( --i >= 0 ) {
        if ( rels[i] == "self" ) return rels[i];
        if ( rels[i].substr(0,g.rels.base.length) == g.rels.base ) return rels[i];
      }
      return rels[0];
    };

    var handleContent = function( element ) {
      var result = [],
      c = element.firstElementChild, property, propertyType;

      while ( c != null ) {
        propertyType = c.getAttribute("m:type");
        if ( c.children.length ) {
          property = new Property( c.tagName, handleContent( c ) );
        } else {
          property = new Property( c.tagName, c.textContent );
        }
        if ( propertyType ) property.type( propertyType );
        result.push( property );
        c = c.nextElementSibling;
      }
      return result;
    };

    var handleServiceDoc = function( xml, status, request ) {
      var $xml = $(xml),
      model = { 
        type: "service",
        title: $xml.find("service > workspace > title").text(),
        entries: []
      };
      $xml.find("collection").each( function( i, collection ) {
        model.entries.push( { 
          title: collection.getElementsByTagName( "title" )[0].textContent,
          url: collection.getAttribute("href"),
          rel: "collection",
          entry: null
        } );
      });
      return model;
    };

    var handleEntry = function( xml, eTag ) {
      var entry = { type: "entry", linkRels: {}, links: [], eTag: eTag }, j, entryItem;

      for ( j=0; j<xml.children.length; ++j ) {
        entryItem = xml.children.item(j);

        if( entryItem.tagName == "id" ) {
          entry.id = entryItem.textContent;
        }
        if( entryItem.tagName == "title" ) {
          entry.title = entryItem.textContent;
        }
        if ( entryItem.tagName == "category" ) {
          entry.category = { term: entryItem.getAttribute("term"),
                           scheme: entryItem.getAttribute("scheme") };
        }
        if( entryItem.tagName == "link" ) {
          link = {
            url:  entryItem.getAttribute("href"),
            rel:  parseLinkRels(entryItem.getAttribute("rel")),
            type: entryItem.getAttribute("type"),
            title: entryItem.getAttribute("title"),
            entry: entry
          };
          entry.linkRels[link.rel] = link;
          entry.links.push( link );
          if ( link.rel == "self" )
            entry.url = link.url;
          link.entries = handleInlineEntries(entryItem);
        }
          
        if ( entryItem.tagName == "content" ) {
          props = entryItem.getElementsByTagNameNS(g.ns.m,"properties")[0];
          entry.content = handleContent(props);
        }
      }
      return entry;
    };

    var handleInlineEntries = function( xml ) {
      var entries = [], i;
      for ( i=0; i < xml.children.length ; ++i ) {
        entries.push( handleEntry( xml.children.item(i) ) );
      }
      return entries;
    };

        

    // capture either an entry document or a feed document
    var handleDoc = function( xml, status, request ) {
      var root = xml.documentElement,
      item = root.firstElementChild,
      entrycount = 0, linkcount = 0,
      eTag = request.getResponseHeader( "ETag" ),
      data = { type: root.tagName, links: [], linkRels: {}, eTag: eTag, view: null },
      entryItem, el, link, props, entry;

      if ( data.type == "entry" ) {
        while ( item != null ) {
          el = item.tagName;
          if ( el == "id" ) {
            data.id = item.textContent;
          }
          if ( el == "title" ) {
            data.title = item.textContent;
          }
          if ( el == "category" ) {
            data.category = { term: item.getAttribute("term"),
                              scheme: item.getAttribute("scheme") };
          }
	  if( el == "link" ) {
            link = {
              url:  item.getAttribute("href"),
              rel:  parseLinkRels(item.getAttribute("rel")),
              type: item.getAttribute("type"),
              title: item.getAttribute("title"),
              entry: data
            }
            if ( item.firstElementChild ) {
              link.entries = handleInlineEntries( item.firstElementChild );
            }

            data.linkRels[link.rel] = link;
            data.links.push( link );
            if ( link.rel == "self" )
              data.url = link.url;
          }

          if ( el == "content" ) {
            props = item.getElementsByTagNameNS(g.ns.m,"properties")[0];
            data.content = handleContent(props);
          }

          item = item.nextElementSibling;    
        }
      }

      if ( data.type == "feed" ) {
        data.entries = [];
        while ( item != null ) {
          el = item.tagName;
          if( el == "title" ) {
            data.title = item.textContent;
          }
	  if( el == "link" ) {
            link = {
              url:  item.getAttribute("href"),
              rel:  parseLinkRels(item.getAttribute("rel")),
              type: item.getAttribute("type"),
              title: item.getAttribute("title"),
              entry: null
            }
            data.linkRels[link.rel] = link;
            data.links.push( link );

            if ( link.rel == "self" )
              data.url = link.url;
          }
          if ( el == "entry" ) {
            entry = handleEntry( item, eTag );
            data.entries.push( entry );
          }
          item = item.nextElementSibling;    
        }
      }
debugData.model = data;
      return data;
    };






    /* Page handling:
     * showDoc() is the first handler for an ajax response.
     * it refreshes the history navigation elements
     * it updates the raw-xml "debug" control
     * it calls handleDoc() to parse the response content
     * it calls feedView() or entryView() to display the content
     */
    var setHistoryVal = function( div, content ) {
        div.attr("style","visibility: " + (content ? "visible" : "collapse") );
        div.find("a").text(content);
    };

    var clearOutput = function() {
      $("#container").empty();
      $("#debug").attr("style","visibility: collapse");
    };

    // render a response to an ajax call
    var showDoc = function( xml, status, request ) {
      var feed = xml.documentElement,
      url = history[historyPtr-1];

      setHistoryVal($("#prev"), (historyPtr>1) ? history[historyPtr-2] : "");
      setHistoryVal($("#curr"), url );
      setHistoryVal($("#next"), (historyPtr<history.length) ? history[historyPtr] : "");

      clearOutput();
      $("#raw").text( vkbeautify.xml( request.responseText ) );

      // Service document is rendered differently
      if ( feed.tagName == "service" ) {
        serviceView( handleServiceDoc( xml, status, request ), $("#container") );
      }
      else if ( feed.tagName == "feed" ) {
        feedView( handleDoc( xml, status, request ), $("#container") );
      }
      else if ( feed.tagName == "entry" ) {
        entryView( handleDoc( xml, status, request ), $("#container") );
      }
    };

    // callback function for the history quick-links
    var historyNavigate = function( event ) {
      var id = event.target.parentNode.getAttribute("id"),
      newUrl;

      if ( id == "prev" ) --historyPtr;
      if ( id == "next" ) ++historyPtr;

      newUrl = history[--historyPtr];
      history.splice(historyPtr,1);
      doAction( { rel: "dummy", url: newUrl } );
    };


    



    /* Startup:
     * callback to load the first service document and initialise the system.
     */
    var fetchServiceDocument = function() {
      var service = $("#service")[0].value;
 
      $.ajax({
	url: service,
	type: "GET",
        dataType: "xml",
        success: showDoc
      });
    };

    var init = function() {
      $("#serviceReload").click( fetchServiceDocument );

      fetchMetadata();
      fetchServiceDocument();
      history = [];
      historyPtr = 0;
    };






    /* Public interface: 
     * functions set as callbacks on the static html.
     */
    return({ init:            init,
	     doAction:        doAction,
             historyNavigate: historyNavigate
           });
  }();






  /* Startup processing
   * attach some callbacks to the static html elements
   */

  debugData = {};

  $("#manualquerylink").click(function() {
    helper.doAction( { url: $("#manualquery").val(), rel: "manual" } );
  });

  [ "#prev", "#curr", "#next" ].forEach( function( id ) {
    $(id).click( helper.historyNavigate );
  });

  $("#debugshow").click(function() { $("#debug").attr("style","visibility:visible"); });

  $("#service").change(function() {
    if (!$("base").length)
      $("head").append("<base href=\""+$("#service")[0].value+"\"/>");

    helper.init();
  });

});
