# Search User

## URL
```/user/search```

## Description
Search for users.

## Method
GET

## Input parameters
`key` Authentication key. 

`query` Research query. 
	
`page` Desired page number.

`size` How many result per page.

## Output type
JSON

## Output example
```JSON
{
    "success" : true,
    "result" : {
        "page" : 5,
        "size" : 5,
        "users" : [
            {
                "id" : 4,
                "username" : "FancyName 1"
            },
            {
                "id" : 15,
                "username" : "FancyName 2"
            },
            {
                "id" : 48,
                "username" : "FancyName 3"
            }
        ]
    }
} 
{"errorCode" : 1001, "errorMessage" : "Invalid key"}
```

## Errors
+ 1001 : Wrong username/password. The username does not exists or the password is wrong.

## Related classes
```service.user.*```

## Note
none