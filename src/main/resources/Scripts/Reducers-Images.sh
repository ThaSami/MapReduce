#!/bin/bash

nodes=$(docker-machine ssh manager "docker node ls | grep reducer* | wc -l")

for node in $(seq 1 $nodes); do

  echo "======> sending map_reduce Image to reducer$node"
  docker save map_reduce | bzip2 | docker-machine ssh reducer$node 'bunzip2 | docker load'

done
