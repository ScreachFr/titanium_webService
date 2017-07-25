# Titanium web service

## What is it ?

Titanium ws is a web service that makes you able to basicaly send commands to servers via the RCON protocol.
___

## Features

With Titanium ws you can create organizations that regroup members and servers. This way members can send commands to every organization's server without knowing anything more that the server address. 
___

## Usage
The first thing you need to do is to create an account :
```
titanium_ws_path/user/register?username=fancyusername&password=1337pwd&email=noidea@mail.com
answer :
{"success":true}
```
Now that you have an account you can get an authentication key :
```
titanium_ws_path/user/login/username=fancyusername&password=1337pwd
{"success":true,"key":"1f4c655c895b4a238242788b6422038f"}
```
Let's create an organization now :
```
titanium_ws_path/org/create?key=1f4c655c895b4a238242788b6422038f&name=cypher
{"success":true}
```

// TODO add member research 

// TODO add server

// TODO command execution
___

## Installation

### Softwares
To install Titanium ws you will need to have installed the following softwares :

+ Java 8 or more.
+ Tomcat 8 or more (haven't tested any previous versions).
+ Mysql 5.5.55 (haven't tested any other version).

### Tomcat
Once you have a proper Tomcat installation, you can access the manager gui. Just use the `WAR file to deploy` form to deploy the web service.

### Configuration file

Change every value that needs to be changed. I strongly recommand using a locally installed database for security reasons.
You can always enable ssl but keep in mind that your mysql installation must be configured to accept SSL.
You can also test your database connection by calling the service `/diag/database`. 

#### Linux
Put `config.json` (available in `installation/`) in `/opt/titanium_ws/`.

### Windows
Well lets say it's not supported yet.