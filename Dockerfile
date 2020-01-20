FROM openjdk:8
COPY ./target/classes/com.atypon.nodes.reducernode.ReducerNode.class /tmp
COPY ./temp/reducer/ReducerUtil.class /tmp
WORKDIR /tmp
