FROM centos

RUN yum install -y java-11-openjdk-devel

VOLUME /tmp
ADD /target/dish-order-service-0.0.1-SNAPSHOT.jar dish-order-service.jar
RUN sh -c 'touch /dish-order-service.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/dish-order-service.jar"]