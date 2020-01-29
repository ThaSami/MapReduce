#!/bin/bash

numOfMappers=$1
numOfReducers=$2

docker-machine ssh manager \
  "docker swarm init \
        --listen-addr $(docker-machine ip manager) \
        --advertise-addr $(docker-machine ip manager)"

###############################################

for node in $(seq 1 $numOfMappers); do
  echo "======> joining mapper$node to the swarm ..."
  docker-machine ssh mapper$node \
    "docker swarm join \
        --token $(docker-machine ssh manager "docker swarm join-token worker -q") \
        --listen-addr $(docker-machine ip mapper$node) \
        --advertise-addr $(docker-machine ip mapper$node) \
        $(docker-machine ip manager)"

  echo "=====> adding label to mapper$node"
  docker-machine ssh manager "docker node update --label-add name=mapper mapper$node"

done

#############################################

for node in $(seq 1 $numOfReducers); do
  echo "======> joining reducer$node to the swarm ..."
  docker-machine ssh reducer$node \
    "docker swarm join \
        --token $(docker-machine ssh manager "docker swarm join-token worker -q") \
        --listen-addr $(docker-machine ip reducer$node) \
        --advertise-addr $(docker-machine ip reducer$node) \
        $(docker-machine ip manager)"

  echo "=====> adding label to reducer$node"
  docker-machine ssh manager "docker node update --label-add name=reducer reducer$node"

done

############################################

echo "==========> installing Swarm visualizer at port 8080"
docker-machine ssh manager "docker run -it -d -p 8080:8080 -v /var/run/docker.sock:/var/run/docker.sock dockersamples/visualizer"
