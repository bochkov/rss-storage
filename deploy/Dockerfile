FROM java:8
ENV TZ=Asia/Yekaterinburg
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
WORKDIR /opt
ADD rss-storage.jar /opt/rss-storage.jar
ENTRYPOINT ["java","-Xms128m", "-Xmx256m", "-jar", "rss-storage.jar"]
