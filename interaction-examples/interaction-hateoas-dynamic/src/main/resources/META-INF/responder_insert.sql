#
# Populate your responder inmemory database with some test values
#
# For example:
DELETE FROM `Profile` WHERE `userID` = 'aphethean'

INSERT INTO `Profile`(`userID` , `name` , `DOB` , `location` , `pictureTaken`) VALUES('aphethean' , 'Aaron Phethean' , '1824-08-21 00:00:00' , '-27.4667,153.0333' , '2012-04-16 15:23:00');

DELETE FROM `Preferences` WHERE `userID` = 'aphethean'
	
INSERT INTO `Preferences`(`userID` , `currency` , `language`) VALUES('aphethean' , 'GBP' , 'EN');	

DELETE FROM `Note` WHERE `NoteID` BETWEEN 1 AND 8

INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(1,'Beverages', 'AU001');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(2,'Condiments', 'SY001');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(3,'Confections', 'SY002');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(4,'Dairy Products', 'AU002');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(5,'Grains/Cereals', 'AU003');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(6,'Meat/Poultry', 'AU001');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(7,'Produce', 'SY002');
INSERT INTO `Note`(`NoteID`,`Body`,`Reference`) VALUES(8,'Seafood', 'SY002');
