FROM sb-java
ENV TZ=Asia/Yekaterinburg
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
WORKDIR /opt
ADD rss-storage.jar /opt/rss-storage.jar
ENTRYPOINT ["java","-Xms32m", "-Xmx64m", "-Dfile.encoding=UTF-8", "-Djava.security.egd=file:/dev/./urandom", "-jar", "rss-storage.jar"]
