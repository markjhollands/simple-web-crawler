# simple-web-crawler

Simple Web Crawler written in Java

## Prerequisites

If building locally, this project requires:
* Java 8
* Apache Maven 3.6.1

If running locally, this project requires:
* Java 8

Other versions of these prerequisites may work, but have not been tested.

If building / running using Docker:
* Docker 

## Building

This project uses Maven for dependency management, compilation and packaging / assembly.

You can build this project on your local machine, or using a Maven docker container.

### 1. Building locally

To build this project locally run:

```bash
mvn clean compile assembly:single
```

This will clean the target output, compile the code and assemble a Jar. Using the `assembly:single` argument means that a Jar will be built with all required dependencies.

### 2. Building in Docker, output locally

To build using the Maven Docker image, and output locally into your workspace, run the following command in your terminal:

```bash
docker run -it --rm --name web-crawler-build -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven:3.6.1-jdk-8 mvn clean compile assembly:single
```

This will output to your local `target/` directory.

### 3. Building in Docker, create Docker image to run

To build the source, and create a Docker image that you can use to crawl run the following Docker build command:

```bash
docker build -t crawler .
```

This will install the built output into a new Docker image called `crawler`.

## Running

As with building, there are many ways you can run the project. 

### Running locally

If you have built to output the Jar file to your local workspace you can run using the following command:

```bash
java -jar target/SimpleWebCrawler-1.0-SNAPSHOT-jar-with-dependencies.jar http://my-site-to-crawl/
```

#### Bash:

If you are running in an environment with bash, you can use the following command as a more simple way to invoke application: 

```bash
bash crawl.sh http://my-site-to-crawl/
```

### Running using Java Docker image

If you do not have Java installed locally, or simply want to use a Docker container to run the application, run the following at your terminal:

```bash
docker run -it --rm --name crawler -v "$(pwd)":/app -w /app openjdk:8-jre-slim bash crawl.sh http://my-site-to-crawl/
```

### Running using crawler Docker image

Finally, if you built using build option 3, creating a Docker image to run the application, run the following command to invoke the application: 

```bash
docker run -it --rm --name crawler crawler http://my-site-to-crawl/
```

## Testing

To allow small, self-contained testing I have created a (very) small and simple site that the crawler can use to crawl.

### Running test site

To use this test site, you will require Docker. To run the test site in a Docker container, run the following command in your terminal:

```bash
docker run --rm --name test-html-site -p 80:80 -v $(pwd)/testhtml/:/usr/share/nginx/html:ro -d nginx
```

This will start your test site. 

#### Alternative Port Number

You may have to use an alternative port number if your Docker host is already using port 80. If you supply an alternative port, remember to specify this in the URL to crawl.

For example to use the port number 8080 run the following command to start the container:

```bash
docker run --rm --name test-html-site -p 8080:80 -v $(pwd)/testhtml/:/usr/share/nginx/html:ro -d nginx
```

You can then access the site at the URL `http://localhost:8080/`

### Access the test site

You can access the test site from your Docker host by using a web browser and navigating to `http://localhost/`.

To invoke the application locally using the test site, run:

```bash
java -jar target/SimpleWebCrawler-1.0-SNAPSHOT-jar-with-dependencies.jar http://localhost/
```

If you are using a Docker container to run the application you cannot use `localhost` to refer to the test site. I recommend using `docker inspect` to find the Docker network IP for the test site container and then passing this into the `crawler` Docker image, for example:

```bash
$ docker ps
CONTAINER ID   IMAGE   COMMAND                  CREATED          STATUS          PORTS                NAMES
ce845892c6cb   nginx   "nginx -g 'daemon of…"   20 minutes ago   Up 20 minutes   0.0.0.0:80->80/tcp   test-html-site
$ docker inspect ce845892c6cb | grep '"IPAddress"' | head -n 1
            "IPAddress": "172.17.0.2",
$ docker run -it --rm --name crawler crawler http://172.17.0.2/
[...output...]
```

### JUnit testing

I have included a JUnit framework with this application, but no tests.

No test currently exists because of the simplicity of the application, however if the application were to grow in the future, for example to include an API, the framework would exist to include unit tests.