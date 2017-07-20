# Register

## URL
```/user/register```

## Description
Register service.

## Method
GET

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
+ 2003 : Username is too short.
+ 2004 : Password is too short.
+ 2005 : Username is too long.
+ 2006 : Password is too long.
+ 2007 : Invalid email.


## Related classes
```service.auth.*```

## Note
none