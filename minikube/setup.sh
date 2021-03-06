#!/usr/bin/env bash

MINIKUBE_PACKAGE=darwin
MINIKUBE_VERSION="0.20.0"

if [[ "$OSTYPE" == "linux-gnu" ]]; then
    MINIKUBE_PACKAGE=linux
elif [[ "$OSTYPE" == "darwin"* ]]; then
    MINIKUBE_PACKAGE=darwin
else
    echo "Operating System of type ${OSTYPE} not supported by Minikube"
    exit 1;
fi

if [ ! -f /usr/local/bin/minikube ]; then
    echo "Installing minikube version ${MINIKUBE_VERSION}"
    curl -Lo minikube https://storage.googleapis.com/minikube/releases/v${MINIKUBE_VERSION}/minikube-${MINIKUBE_PACKAGE}-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/
fi

if [ "minikube version: v${MINIKUBE_VERSION}" != "$(minikube version)" ]; then
    echo "Upgrading minikube from version $(minikube version) to ${MINIKUBE_VERSION}"
    curl -Lo minikube https://storage.googleapis.com/minikube/releases/v${MINIKUBE_VERSION}/minikube-${MINIKUBE_PACKAGE}-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/
fi

# make sure there is no old proxy running
killall kubectl

minikube config set cpus 4
minikube config set memory 4096
minikube start --vm-driver="virtualbox"
minikube addons enable heapster
minikube addons disable ingress
# this is to counter clock skew when your laptop hibernates
minikube ssh -- docker run -i --rm --privileged --pid=host debian nsenter -t 1 -m -u -n -i date -u $(date -u +%m%d%H%M%Y)

sleep 10

kubectl proxy &

sleep 10

#open the minikube dashboard (this will ensure that minikube running before executing the below commands)
minikube dashboard

# setup rabbitmq and cassandra
kubectl create secret generic rabbitmq-config --from-literal=erlang-cookie=2b3c4753-e8ee-487c-9564-619c4777c228
kubectl apply -f rabbitmq.yaml
kubectl create configmap elasticactors-cql --from-file=./elasticactors.cql
kubectl apply -f cassandra.yaml
sleep 30
kubectl apply -f create-schema.yaml
