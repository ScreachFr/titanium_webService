# Titanium web service

## What is it ?

Titanium ws is a web service that makes you able to basicaly send commands to servers via the RCON protocol.
___

## Features

With Titanium ws you can create organizations that regroup members and servers. This way members can send commands to every organization's server without knowing anything more that the server address. 
___

## Usage
The first thing you need to do is to create an account :

`
titanium_ws_path/user/register?username=fancyusername&password=1337pwd&email=noidea@mail.com
`

Result :
```json
{"success":true}
```
Now that you have an account you can get an authentication key :
`titanium_ws_path/user/login/username=fancyusername&password=1337pwd`

Result :
```json
{"success":true,"key":"1f4c655c895b4a238242788b6422038f"}
```
Let's create an organization now :

`
titanium_ws_path/org/create?key=1f4c655c895b4a238242788b6422038f&name=cypher
`

Result :
```json
{"success":true}
```

Once your organization is created you can add member. To do so you first need to search for user you want to add to your organization : 

`
titanium_ws_path/user/search?key=1f4c655c895b4a238242788b6422038f&query=bff&page=0&size=10
`

Result :
```json
{
    "success":true,
    "result":{
        "size":10,
        "page":0,
        "users":[
            {"id":1,"username":"notmybff"},
            {"id":3,"username":"bff4ever"}
        ]
    }
}
```
__Note__ : Only the organization's owner can do such things.

Now list your organization with :

`
titanium_ws_path/org/list?key=1f4c655c895b4a238242788b6422038f
`

Result :
```json
{
    "success" : true,
    "organizations" : [
        {"id" : 2, "name" : "Organization name 1", "owner" : 3},
        {"id" : 6, "name" : "cypher", "owner" : 6}
    ]
} 
```


You can add member with the following request :

`
titanium_ws_path/org/addmember?key=1f4c655c895b4a238242788b6422038f&idorga=6&iduser=3
`

Result :
```json
{"success":true}
```

Now that everything is setup correctly you can add servers to your organization : 

`titanium_ws_path/org/members/add?key=1f4c655c895b4a238242788b6422038f&idorga=6name=top Server&host=impot.gouv.fr&port=42666&password=t0ps3cr3t`

Result :
```json
{"success":true}
```

__Note__ : Only the organization's owner can do such things.

List your servers with :

`titanium_ws_path/servers/list?key=1f4c655c895b4a238242788b6422038f&idorga=6`

Result :
```json
{
    "success" : true,
    "servers" : [
        {"id" : 5, "name" : "top Server", "address" : "impot.gouv.fr:42666"},
    ]
} 
```

You can finaly execute a command on a server by sending this :

`titanium_ws_path/servers/exec?key=1f4c655c895b4a238242788b6422038f&idserver=5&command=adminbroadcast testbc`

Result :
```JSON
{
    "success" : true,
    "answer" : "[CHAT Broadcast] testbc"
} 
```
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

___
## Contacts and Bug Report
If you don't want to open a thread on Github you can contact me via twitter [@Screach_FR](https://twitter.com/Screach_FR) or discord Screach#9203.