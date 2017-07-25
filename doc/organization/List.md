# List organizations

## URL
```/org/list```

## Description
List every organization related to the token's owner.

## Method
GET

## Input parameters
`key` Authentication key.

## Output type
JSON

## Output example
```JSON
{
    "success" : true,
    "organizations" : [
        {"id" : 2, "name" : "Organization name 1", "owner" : 3},
        {"id" : 6, "name" : "Organization name 2", "owner" : 6}
    ]
} 

{"errorCode" : -1, "errorMessage" : "Invalid key"}
```

## Errors
+ -3 : Invalid key.

## Related classes
```services.organizations.*```

## Note
None