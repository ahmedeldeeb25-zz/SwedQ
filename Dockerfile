FROM openjdk:8
MAINTAINER ahmedeldeeb

VOLUME /tmp
EXPOSE 8080

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh 

ADD /target/testpro-0.0.1-SNAPSHOT.war testpro-0.0.1-SNAPSHOT.war

ENTRYPOINT ["./entrypoint.sh"]