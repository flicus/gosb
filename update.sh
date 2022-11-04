#!/bin/sh

microk8s kubectl delete deployment gosb -n flicus
microk8s kubectl apply -f kubernetes/deployment.yml -n flicus