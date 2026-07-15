# ---- Stage 1 : Build ----
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Copie d'abord uniquement le pom.xml pour profiter du cache Docker sur les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie le reste du code source
COPY src ./src

# Build le jar (skip les tests ici, la CI les lancera séparément)
RUN mvn clean package -DskipTests -B

# ---- Stage 2 : Runtime minimal ----
FROM eclipse-temurin:21-jre-alpine

# Mise à jour des paquets Alpine (corrige les CVE connues du système de base)
RUN apk update && apk upgrade --no-cache

# Utilisateur non-root (bonne pratique sécurité)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copie uniquement le jar compilé depuis le stage builder
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
