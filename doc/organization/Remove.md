# Remove an Organization

## URL
```/org/remove```

## Description
Remove an organization.

## Method
GET

## Input parameters
`key` Authentication key.

`idorga` Organization id.
	

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