FROM openjdk:11
WORKDIR /opt
ENV PORT 8080
EXPOSE 8000
COPY target/*.jar /opt/pedal.jar
ENTRYPOINT exec java $JAVA_OPTS -jar pedal.jar