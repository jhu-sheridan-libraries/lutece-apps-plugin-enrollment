# lutece-enroll-plugin [plugin-enroll]


## Test

To run the tests, we need to spin up a mysql instance in a docker container. The following `docker-compose.yml` 
accomplishes this: 

```
version: '2'

services:
    mysql:
      container_name: mysql-test
      image: mysql/mysql-server:5.6
      ports:
        - "3306:3306"
      env_file: .env
      volumes:
        - db:/var/lib/mysql

volumes:
    db:
      driver: local
```

along with this `.env`file"

```
MYSQL_DATABASE=lutece
MYSQL_USER=lutece
MYSQL_PASSWORD=motdepasse
MYSQL_ROOT_HOST=%
```

once this instance is running, the following command (run in the top directory for the enroll plugin) will execute the tests:

```
mvn clean lutece:exploded antrun:run test \
-Dlutece-test-mysql \
-Dlutece-antrun-db-user=lutece \
-Dlutece-antrun-db-password=motdepasse \
-Dlutece-antrun-db-name=lutece \
-Dlutece-antrun-db-host=127.0.0.1
```