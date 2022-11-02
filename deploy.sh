#!/bin/sh

./mvnw package
docker build -f src/main/docker/Dockerfile.jvm -t flicus/gosb .
docker save flicus/gosb > gosb.tar
microk8s ctr image import gosb.tar
ktl delete deployment gosb -n flicus
ktl delete service gosb -n flicus
ktl apply -f target/kubernetes/kubernetes.yml -n flicus