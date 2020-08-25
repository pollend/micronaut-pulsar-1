# Micronaut Pulsar

## WORK IN PROGRESS

This project includes integration between [Micronaut](http://micronaut.io) and [Apache Pulsar](https://pulsar.apache.org).

Quick info:
- @PulsarConsumer should be method that consumes message from topic(s) accepting either pattern or single item array
- @PulsarListener is to store subscription name and such
- @PulsarServiceUrlProvider should be used in cases where method should be used to provide url and trigger updates once 
changed in annotated class

Plans:
- on micronaut stop, stop consumers before destorying beans
- support Oauth 2 authentication for Pulsar
- add more configuration options to pulsar listener and consumer
- add pulsar producer handlers and registry
- pulsar administration features
