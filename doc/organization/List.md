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
    1 : {
        "name" : "organisation name 1",
        "owner" : "2"
    },
    5 : {
        "name" : "organisation name 2",
        "owner" : "3"
    },
} 

{"errorCode" : -1, "errorMessage" : "Invalid key"}
```

## Errors
+ -3 : Invalid key.

## Related classes
```services.organizations.*```

## Note
```json
1 : {
        "name" : "organisation name 1",
        "owner" : "5"
}
```
`1` is the organization id. This id is unique and shall be used as the server absolute identity.