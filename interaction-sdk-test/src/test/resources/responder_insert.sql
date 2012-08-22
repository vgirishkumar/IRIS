
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('MUC', 'Franz Josef Strauß', 'Germany');
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('MIA', 'Miami International Airport', 'United States');
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('SFO', 'San Francisco International', 'United States');
INSERT INTO AIRPORT (CODE, NAME, COUNTRY) VALUES ('JFK', 'John F Kennedy International', 'United States');

INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('JFK' , '1' , '14:35:00' , 'LH410' , '2011-03-28 11:30:00.0' , '11:30:00' , 'MUC' , '2011-05-31 11:30:00.0');
INSERT INTO `FlightSchedule`(`arrivalAirportCode` , `flightScheduleID` , `arrivalTime` , `flightNo` , `firstDeparture` , `departureTime` , `departureAirportCode` , `lastDeparture`) VALUES('SFO' , '2' , '11:32:00' , 'LH520' , '2011-03-28 11:30:00.0' , '11:30:00' , 'JFK' , '2011-02-30 01:22:00.0');

INSERT INTO FLIGHT(FLIGHTID , TAKEOFFTIME) VALUES('1' , '1996-08-20 00:00:00');	