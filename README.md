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

Ассан байгаа URL

[http://localhost:8089](http://localhost:8089)
```
Phpmyadmin 
username = root
password = secret
```
\
[http://localhost:8080/eureka](http://localhost:8080/eureka)
\
[http://localhost:8080/api/gallery](http://localhost:8080/api/gallery)
\
[http://localhost:8080/api/gallery/galleries](http://localhost:8080/api/gallery/galleries)

Даалгавар
1. Image service дээр coroutine api ашиглан database болон api гаргах
2. Gallery service-c Image service-рүү хүсэлт шидэн ирсэн response-той хамт gallery мэдээлэл буцаах api

Dockerfile coming soon.
