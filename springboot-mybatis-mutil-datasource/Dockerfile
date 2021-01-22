# 该镜像需要依赖的基础镜像
#FROM lhl-docker-java8:1.0
FROM openjdk:8-jdk-alpine
USER root
#FROM java:8
#FROM openjdk8:latest
# 将targer目录下的jar包复制到docker容器/home/springboot目录下面目录下面
WORKDIR /home/springboot
#ADD ./target/springboot-helloworld.jar /home/springboot/bin/springboot-helloworld.jar
ADD ./springboot-api.jar  /home/springboot/bin/springboot-api.jar
COPY ./config/*  /home/springboot/config/
COPY ./bin/*  /home/springboot/bin/
# 声明服务运行在8889端口
# EXPOSE 8889
RUN chmod +x /home/springboot/bin/start.sh
# 执行命令
#CMD ["java","-jar","/home/springboot/springboot-helloworld.jar"]
ENTRYPOINT /home/springboot/bin/start.sh
# 指定维护者名称
MAINTAINER liuyucheng liuyucheng02@baidu.com









