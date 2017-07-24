# List Orgnaization Members

## URL
```/org/members/list```

## Description
List every member of an organization.

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
    "members" : [
        {"id" : 5, "username" : "Member name 1"},
        {"id" : 24, "username" : "Member name 2"},
        {"id" : 186, "username" : "Member name 3"},
        {"id" : 2455, "username" : "Member name 4"}
    ]
} 
{"errorCode" : 3001, "errorMessage" : "Permission denied (ownership required)."}
```

## Errors
`See ./Errors.md.`

## Related classes
```service.organization.*```

## Note
Organization ownership is requiered to perform this operation.