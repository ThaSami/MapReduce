#!/bin/bash


mappers=$1
reducers=$2

echo "======> Creating manager machine ...";
docker-machine create -d virtualbox manager;


echo "======> Creating $mappers mappers machines ...";
for node in $(seq 1 $mappers);
do
	echo "======> Creating mapper$node machine ...";
	docker-machine create -d virtualbox mapper$node;
done

echo "======> Creating $reducers reducers machines ...";
for node in $(seq 1 $reducers);
do
        echo "======> Creating reducer$node machine ...";
        docker-machine create -d virtualbox reducer$node;
done


docker-machine ls
