#!/bin/sh

microk8s kubectl apply -f volume.yml -f configmap.yml -f service.yml deployment.yml -n flicus
