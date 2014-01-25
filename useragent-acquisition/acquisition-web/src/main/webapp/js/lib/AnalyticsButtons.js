// analytics template for recording button clicks" -->
callAnalytics = function(button) {
	var custom_subgroup = "";
	switch (button) {
	case "cancel":
		custom_subgroup = "cancel";
		break;
	case "save":
		custom_subgroup = "save";
		break;
	}
	
	dcsMultiTrack("WT.si_n", custom_subgroup, "WT.si_x", "1");
}