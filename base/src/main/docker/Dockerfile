FROM adoptopenjdk:11.0.4_11-jdk-hotspot
MAINTAINER Joost van de Wijgerd <jwijgerd@gmail.com>

COPY lib/           /usr/share/actorsystem/lib
COPY endpoint.jar      /usr/share/actorsystem
COPY configure_and_run.sh /opt/provision/configure_and_run.sh
COPY jvm.options /etc/actorsystem/jvm.options
COPY log4j2.xml /etc/actorsystem/log4j2.xml