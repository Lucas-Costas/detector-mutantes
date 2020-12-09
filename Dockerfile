FROM openjdk:11

COPY mutantes-1.6.jar /app.jar

#ENTRYPOINT sh -c "java -Dspring.config.location=file:/config/application.properties -Djava.security.egd=file:/dev/./urandom -jar /app.jar"
ENTRYPOINT sh -c "java -jar /app.jar"