#!/bin/bash

nodes=$(docker-machine ssh manager "docker node ls | grep mapper* | wc -l")


for node in $(seq 1 $nodes);
do

    echo "======> sending map_reduce Image to mapper$node"
    docker save map_reduce | bzip2 | docker-machine ssh mapper$node 'bunzip2 | docker load'


done