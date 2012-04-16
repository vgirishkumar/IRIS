# IRIS

A runtime for Interaction Resources & Information Services

## Purpose

Quickly and easily create REST APIs for use by humans and machines.

The project allow you to:

* create web based services according to RESTful constraints
* aggregate / mashup multiple resource managers into a single interaction service
* program Hypermedia (HATEOAS) interactions with a state machine domain specific language (DSL)


## Requires

* aphethean/jax-rs-hateoas.git (mvn install)
* Java JDK 1.6
* Maven 3

## Build commands:

mvn clean		Clean projects
mvn install		Build and install to maven repository
mvn install -Ddebug	Build and install to maven repository without running integration tests
mvn site		Build maven site
mvn site:deploy		Deploy maven site [default: C:\temp\iris\site]

