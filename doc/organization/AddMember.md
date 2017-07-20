# Add a Member to Organization

## URL
```/org/addmember```

## Description
Add an user to the organization. This way, the user will be able to access every servers listed in this organization.

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