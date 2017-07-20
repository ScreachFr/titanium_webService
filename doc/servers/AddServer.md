# Add a Server

## URL
```/servers/add```

## Description
Add a server to the list of available organization servers.

## Method
GET

## Input parameters
`key` Authentication key.

`idorga` Organization id.

`name` Name of the server. Note that this value is for display only, it won't impact anything.

`host` Server address.

`port` Server port.

`password`	Server password.

## Output type
JSON

## Output example
```JSON
{“success” : true} 
{"errorCode" : 4001, "errorMessage" : "Duplicated server."}
```

## Errors
`See Errors.md`

## Related classes
```service.servers.*```

## Note
Organization ownership is required to perform this operation.