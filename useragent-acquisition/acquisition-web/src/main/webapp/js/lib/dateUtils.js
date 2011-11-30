/*
 * Tests whether the year y is a leap year
 */
function isLeapYear (y) { 
	return y%400==0 || (y%100!=0 && y%4==0);
}
/*
 * Returns the number of days in a given month m of the year y
 */
function getDaysInMonth(y, m) {
	var daysInMonth = 31;
    switch (m) {
    	//zero-index    
        case 1: daysInMonth = isLeapYear (y) ? 29 : 28;break;
        case 3:
        case 5:
        case 8:
        case 10: daysInMonth = 30;
    }
    return daysInMonth;
}