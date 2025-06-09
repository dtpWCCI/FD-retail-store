FROM eclipse-temurin:17-jdk

# Install netcat for TCP port checking
RUN apt-get update && apt-get install -y netcat-openbsd

# Set working directory
WORKDIR /app

# Copy application JAR and wait-for-it script
COPY target/ecommerce-backend-1.0.0.jar app.jar
COPY wait-for-mysql.sh /app/wait-for-it.sh

# Make the wait script executable
RUN chmod +x /app/wait-for-it.sh

# Expose the backend port
EXPOSE 8080

# Wait for MySQL before starting the app
ENTRYPOINT ["/app/wait-for-it.sh", "mysql-db", "3306", "--", "java", "-jar", "app.jar"]

