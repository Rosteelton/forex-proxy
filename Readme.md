# Currency exchange proxy #

## How to run

1. Run one-frame container

```
docker run -p 8090:8080 paidyinc/one-frame
```

2. Run forex-app

```sbt run```

Server will be started at localhost:8080

3. Execute GET request

```
curl --location 'localhost:8080/rates?from=USD&to=JPY'
```

## Design and assumptions

1. Calculating requests capacity.

- One-Frame service handles: 1000 rpd
- Forex app should handle: >= 10000 rpd
- Rate pair TTL: 5 minutes

Number of incoming requests is more than available in the one-frame service.
Conclusion: we need to use cache and retrieve all available pairs in one request.

Maximum request to one-frame service per day = 24H * 12 rph (60 min / 5min = 12) = 288

2. Libraries and technologies choices.

- sttp library for one-frame service client
- cats.Ref as internal cache
- enumeratum for a safer way to handle the Currency class
- weaver test framework to test code based on cats-effect

3. What was done

- Used enumeratum for safe converters String <-> Currency
- Implemented an sttp client for the one-frame service
- Rewrote the program logic to get all pairs in 1 request
- Used cache to avoid fetching new data if 5 minutes have not yet elapsed.
- Improved error handling - now detailed error messages are visible in the API response.
- Added validation to currencies for incoming requests.
- Added test for pair generator, cache and program

4. What can be improved

- Add openApi specification
- Add auth 
- Add tests to ensure that the 1000-request limit will not be exceeded (provide a custom Clock instance and move the current time after every request).