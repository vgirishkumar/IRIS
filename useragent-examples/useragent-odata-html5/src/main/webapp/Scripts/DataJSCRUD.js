//ODATA Root Service URI
var ODATA_SVC = "http://localhost:8080/example/interaction-odata-notes.svc/";
var NOTES_ODATA_SVC = ODATA_SVC + "Note";

//User edit form Variables
var name = $("#name"),
	email = $("#email"),
	password = $("#password"),
    allFields = $([]).add(name).add(email).add(password),
	tips = $(".validateTips");


//Page Load Actions

function OnPageLoad() 
{
    $("#dialog:ui-dialog").dialog("destroy");

    $("#dialog-form").dialog({
        autoOpen: false,
        height: 375,
        width: 450,
        modal: true,
        close: function () {
            allFields.val("").removeClass("ui-state-error");
        }
    });

    $("#create-user").button()
			.click(OpenCreateUserDialog);

    GetServices();    
} 

//Page Events:

//Gets all the services
function GetServices() 
{
    $("#loadingServices").show();
    OData.read({ 
    		requestUri: ODATA_SVC,
    		headers: { Accept: "application/atomsvc+xml" }
    	}, GetServicesCallback);
}
//GetServices Success Callback
function GetServicesCallback(data, request) 
{
    $("#loadingServices").hide();
    $("#services").find("tr:gt(0)").remove();
    alert("stop");
    ApplyServiceTemplate(data.workspaces[0].collections);
}

//***********************Get Notes (READ)***************************
//Gets all the Entities for an EntitySet
function GetEntitySet(uri) 
{
    $("#loadingUsers").show();
    OData.read({ 
		requestUri: uri,
		headers: { Accept: "application/atom+xml" }
	}, GetEntitySetCallback);
}

//GetEntitySet Success Callback
function GetEntitySetCallback(data, request) 
{
    $("#loadingUsers").hide();
    $("#EntitySetName").text(data.__metadata.title)
    $("#users").find("tr:gt(0)").remove();
    ApplyTemplate(data.results)
}

//***********************End: Get Notes***************************

//*****************************Add User (CREATE)***************************
//Handle Create User Account button click
function OpenCreateUserDialog() 
{
    $("#dialog-form").dialog("option", "title", "Create An Account");
    $("#dialog-form").dialog("option", "buttons", [
                                                            {
                                                                text: "Save",
                                                                click: function () {
                                                                    var bValid = false;
                                                                    bValid = ValidateUserData();
                                                                    if (bValid) {
                                                                        AddUser();
                                                                    }
                                                                }
                                                            },
                                                            {
                                                                text: "Cancel",
                                                                click: function () {
                                                                    $("#dialog-form").dialog("close");
                                                                }
                                                            }
                                                        ]
                                    );
    $("#dialog-form").dialog("open");
}

//Handle the DataJS call for new user acccount creation
function AddUser() 
{
    $("#loading").show();
    var newUserdata = { username: $("#name").val(), email: $("#email").val(), password: $("#password").val() };
    var requestOptions = {
        requestUri: USERS_ODATA_SVC,
        method: "POST",
        data: newUserdata
    };

    OData.request(requestOptions, AddSuccessCallback, AddErrorCallback);

}

//AddUser Success Callback
function AddSuccessCallback(data, request) 
{
    $("#loading").hide('slow');
    $("#dialog-form").dialog("close");
    GetUsers();
}

//AddUser Error Callback
function AddErrorCallback(error) 
{
    alert("Error : " + error.message)
    $("#dialog-form").dialog("close");
}

//*************************End Add User***************************

//*************************Update User (UPDATE)***************************
//Handle Update hyper link click
function OpenUpdateDialog(userId) 
{
    $("#loading").hide();
    var cells = $("#userRow" + userId).children("td");
    $("#name").val(cells.eq(0).text());
    $("#email").val(cells.eq(1).text());
    $("#password").val(cells.eq(2).text());

    $("#dialog-form").dialog("option", "title", "Update Account");

    $("#dialog-form").dialog("option", "buttons", [
                        {
                            text: "Save",
                            click: function () {
                                var bValid = false;
                                bValid = ValidateUserData();
                                if (bValid) {
                                    UpdateUser(userId);
                                }
                            }
                        },
                        {
                            text: "Cancel",
                            click: function () {
                                $("#dialog-form").dialog("close");
                            }
                        }
                    ]);
    $("#dialog-form").dialog("open");
}

//Handle DataJS calls to Update user data
function UpdateUser(userId) 
{
    $("#loading").show();
    var updateUserdata = { username: $("#name").val(), email: $("#email").val(), password: $("#password").val() };
    var requestURI = USERS_ODATA_SVC + "(" + userId + ")";
    var requestOptions = {
        requestUri: requestURI,
        method: "PUT",
        data: updateUserdata
    };

    OData.request(requestOptions, UpdateSuccessCallback, UpdateErrorCallback);

}

//UpdateUser Suceess callback
function UpdateSuccessCallback(data, request) {
    $("#loading").hide('slow');
    $("#dialog-form").dialog("close");
    GetUsers();
}

//UpdateUser Error callback
function UpdateErrorCallback(error) {
    alert("Error : " + error.message)
    $("#dialog-form").dialog("close");
}
//*************************End : Update User (UPDATE)***************************

//*************************Delete User (DELETE)***************************

var $dialog = null;

//Handle Delete hyperlink click
function OpenDeleteDialog(userId) 
{
    $("#loading").hide();
    var cells = $("#userRow" + userId).children("td");

    $dialog = $('<div></div>')
		            .html('You are about to delete account "' + cells.eq(0).text() + '". Do you want to continue? ')
		            .dialog({
		                autoOpen: false,
                        width:400,
		                modal: true,
		                buttons: {
		                    "Yes": function () {
		                        DeleteUser(userId);
		                    },
		                    "No": function () {
		                        $(this).dialog("close");
		                    }
		                },
		                title: 'Delete Account'
		            });
        $dialog.dialog('open');
}

//Handles DataJS calls for delete user
function DeleteUser(userId) 
{
    var requestURI = USERS_ODATA_SVC + "(" + userId + ")";
    var requestOptions = {
                            requestUri: requestURI,
                            method: "DELETE",
                        };

    OData.request(requestOptions, DeleteSuccessCallback, DeleteErrorCallback);
}

//DeleteUser Success callback
function DeleteSuccessCallback()
{
    $dialog.dialog('close');
    GetUsers();
}

//DeleteUser Error callback
function DeleteErrorCallback(error)
{
    alert(error.message)
}
//*************************End : Delete User (DELETE)***************************

//*************************Helper Functions***************************

//Helper function to apply UI template for list of services
function ApplyServiceTemplate(links) 
{
	for (obj in links) {
		var link = links[obj];
		var content = "<tr id=\"entitySet_" + link.title + "\">" +
							"<td><a href=\"javascript:GetEntitySet('" + link.href + "')\">" + link.title + "</a></td>" +
						"</tr>";
        $(content).appendTo("#services tbody");
	}
}

//Helper function to apply UI template for Notes
function ApplyTemplate(data) 
{
	var header = "<tr class=\"ui-widget-header\">";
	for (obj in data[0].__metadata.properties) {
		header += "<th>" + obj + "</th>";
	}
	header += "<th>Actions</th>"
	header += "</tr>";
	$(header).appendTo("#users tbody");

	var body = "";
	for (row in data) {
		if (row != "__metadata") {
			body += "<tr id=\"userRow" + row + "\">";
			for (obj in data[0].__metadata.properties) {
				var prop = data[row][obj];
				if (prop.__deferred != null && prop.__deferred.uri != null) {
					body += "<td><a href=\"" + prop.__deferred.uri + "\">" + obj + "</a></td>";
				} else {
					body += "<td>" + prop + "</td>";
				}
			}
			body += 
				"<td>" +
					"<a href=\"javascript:OpenUpdateDialog(${userid})\">Update</a>" +
					" " +
					"<a href=\"javascript:OpenDeleteDialog(${userid})\">Delete</a>" +
					"</td>" +
				"</tr>";
		}                            
	}
    $(body).appendTo("#users tbody");
}

//Validation Helper, validates the user edit form
function ValidateUserData() 
{
    var bValid = true;
    allFields.removeClass("ui-state-error");

    bValid = bValid && checkLength(name, "username", 3, 16);
    bValid = bValid && checkLength(email, "email", 6, 80);
    bValid = bValid && checkLength(password, "password", 5, 16);

    bValid = bValid && checkRegexp(name, /^[a-z]([0-9a-z_])+$/i, "Username may consist of a-z, 0-9, underscores, begin with a letter.");
    bValid = bValid && checkRegexp(email, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "eg. ui@jquery.com");
    bValid = bValid && checkRegexp(password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9");
    return bValid;
}

//Helper function used to show validation errors
function updateTips(t) 
{
    var tips = $(".validateTips");
    tips
				.text(t)
				.addClass("ui-state-highlight");
    setTimeout(function () {
        tips.removeClass("ui-state-highlight", 1500);
    },
                        500);
}

//Helper function to validate length requirements
function checkLength(o, n, min, max) {
    if (o.val().length > max || o.val().length < min) {
        o.addClass("ui-state-error");
        updateTips("Length of " + n + " must be between " +
					min + " and " + max + ".");
        return false;
    }
    else {
        return true;
    }
}

//Helper function to validate using regular expression
function checkRegexp(o, regexp, n) {
    if (!(regexp.test(o.val()))) {
        o.addClass("ui-state-error");
        updateTips(n);
        return false;
    }
    else {
        return true;
    }
}
//*************************End : Helper Functions***************************