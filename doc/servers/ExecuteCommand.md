# Execute a command.

## URL
```/servers/exec```

## Description
Execute a command on a server.

## Method
GET

## Input parameters
`key` Authentication key.

`idserver` Server id.

`command` Command to execute with arguments.

`timeout` How much time time in ms shall the servlet wait for an answer.

## Output type
JSON

## Output example
```JSON
{
    "success" : true,
    "answer" : "[CHAT Broadcast] test"
} 
{"errorCode" : 4001, "errorMessage" : "Duplicated server."}
```

## Errors
`See Errors.md`

## Related classes
```service.servers.*```

## Note
Organization membership is required to perform this operation.