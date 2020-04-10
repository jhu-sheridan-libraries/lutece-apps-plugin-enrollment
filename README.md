# lutece-enroll-plugin [plugin-enroll]

## Use

This Lutece plugin provides functionality for specifying a list of projects, and for people to sign up for them.

To use this plugin with Lutece, one needs to make sure it is installed somewhere `maven` can find it, and mention it in 
the Lutece site's `pom` file.  

## Test

To run the tests, we need to spin up a mysql instance in a docker container.  To do this, in the top level directory for 
this plugin, run `docker-compse up` . Then, execute the following command: 
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

```

# Student Contributors
**@author** ehsia1 (github)

**@author** Sofya Freyman    
  
**@author** Danielle Currey

**@author** kcaruso4 (github)

