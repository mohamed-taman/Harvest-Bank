#!/usr/bin/env bash
## Author: Mohamed Taman
## version: v0.7

echo -e "\nStopping [Harvest Bank] μServices ....\n\
---------------------------------------\n"
for port in 8090 8091 8092
do
    echo "Stopping μService at port $port ...."
    curl -X POST localhost:${port}/actuator/shutdown
    echo -e "\nμService at port ${port} stopped successfully .... \n"
done
