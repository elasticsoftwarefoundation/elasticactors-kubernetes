#!/usr/bin/env bash

NAME=actorsystem
CONFDIR=/etc/actorsystem

classpath()
{
    cp="$EXTRA_CLASSPATH"
    for j in /usr/share/$NAME/lib/*.jar; do
        [ "x$cp" = "x" ] && cp=$j || cp=$cp:$j
    done
    for j in /usr/share/$NAME/*.jar; do
        [ "x$cp" = "x" ] && cp=$j || cp=$cp:$j
    done

    # use JNA if installed in standard location
    [ -r /usr/share/java/jna.jar ] && cp="$cp:/usr/share/java/jna.jar"

    # load extra configuration from the conf directory
    printf "$cp:$CONFDIR/conf"
}

# source the variables
set -a
source /etc/actorsystem/jvm.options

java -cp `classpath` -Dlog4j.configurationFile=file://${CONFDIR}/log4j2.xml -Dea.node.address=${NODE_ADDRESS} -Dea.node.id=`hostname` ${JVM_OPTS} org.elasticsoftwarefoundation.elasticactors.container.Entrypoint