#
# Populate your responder inmemory database with some test values
#
# For example:
INSERT INTO `Preferences`(`userID` , `currency` , `language`) VALUES('fred' , 'GBP' , 'EN');	

INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(1, 'FT0001');
INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(2, 'FT0002');
INSERT INTO `FundTransfer`(`Id`,`Body`) VALUES(3, 'FT0003');

INSERT INTO `Address`(`Id`,`PostCode`,`HouseNumber`) VALUES('Fred', 'WD6 1OR', '99');
INSERT INTO `Customer`(`Name`,`Address`,`DateOfBirth`) VALUES('Fred', 'Fred', '1995-01-28');
