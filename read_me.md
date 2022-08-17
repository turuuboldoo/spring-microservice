# spring-microservice

Мэдвэл зохих - 
[What is Spring cloud Netflix Eureka Discovery Service in Microservice Architecture](https://www.behindjava.com/java-spring-eureka/)


Database - Mysql
\
DB client - PhpMyAdmin

### start database
docker-compose up -d

## start server
1. server
2. gateway
3. gallery
4. image

гэсэн дараалалаар main function-уудыг run хийнэ. Бүгд ассан бол [http://localhost:8080/eureka](http://localhost:8080/eureka) холбоосоор орж 
server-т холбогдсон service-уудыг шалгаарай.

Active URL

[phpMyAdmin](http://localhost:8089)
\
[Server Dashboard](http://localhost:8080/eureka)
\
[Gallery API](http://localhost:8080/api/gallery)

```
Phpmyadmin 
username = root
password = secret
```

## Хэрхэн ажиллад байна вэ?
Server module нь service-үүдыг (энэ тохиолдолд Gallery болон Image module-ууд) бүртэж нэг domain name-р хоорондох харилцах боломжтой болгож байгаа юм.
Харин Gateway module бол аль module (service)-рүү хүсэлт чиглүүлэх болон loadbalancer үүрэг гүйцэтгэнэ. (Gateway service-н application.yml file дээрх тохиргоог харна уу)
Service-үүд ганцаараа бие даан ажиллах чадвартай байх ба өөрийн гэсэн database-тэй байна.
\
[Eureka server](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html)
\
[Eureka client](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)

Даалгавар
1. Image service дээр coroutine api ашиглан database болон api гаргах
2. Gallery service-c Image service-рүү хүсэлт шидэн ирсэн response-той хамт gallery мэдээлэл буцаах api

Даалгавараа хийхдээ өөртийн нэрээр шинэ branch үүсгэн тухан branch дээрээ хийгээрэй.
```
git checkout -b 'your branch name here'
```

### Microservices from Docker
Docker орчноос асаах бол (Start services from docker production mode)
```
git fetch --all
git checkout dockerized
```
