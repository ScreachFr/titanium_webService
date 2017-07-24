# List Serverq

## URL
```/servers/list```

## Description
List every servers owned by an organization.

## Method
GET

## Input parameters
`key` Authentication key.

`idorga` Organization id.

## Output type
JSON

## Output example
```JSON
{
    "success" : true,
    "servers" : [
        {"id" : 4, "name" : "Server name 1", "address" : "localhost:80"},
        {"id" : 5, "name" : "Server name 2", "address" : "localhost:80"},
        {"id" : 15, "name" : "Server name 3", "address" : "localhost:80"}
    ]
} 
{"errorCode" : 3002, "errorMessage" : "Permission denied (membership required)."}
```

## Errors
`See Errors.md`

## Related classes
```service.servers.*```

## Note
Organization membership is required to perform this operation.