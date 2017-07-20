# Remove a Server

## URL
```/servers/remove```

## Description
Remove a server from the list of available organization servers.

## Method
GET

## Input parameters
`key` Authentication key.

`idserver` Server id.

## Output type
JSON

## Output example
```JSON
{“success” : true} 
{"errorCode" : 3002, "errorMessage" : "Permission denied (ownership required)."}
```

## Errors
`See ./Errors.md`

## Related classes
```service.servers.*```

## Note
Organization ownership is required to perform this operation.