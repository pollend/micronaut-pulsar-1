FROM openjdk:14-alpine
COPY build/libs/pulsar-*-all.jar pulsar.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "pulsar.jar"]