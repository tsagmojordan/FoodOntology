
FROM maven:latest AS build
WORKDIR /app

# Copier seulement le fichier pom.xml et les fichiers de dépendances
COPY pom.xml .
COPY src ./src

# Installer les dépendances
RUN mvn dependency:go-offline

# Copier le reste du code source
COPY src ./src

# Construire l'application
RUN mvn clean package -DskipTests

# Étape 2 : Créer l'image pour l'exécution
FROM openjdk:22-jdk
WORKDIR /app

# Copier le JAR généré (en utilisant un wildcard)
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]