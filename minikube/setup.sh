#!/usr/bin/env bash

# TODO: make sure minikube is installed

minikube config set cpus 4
minikube config set memory 4096
minikube start --vm-driver="virtualbox"
minikube addons enable heapster
minikube addons disable ingress
# this is to counter clock skew when your laptop hibernates
minikube ssh -- docker run -i --rm --privileged --pid=host debian nsenter -t 1 -m -u -n -i date -u $(date -u +%m%d%H%M%Y)

#open the minikube dashboard (this will ensure that minikube running before executing the below commands)
minikube dashboard

# setup rabbitmq
kubectl create secret generic rabbitmq-config --from-literal=erlang-cookie=2b3c4753-e8ee-487c-9564-619c4777c228
kubectl apply -f rabbitmq.yaml
kubectl apply -f cassandra.yaml
