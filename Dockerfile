FROM openjdk:8-jdk-alpine
MAINTAINER Datawire Inc, <dev@datawire.io>

LABEL PROJECT_REPO_URL         = "git@github.com:/plombardi89/vertx-pgnotify" \
      PROJECT_REPO_BROWSER_URL = "https://github.com/plombardi89/pgnotify" \
      PROJECT_LICENSE          = "https://github.com/plombardi89/pgnotify/LICENSE" \
      VENDOR                   = "Datawire Inc." \
      VENDOR_URL               = "https://datawire.io/"

WORKDIR /opt/pgnotify
COPY    . ./

RUN apk --no-cache add --virtual .build-deps \
        bash \
        libstdc++ \
    && ./gradlew shadowJar \
    && apk del .build-deps \
    && mv build/libs/pgnotify*.jar ./pgnotify.jar \
    && chmod +x entrypoint.sh \
    && rm -rf src build gradle .vertx .gradle .git .gitignore .dockerignore Dockerfile

EXPOSE 5000
ENTRYPOINT ["./entrypoint.sh"]
