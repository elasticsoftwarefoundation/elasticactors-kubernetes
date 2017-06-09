# elasticactors-kubernetes
Run an ElasticActors cluster on K8S and deploy your actors as a jar file

Install the latest version of minikube from: https://github.com/kubernetes/minikube/releases

Run setup.sh in the minikube directory

minikube ip to get the address from the minikube host

rabbitmq cluster is reachable under $(`minikube ip`):31042

cassandra cluster is reachable under $(`minikube ip`):31672

to install the base actorsystem image (requires a running minikube)

kubectl create configmap actorsystem-config --from-file=base/src/main/k8s/system.properties --from-file=base/src/main/k8s/config.yaml
kubectl apply -f base/src/main/k8s/actorsystem.yaml




