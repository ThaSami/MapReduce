#!/bin/bash

echo "======> sending map_reduce Image to Manager"
docker save map_reduce | bzip2 | docker-machine ssh manager 'bunzip2 | docker load'

