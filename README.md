# lutece-enroll-plugin [plugin-enroll]

## Use

This Lutece plugin provides functionality for specifying a list of projects, and for people to sign up for them.

To use this plugin with Lutece, one needs to make sure it is installed somewhere `maven` can find it, and mention it in 
the Lutece site's `pom` file.  

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

## Installation

Installation can be accomplished by appending the `install` goal after the `test` goal in the above `mvn` command. 
To install without testing, a simple `mvn clean install -Dmaven.test.skip` should suffice.
