var ebankValidator; 
$(document).ready(function(){
		ebankValidator = $('#${formMarkupId}').validate({ 
			errorElement: 'dd',
			errorPlacement: function(error, element){
				error.insertAfter(element.parent());
			},
			focusOption: function( errorList ){
			     var focusLink = $("<a href='' style='text-decoration:none'>&nbsp</a>");
			     focusLink.prependTo(errorList[0].element.parentNode);
			     focusLink.focus();
			     // manually trigger focusin event; without it, focusin handler isn't called, findLastActive won't have anything to find
			     focusLink.trigger("focusin");
			     focusLink.remove();
			}
		});
 });
