(function($){
 $.fn.idleTimeout = function(options) {
    var defaults = {
			inactivity: 1800000, //30 Minutes
			noconfirm: 10000, //10 Seconds
			sessionAlive: 1785000, //5 seconds before dialogue						
			alive_url: '/resume',
			logout_url: 'www.google.com'
		}
    
    //##############################
    //## Private Variables
    //##############################
    var opts = $.extend(defaults, options);
    opts.sessionAlive = opts.inactivity - 5000;    
    var liveTimeout, confTimeout, sessionTimeout, lastSessionTimeout;
    var modal = "<div id='modal_pop'><p>You are about to be signed out due to inactivity.</p></div>";		
	var lastTimeKA = $.now();	

	//##################################################
    //## reset inactivity timer . Please note :
	//##  -it sets the dialogue on a timer
	//##  -it sets a keepalive signal timer based on the
	//##   time of the last such signal
    //##################################################
    var start_liveTimeout = function()
    {
      clearTimeout(liveTimeout);
      clearTimeout(confTimeout);
      liveTimeout = setTimeout(logout, opts.inactivity);
      if(opts.sessionAlive)
      {
       clearTimeout(sessionTimeout);
	   var currentTimeKA = Math.min($.now() + opts.sessionAlive, lastTimeKA + opts.sessionAlive) - $.now();
	   sessionTimeout = setTimeout(keep_session, currentTimeKA);	   
      }
    }
    
    //#################################################
    //## display dialog and 
    //##    logout if still inactive during dialog time
    //##    keep alive signal and reset client session
    //#################################################
    var logout = function()
    {
      
      confTimeout = setTimeout(redirect, opts.noconfirm);
      $(modal).dialog({
        buttons: {"Stay Logged In":  function(){
          $(this).dialog('close');
          stay_logged_in();
        }},
        modal: true,
        title: 'Auto Logout'
      });
      
    }

    //#################################################
    //## call to logout screen 
    //#################################################
    var redirect = function()
    {
    	window.location = opts.logout_url;
    }
    
    //######################################################
    //## used by dialogue to reset session (server & client)
    //######################################################
    var stay_logged_in = function(el)
    {
      start_liveTimeout();
      if(opts.alive_url)
      {
        keep_session();
      }
    }
    
    //#######################################################
    //## basic keep alive signal : ajax call to current page
    //## and set time of it;
    //#######################################################
    var keep_session = function()
    {
      $.get(opts.alive_url);
	  lastTimeKA = $.now();
    } 
    
    //######################################################
    //##return the instance of the item as a plugin
    //##please note that any kind of click gets handled by 
    //## the start_liveTimeout function
    //######################################################
    return this.each(function() {
      obj = $(this);      
      start_liveTimeout();      
      $(document).bind('click', start_liveTimeout);
    });
    
 };
})(jQuery);