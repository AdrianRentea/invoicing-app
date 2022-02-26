FROM openjdk:17

COPY suppliers /suppliers
RUN mkdir -p /jars
COPY target /jars
COPY application.yaml /jars

# set default dir so that next commands executes in /home/app dir
WORKDIR /jars

# will execute npm install in /home/app because of WORKDIR
#RUN npm install

# no need for /home/app/server.js because of WORKDIR
CMD ["/usr/bin/java", "-jar", "-Dspring.config.location=/jars/application.yaml","/jars/generate-invoice-0.0.1-SNAPSHOT.jar"]

