#
# Populate your responder inmemory database with some test values
#
# For example:
INSERT INTO `Preferences`(`userID` , `currency` , `language`) VALUES('fred' , 'GBP' , 'EN');	

INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(1, 'FT0001');
INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(2, 'FT0002');
INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(3, 'FT0003');

INSERT INTO `Address`(`Id`,`PostCode`,`HouseNumber`) VALUES('House1', 'WD6 1OR', '99');
INSERT INTO `Address`(`Id`,`PostCode`,`HouseNumber`) VALUES('House2', 'WD3 4RR', '34');
INSERT INTO `Customer`(`Name`,`Address`,`DateOfBirth`) VALUES('Fred', 'House1', '1995-01-28');
INSERT INTO `Customer`(`Name`,`Address`,`DateOfBirth`) VALUES('Tom', 'House1', '1992-01-18');
INSERT INTO `Customer`(`Name`,`Address`,`DateOfBirth`) VALUES('Bob', 'House2', '1995-03-23');
