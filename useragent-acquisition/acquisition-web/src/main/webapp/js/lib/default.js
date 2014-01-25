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

$(function() {
	initHintPanel();
	// This finds span.printbtn and replaces it with a print link
	$('.action span.printbtn').html('<a href="#" onclick="window.print(); return false;" class="printbtn"><span><span>Print this page</span></span></a>');

	// As above, but for a close button
	$('.action span.closebtn').html('<a href="#" onclick="window.close(); return false;" class="cancel"><span><span>Close this page</span></span></a>');

	// This spawns a calander, but only once per page, for the input field #date
	$( "#date" ).datepicker({
		nextText: '&#xBB;',
		prevText: '&#xAB;',
		showOn: "button",
		buttonImageOnly: true,
		buttonImage: '/lloydsbg/2011-q1/irb/prototypes/2/img/icon-datepicker.gif',
		showOn: 'both'
	});

	// Initialises FancyBox. Integers have to appear without quotes, and are assumed to be
	// in pixel units. I don't know why either.
	$(".fancybox").fancybox({
		'titlePosition'		: 'inside',
		'transitionIn'		: 'none',
		'transitionOut'		: 'none',
		'autoDimensions'	: false,
		width					: 426,
		height				: 'auto',
		'padding'				: 15,
		'overlayOpacity'	: 0
	});
});