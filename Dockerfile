# Stage 1: Build file .war bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và source code để build
COPY pom.xml .
COPY src ./src

# Đóng gói bỏ qua test nếu có
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng trên Apache Tomcat
FROM tomcat:10.1-jdk17-temurin

# Xóa các webapp mặc định của Tomcat để tránh chiếm root
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy file war đã build vào webapps và đổi tên thành ROOT.war để truy cập trực tiếp qua domain gốc
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Mở cổng mặc định của Tomcat
EXPOSE 8080

CMD ["catalina.sh", "run"]
