FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY build/libs/league-0.0.1-SNAPSHOT.jar league.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "league.jar"]