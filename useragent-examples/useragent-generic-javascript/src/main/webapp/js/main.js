require(['jquery', 'cs!generic-rest-client'], function($, Client) {
    //This function is called when js/generic-rest-client.coffee is loaded.
    //If generic-rest-client.coffee calls define(), then this function is not fired until
    //all those dependencies have loaded, and the Client argument will hold
    //the module value for "generic-rest-client".
    console.log($);  // jquery loaded
    console.log('Initialised generic-rest-client');
    $; // bootstrap jquery application
});

