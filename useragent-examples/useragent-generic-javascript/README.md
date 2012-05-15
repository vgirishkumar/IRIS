# An IRIS user agent example

Originally forked from jax-rs-hateoas.

## Purpose

Provides a way for a human to interact with an IRIS endpoint using a java script based 
generic user agent.  This generic user agent is informed of the available actions, i.e.
the application state, using links and it is able to make decisions about how to display
resources by using a links relations element and a fields vocabulary element.

This example demonstrates the use of:
* the Hypermedia Application Language (HAL) media type
* Hypermedia (HATEOAS) interactions (application state via links)
* a form media based on HAL (form/hal+json & form/hal+xml)
* a very simple application state machine with links to items and ollections (see http://tools.ietf.org/html/rfc6573)


## Requires

* Java JDK 1.7 (for the coffee script compiler)
* Maven 3

## Build commands:

mvn clean		Clean projects
mvn install		Build and install to maven repository
NB - this project can be used by specifying it as a war dependency in your maven web application project

