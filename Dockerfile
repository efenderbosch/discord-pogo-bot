FROM openjdk:8-jdk-alpine

RUN apk update && \
    apk upgrade && \
    apk add graphicsmagick

