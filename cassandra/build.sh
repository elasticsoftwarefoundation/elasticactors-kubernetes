#!/usr/bin/env bash

CASSANDRA_VERSION=3.11.0

eval $(minikube docker-env)
docker build --build-arg "CASSANDRA_VERSION=${CASSANDRA_VERSION}" -t elasticactors/cassandra:${CASSANDRA_VERSION} src/main/docker
docker push elasticactors/cassandra:${CASSANDRA_VERSION}