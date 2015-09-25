FROM java:8
VOLUME /tmp
WORKDIR /opt
ADD rss-storage.jar /opt/rss-storage.jar
ENTRYPOINT ["java","-Xms128m", "-Xmx256m", "-jar", "rss-storage.jar"]
