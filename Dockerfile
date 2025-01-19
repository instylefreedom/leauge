FROM openjdk:17-alpine
VOLUME /tmp
COPY build/libs/league-0.0.1-SNAPSHOT.jar league.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "league.jar"]