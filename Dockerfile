FROM openjdk:jre-alpine
ENV TZ=Asia/Yekaterinburg
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN apk update && \
  apk add ca-certificates wget && \
  update-ca-certificates && \
  wget -O /usr/local/bin/dumb-init https://github.com/Yelp/dumb-init/releases/download/v1.2.2/dumb-init_1.2.2_amd64 && \
  chmod +x /usr/local/bin/dumb-init
VOLUME /tmp
WORKDIR /opt
ADD build/libs/rss-storage-1.10.jar /opt/rss-storage.jar
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/dumb-init", "--"]
CMD ["java", "-Xmx128m", "-Xss1m", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "rss-storage.jar"]
