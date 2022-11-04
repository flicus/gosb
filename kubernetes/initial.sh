#!/bin/sh

microk8s kubectl apply -f kubernetes/volume.yml -n flicus
microk8s kubectl apply -f kubernetes/configmap.yml -n flicus
microk8s kubectl apply -f kubernetes/service.yml -n flicus
microk8s kubectl apply -f kubernetes/deployment.yml -n flicus
