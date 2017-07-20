# Web service name

## URL
```/org/transfer```

## Description
Transfer an organization ownership.

## Method
GET

## Input parameters
`key` Authentication key.

`idorga` Organization you want to transfer.

`iduser` The new owner of the organization.	

## Output type
JSON

## Output example
```JSON
{“success” : true} 
{"errorCode" : 3005, "errorMessage" : "Can't find organization."}
```

## Errors
`See ./Errors.md.`

## Related classes
```service.organization.*```

## Note
Organization ownership is requiered to perform this operation.