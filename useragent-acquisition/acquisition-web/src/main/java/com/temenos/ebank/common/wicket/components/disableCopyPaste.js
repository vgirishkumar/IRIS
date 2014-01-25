var globalPasteDisabledMessage;

function disablePaste(fieldId, pasteDisabledMessage){
	var inputJqObj = $('#' + fieldId);
	globalPasteDisabledMessage = pasteDisabledMessage;
	inputJqObj.bind('paste', function(e) {
        var el = $(this);
        setTimeout(function() {
            inputJqObj.val('');
            alert(globalPasteDisabledMessage);
        }, 100);
    })
}