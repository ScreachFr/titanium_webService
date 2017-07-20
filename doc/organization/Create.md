# Create an organization

## URL
```/org/create```

## Description
Creates a new organization.

## Method
GET

## Input parameters
`key` Authentication key.

`name` Organization name.

## Output type
JSON

## Output example
```JSON
{“success” : true} 
{"errorCode" : 30001, "errorMessage" : "This name is already in use."}
```

## Errors
`See ./Errors.md.`

## Related classes
```service.organization.*```

## Note
none