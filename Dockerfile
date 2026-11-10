FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out

RUN javac --add-modules jdk.httpserver -d out mydb/*.java

EXPOSE 10000

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", "out", "mydb.MyDBServer"]
