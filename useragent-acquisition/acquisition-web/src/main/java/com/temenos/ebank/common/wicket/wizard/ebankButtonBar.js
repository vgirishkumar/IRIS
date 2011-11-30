var needReload = false;

$(window).bind('pageshow', function() {
	if (needReload) {
		Wicket.$("${cancelButtonMarkupId}").onclick = "doCancel(); return false;";
		window.location.reload();
	}
});

$(document).ready(function() {
	$("#${dialogMarkupId}").dialog( {
		autoOpen: false,
		resizable: false,
		width: 400,
		height: 200,
		modal: true,
		title: "${confirmTitle}",
		buttons: {
			"${back}": function() {
				$(this).dialog("close");
			},
			"${exit}": function() {
				$(this).dialog("close");
				needReload = true;
				Wicket.$("${cancelButtonMarkupId}").onclick = "";
				Wicket.$("${cancelButtonMarkupId}").click();
			},
			"${saveAndExit}": function() {
				$(this).dialog("close");
				Wicket.$("${saveAndCancelButtonMarkupId}").click();
			}
		}
	});
});

function doCancel() {
	if (form_is_modified(Wicket.$("${formMarkupId}"))) {
		$("#${dialogMarkupId}").dialog("open");
	} else {
		needReload = true;
		Wicket.$("${cancelButtonMarkupId}").onclick = "";
		Wicket.$("${cancelButtonMarkupId}").click();
	}
}

function form_is_modified(oForm)
{
	var el, opt, hasDefault, i = 0, j;
	while (el = oForm.elements[i++]) {
		switch (el.type) {
		case 'text':
		case 'textarea':
		case 'hidden':
		    if (!/^\s*$/.test(el.value) && el.value != el.defaultValue) return true;
		    break;
		case 'checkbox':
		case 'radio':
			if (el.checked != el.defaultChecked) return true;
			break;
		case 'select-one':
		case 'select-multiple':
			j = 0, hasDefault = false;
			while (opt = el.options[j++])
				if (opt.defaultSelected) hasDefault = true;
			j = hasDefault ? 0 : 1;
			while (opt = el.options[j++]) 
				if (opt.selected != opt.defaultSelected) return true;
			break;
		}
	}
	return false;
}