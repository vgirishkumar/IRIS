#
# Populate your responder inmemory database with some test values
#
# For example:
INSERT INTO `rb_order`(`Id` , `location` , `name` , `email` , `milk` , `quantity` , `size`) VALUES('RB1000' , 'abc' , 'abc' , 'test@abc.com' , 'abc' , '1' , 'abc');	
INSERT INTO `rb_payment`(`Id` , `orderId`, `authorisationCode`) VALUES('1000', 'RB1000', 'abc');	
