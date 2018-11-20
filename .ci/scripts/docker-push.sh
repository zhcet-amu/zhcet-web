#!/bin/bash

docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
./gradlew dockerPush
