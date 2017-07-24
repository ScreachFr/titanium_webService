# Add a Member to Organization

## URL
```/org/members/remove```

## Description
Remove an user from the organization.

## Method
GET

## Input parameters
`key` Authentication key.

`idorga` Organization id.

`iduser` New member id.
	

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