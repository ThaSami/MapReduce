FROM openjdk:8
RUN mkdir -p /com/atypon/nodes
COPY ../../../../target/classes/com/atypon/nodes /com/atypon/nodes
COPY ../../../../temp/mapper/MapperUtil.class /com/atypon/nodes
COPY ../../../../temp/reducer/ReducerUtil.class /com/atypon/nodes
WORKDIR /com/atypon/nodes

