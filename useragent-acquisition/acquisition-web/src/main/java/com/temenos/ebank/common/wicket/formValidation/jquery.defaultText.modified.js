
(function($){
    $.defaultText = function(opts) {
        var selector = 'input:text[title]',
            ctx = opts && opts.context ? opts.context : 'body',
            css = opts && opts.css ? opts.css : 'default',
            form_clear = [{selector: 'form', type:'submit'}];
            clear_events = opts && opts.clearEvents ? form_clear.concat(opts.clearEvents) : form_clear;
        
        $(ctx).delegate(selector, 'focusin', function(e){
            onFocus($(this));
        }).delegate(selector, 'focusout', function(e){
            onFocusOut($(this));
        });
        
        $(selector).each(function(){onFocusOut($(this));});
        
        $.each(clear_events, function(i, event){
            
            var sel = event.selector,
                type = event.type,
                ele = $(sel),
                len = ele.length;
            
            if (ele.size()) {
                var ev_queue = $.data( ele.get(0), "events" );

                if (ev_queue) {
                    for (var x=0; x<len; x++) {
                        $.data( ele.get(x), "events" )[type].unshift({
                            type : type,
                            guid : null,
                            namespace : "",
                            data : undefined,
                            handler: function() {
                                blink();
                            }
                        });
                    }
                } else {
                    ele.bind(type, function(){
                        blink();
                    });
                }
                
            } 
            
        });
        
        function onFocus(ele) {
            var title = ele.attr('title'),
                val = ele.val();
            ele.removeClass(css);
            if (title === val) ele.val('');
        }
        
        function onFocusOut( ele ){
            var title = ele.attr('title'),
                val = ele.val();
            if ($.trim(val) === '' || val === title) 
            	ele.val(title).addClass(css); 
        }
        
        function blink(){
            $(selector).each(function(){
                onFocus($(this));
            });
            setTimeout(function(){
                $(selector).each(function(){onFocusOut($(this));}); 
            }, 1);
        }
    }
})(jQuery);