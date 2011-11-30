// Tooltips
function initHintPanel(){
	$('fieldset dt img,.action em,.help').tooltip({
	    track: true,
	    delay: 0,
	    showBody: " :: ",
	    fade: 250,
	    showURL: false
	});
}
