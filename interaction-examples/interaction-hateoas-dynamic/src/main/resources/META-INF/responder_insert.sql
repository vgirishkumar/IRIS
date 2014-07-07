#
# Populate your responder inmemory database with some test values
#
# For example:
DELETE FROM `Profile` WHERE `userID` = 'aphethean'

INSERT INTO `Profile`(`userID` , `name` , `DOB` , `location` , `pictureTaken`) VALUES('aphethean' , 'Aaron Phethean' , '1824-08-21 00:00:00' , '-27.4667,153.0333' , '2012-04-16 15:23:00');

DELETE FROM `Preferences` WHERE `userID` = 'aphethean'
	
INSERT INTO `Preferences`(`userID` , `currency` , `language`) VALUES('aphethean' , 'GBP' , 'EN');	

DELETE FROM `Note` WHERE `NoteID` BETWEEN 1 AND 8

INSERT INTO `Note`(`NoteID`,`Body`) VALUES(1,'Beverages');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(2,'Condiments');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(3,'Confections');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(4,'Dairy Products');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(5,'Grains/Cereals');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(6,'Meat/Poultry');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(7,'Produce');
INSERT INTO `Note`(`NoteID`,`Body`) VALUES(8,'Seafood');
