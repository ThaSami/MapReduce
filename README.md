## Summary

# Mapreduce FrameWork

A Mapreduce framework implementation in Java and docker.
The user can apply the Mapreduce functions in local environment using containers or on cluster using docker-Swarm, were everything is automated.
The code is written following the Rules in Effective Java, Clean code, the code also follows Design pattern and SOLID principles.

a full documenation file is presented in the repo 'FinalProjectReport.pdf'

more about mapreduce:
https://www.thegeekdiary.com/understanding-the-hadoop-mapreduce-framework/


# Project’s Features:

1- A simple easy to use GUI, with instant feedback to user.
![alt text](https://i.imgur.com/kwH5KFw.png)

2- Scalable decoupled Workflow implementation which can be rolled back on fail. And
easy to add to phases from XML parser.

3- Highly scalable run on swarm.

## presequites 

1- Linux OS.

2- Docker.

3- Docker-machine.

4- Docker-compose.

5- VirtualBox.

6- Docker-Swarm.

7- Java 1.8.

8- JavaFx.

## How to use it ?
1- Number of Mappers : take int value, defines how many mapper's nodes (swarm/containers) to run mapper's function.

2- Number of Reducers : take int value, defines how many reducers's nodes (swarm/containers) to run reducers's function.

3- Text File Path: the text file you want to process.

4- Where to execute: define where to run on Swarm cluster or Local 
   - Swarm -> this will create (Docker Machines) based on number of mappers and reducers using scripts in src/main/resources/Scripts
   - locally -> this will create containers based on number of mappers and reducers in src/main/resources/compose/docker-compose.vm   

5- Custom Imports: custom imports for Mapper and Reducer Function.

6- Mapper Function (must return a generic hashmap and takes file) defines the function to run on the mapper nodes.

7- Reducer Function (must return a generic hashmap and takes hashmap) defines the function to run on the reducer nodes.

## Note
Make sure to edit the Main server ip in /src/main/java/com/atypon/utility/Constants.java to match your host ip address.


## Contribution
just add your classes and define the workflow steps in src/main/resources/
