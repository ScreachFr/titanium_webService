# Check Connection

## URL
```/servers/check```

## Description
Check server connection.

## Method
GET

## Input parameters
`key` Authentication key.

`idserver` Server id.

## Output type
JSON

## Output example
```JSON
{
    "success" : true,
}
{"errorCode" : 4001, "errorMessage" : "Duplicated server."}
```

## Errors
`See Errors.md`

## Related classes
```service.servers.*```

## Note
Organization membership is required to perform this operation.