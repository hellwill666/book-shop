FROM openjdk:17.0.2-jdk-slim AS builder

# Install sbt
RUN apt-get update && \
    apt-get install -y curl && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    apt-get update && \
    apt-get install -y sbt

WORKDIR /app
COPY build.sbt .
COPY project/ project/
COPY src/ src/

RUN sbt compile stage

FROM openjdk:17.0.2-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/universal/stage/ .

EXPOSE 8080

CMD ["bin/scala-book-rental_3"]
