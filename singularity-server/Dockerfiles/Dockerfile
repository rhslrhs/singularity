# ```
# singularity\singularity-server>  docker build -t singularity-gw:1.0.0 -f Dockerfiles/Dockerfile .
# ```
FROM openjdk:17-jdk-slim
LABEL name="rhslrhs"

ENV TZ=Asia/Seoul

RUN echo 'export PATH=/usr/local/openjdk-17/bin:$PATH' >> ~/.bashrc

EXPOSE 8080

COPY build/libs/singularity-server-0.0.1-SNAPSHOT.jar /var/apps/singularity-server-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "/var/apps/singularity-server-0.0.1-SNAPSHOT.jar"]