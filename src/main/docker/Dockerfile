FROM java:8
VOLUME /tmp
WORKDIR /opt
ADD rss-storage-1.5.jar /opt/rss-storage-1.5.jar
CMD ["java","-Xms128m", "-Xmx256m", "-jar", "rss-storage-1.5.jar"]
