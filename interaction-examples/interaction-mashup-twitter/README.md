# interaction-mashup-twitter

This example shows a server side mashup of users, and their tweets.

NB - We intend to extend this example to also show a user-agent mashup 
of their location based on the 'geolocation' custom link rel.

## Setup

Some minor setup is required before this example will work.

1.  You first need to register with Twitter to get a consumer key.
2.  Modify  Twitter4JConsumer & OAuthRequestor to use YOUR consumer key.
3.  Run the OAuthRequestor to get an OAuth token on your machine
4.  As part of OAuthRequestor step.  Go to the web page to allow this app access to your account.
5.  You can enable the HypermediaITCase.testTweets test to easily check whether your client will work.
