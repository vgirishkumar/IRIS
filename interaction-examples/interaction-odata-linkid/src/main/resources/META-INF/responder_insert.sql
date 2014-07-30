#
# Populate your responder inmemory database with some test values
#
# For example:
#INSERT INTO `Airport`(`name` , `code` , `country`) VALUES('example' , 'example' , 'example');	
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('example' , '1' , '00:00:00' , 'example' , '1996-08-20 00:00:00' , '00:00:00' , 'example' , '1996-08-20 00:00:00');	
#INSERT INTO `Flight`(`flightID` , `flightScheduleID` , `takeoffTime`) VALUES('1' , '1' , '1996-08-20 00:00:00');	
#INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('MUC', 'Franz Josef Strau�', 'Germany');
#INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('MIA', 'Miami International Airport', 'United States');
#INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('SFO', 'San Francisco International', 'United States');
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('JFK', 'John F Kennedy International', 'United States');
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('GVA', 'Geneva International Airport', 'CH');   
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('LTN' ,'London Luton International Airport', 'GB'); 

#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('JFK' , '1' , '14:35:00' , 'LH410' , '2011-03-28 11:30:00.0' , '11:30:00' , 'MUC' , '2011-05-31 11:30:00.0');
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('SFO' , '2' , '11:32:00' , 'LH520' , '2011-03-28 11:30:00.0' , '11:30:00' , 'JFK' , '2011-02-30 01:22:00.0');
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('GVA' , '2055' , '16:05:00' , 'EZY2055' , '1996-08-20 13:20:00' , '13:20:00' , 'LTN' , '2014-08-20 13:20:00'); 
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('GVA' , '2051' , '10:10:00' , 'EZY2051' , '1996-08-20 07:25:00' , '07:25:00' , 'LTN' , '2014-08-20 07:25:00'); 
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('GVA' , '2061' , '20:55:00' , 'EZY2061' , '1996-08-20 18:15:00' , '18:15:00' , 'LTN' , '2014-08-20 18:15:00'); 
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('LTN' , '2052' , '11:25:00' , 'EZY2052' , '1996-08-20 10:45:00' , '10:45:00' , 'GVA' , '2014-08-20 10:45:00'); 
#INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('LTN' , '2062' , '22:10:00' , 'EZY2062' , '1996-08-20 21:30:00' , '21:30:00' , 'GVA' , '2014-08-20 21:30:00'); 

#INSERT INTO `Flight`(`flightID` , `flightScheduleNum` , `takeoffTime`) VALUES('1' , '1' , '1996-08-20 00:00:00');
#INSERT INTO `Flight`(`flightID` , `flightScheduleNum` , `takeoffTime`) VALUES('2' , '2055' , '2012-08-12 13:21:05');
#INSERT INTO `Flight`(`flightID` , `flightScheduleNum` , `takeoffTime`) VALUES('3' , '2051' , '2012-08-12 07:26:08'); 
#INSERT INTO `Flight`(`flightID` , `flightScheduleNum` , `takeoffTime`) VALUES('4' , '2062' , '2012-08-15 22:34:45');

#INSERT INTO `Passenger`(`passengerNo` , `flightID` , `name`, `dateOfBirth`) VALUES('1' , '1' , 'Passenger One', '1985-09-27 00:00:00');
#INSERT INTO `Passenger`(`passengerNo` , `flightID` , `name`, `dateOfBirth`) VALUES('2' , '3' , 'Passenger Two', '1982-09-27 00:00:00');
#INSERT INTO `Passenger`(`passengerNo` , `flightID` , `name`, `dateOfBirth`) VALUES('3' , '1' , 'Passenger Three', '1982-09-27 00:00:00');
