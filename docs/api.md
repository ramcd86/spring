# API Documentation

### Endpoints:

### `/register`

Accepts the following example JSON:

```
{
"userName": "test1",
"firstName": "john",
"lastName": "doe",
"email": "test@test.com",
"password": "test1",
"dob": "01/01/1960",
"avatar": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCABkAFoDASIAAhEBAxEB/8QAHAAAAgIDAQEAAAAAAAAAAAAAAAcEBgQFAgMI/8QANxAAAQMDAwIDBwMDBQAAAAAAAQIDBAUGEQcSIRMxQQgiMlFhcYGRobHRFUKxwSMkMlJik8EjNDVDM0NTc5OisrPC0f/EABkBAQEBAQEBAAAAAAAAAAAAAAABAgMEBf/EACQRAAICAQMEAgIDAAAAAAAAAAABAgMRBDEGEhMxUWGBoSIyQVJxoQcVM//EABwBAQACAgMAAAAAAAAAAAAAAAABAgMRBCEAEv/EACURAQEBAQAAAAAAAAAAAAAAAAEAAhEx/9oADAMBAAIRAxEAPwC6KKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA//2Q=="
}
```

Successful registrations return `User inserted.`, failures return an array of reasons why it failed, i.e. empty fields.

### `/login`

Accepts the following example JSON:

```
{
    "email": "test@test.com",
    "password": "test1"
}
```

Returns either `Error: 500` or the following JSON object:

```
{
    "userName": "test1",
    "firstName": "john",
    "lastName": "doe",
    "email": "test@test.com",
    "dob": "01/01/1960",
    "registrationDate": "2023-04-20",
    "userAvatar": "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCABkAFoDASIAAhEBAxEB/8QAHAAAAgIDAQEAAAAAAAAAAAAAAAcEBgQFAgMI/8QANxAAAQMDAwIDBwMDBQAAAAAAAQIDBAUGEQcSIRMxQQgiMlFhcYGRobHRFUKxwSMkMlJik8EjNDVDM0NTc5OisrPC0f/EABkBAQEBAQEBAAAAAAAAAAAAAAABAgMEBf/EACQRAAICAQMEAgIDAAAAAAAAAAABAgMRBDEGEhMxUWGBoSIyQVJxoQcVM//EABwBAQACAgMAAAAAAAAAAAAAAAABAgMRBCEAEv/EACURAQEBAQAAAAAAAAAAAAAAAAEAAhEx/9oADAMBAAIRAxEAPwC6KKACiiigAooooAKKKKACiiigAooooAKKKKACiiigAooooA//2Q==",
    "authKey": "S9npJ7qLsCbWytzMKhLhSkMmFyurJBFKJVOfyHY9mIk="
}
```

### `/auth`

Accepts the following example JSON:

```
{
    "authKey": "S9npJ7qLsCbWytzMKhLhSkMmFyurJBFKJVOfyHY9mIk="
}
```

Returns either `true` if the user's authentication key is valid or `false` if it isn't.
