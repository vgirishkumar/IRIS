$(document).ready(function() {
    try {
        var refreshDays${dateChooser} = function () {
        	var m = parseInt($('#${month}').val());
        	var y = parseInt($('#${year}').val());
        	if (isNaN(m) || isNaN(y)) {
        		return;
        	}
        	var daysInMonth = getDaysInMonth (y, m);
        	var d = parseInt($('#${day}').val());
        	if (isNaN(d)) {
        		d = 0;
        	}
            //alert("day " + d);
            $('#${day}').empty();
            var daysForCombo = new Object();
			for (var i = 0; i < daysInMonth ; i++) {
				daysForCombo[i+1] = i + 1;
			}
           	$.each(daysForCombo, function(val, text) {
           	    $('#${day}').append(
           	        $('<option></option>').val(val).html(text)
           	    );
           	});
			var dayToSelect = (d <= daysInMonth) ? d : daysInMonth;             	
			$("#${day} option[value='" + dayToSelect + "']").attr('selected', 'selected');
        }
        $('#${month}').change(refreshDays${dateChooser});
        $('#${year}').change(refreshDays${dateChooser});
    } catch(e) {}
})