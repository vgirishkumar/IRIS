	//########################################################
	//## values injected from BasePage.java. Please note:
	//##  -inactivity + noconfirm = application session timeout 
	//##  -noconfirm = time for the dialogue to remain open
	//##  -alive_url = wicket url for the current page
	//##  -logout_url = wicket mounted url for the logout page
	//########################################################

/** With Optional Overrides **/
  $(document).ready(function(){
    $(document).idleTimeout({
      inactivity: ${inactivityMillis},
      noconfirm: ${noConfirmMillis},
      alive_url: '${alive_url}',
      logout_url: '${logout_url}'
    });
  });
