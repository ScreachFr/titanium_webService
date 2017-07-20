# List Orgnaization Members

## URL
```/org/listmembers```

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
{“success” : true,
    "1" : "Member name 1",
    "5" : "Member name 2",
    "48" : "Member name 3",
    "21" : "Member name 4"
} 
{"errorCode" : 1, "errorMessage" : "Error description"}
```

## Errors
`See ./Errors.md.`

## Related classes
```service.organization.*```

## Note
Organization ownership is requiered to perform this operation.