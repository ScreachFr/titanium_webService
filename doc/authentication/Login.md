# Login

## URL
```/user/login```

## Description
Login service.

## Method
GET

## Input parameters
`username` 

`password` 
	

## Output type
JSON

## Output example
```JSON
{"success" : true, "key" : "efknsldfn1dq35de1sedsef1"} 
{"errorCode" : 1001, "errorMessage" : "Wrong username/password"}
```

## Errors
+ 1001 : Wrong username/password. The username does not exists or the password is wrong.

## Related classes
```service.auth.*```

## Note
none