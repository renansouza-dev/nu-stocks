FROM adoptopenjdk/openjdk14-openj9:jdk-14.0.2_12_openj9-0.21.0-alpine-slim
COPY target/nu-stocks*.jar nu-stocks.jar
EXPOSE 8080
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote ${JAVA_OPTS} -jar nu-stocks.jar