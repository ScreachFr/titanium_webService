# Register

## URL
```/user/register```

## Description
Register service.

## Input parameters
`username` 

`password` 

`email`
	

## Output type
JSON

## Output example
```JSON
{“success” : true} 
{"errorCode" : 2001, "errorMessage" : "Username is already in use"}
```

## Errors
+ 2001 : (Not a space odyssey) Username is already in use.
+ 2002 : Email is already in use.

## Related classes
```service.register.*```

## Note
none