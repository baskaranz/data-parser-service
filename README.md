# data-parser-service

## Problem Statement

A company has a business process that requires processing of text files from external customers.
Some information about the text file:

- Each line of the text file is an individual record and can be processed independently of the
other records in the text file.
- ThesizeofthefilecanrangefromafewKBtoafewGB.
- Here is the format of each line: `[Date in YYYY-MM-DDThh:mm:ssZ Format][space][Email Address][space][Session Id in GUID format]`
For example:
`2020-12-04T11:14:23Z jane.doe@email.com 2f31eb2c-a735-4c91-a122-b3851bc87355`
- The lines are ordered by the Date field in ascending order.
- Sample files can be found here: https://bitbucket.org/ROKT/backend-technical-test

## Implementation
### Language/Framework
	- Play/Scala

### Running the service
- git clone https://github.com/baskaranz/data-parser-service.git
- `sbt clean test` to run the tests
- `sbt run` or `sbt start` to run/start the project in dev/prod mode

### Solution endpoint
GET http://localhost:9000/parse

Sample request payload

```
{
    "filePath": "/Users/baskaran/data/sample1.txt",
    "startDate": "1999-01-01T13:20:05Z",
    "endDate": "2020-01-01T13:20:05Z",
    "offset": 0,
    "limit": 10
}
```
where
- filePath: Local file path of the text file
- startDate: The start date in ISO 8601 format
- endDate: The end date in ISO 8601 format
- offset: The start position of the result for pagination 
- limit: The max no. of the result for pagination (The max default is 100)

Sample response

```
{
    "total_count": 1000,
    "result": [
        {
            "eventTime": "2001-07-14T17:14:40",
            "email": "howard@lebsackprosacco.co.uk",
            "sessionId": "fc5621fa-212b-4750-8606-7dbc21c94f26"
        },
        {
            "eventTime": "2001-07-13T18:38:51",
            "email": "ahmad_cassin@cummingsdamore.ca",
            "sessionId": "11d4ef62-b185-4dfb-87da-1dc4a832e2d0"
        },
        {
            "eventTime": "2001-07-13T03:28:17",
            "email": "haleigh@rowedach.biz",
            "sessionId": "cf89cd22-3b6b-46a9-aad3-dfdf5b776da3"
        },
        {
            "eventTime": "2001-07-12T06:01:06",
            "email": "naomie_lockman@bechtelar.biz",
            "sessionId": "2c2f1ea3-fee5-40b5-a085-92da3845fc4b"
        },
        {
            "eventTime": "2001-07-11T13:16:26",
            "email": "felicia@stroman.name",
            "sessionId": "9a8f2ac8-5a0b-4acd-8fd8-fd9fcbf244c0"
        },
        {
            "eventTime": "2001-07-11T04:43:09",
            "email": "nels@trantow.biz",
            "sessionId": "af87a9ab-83c3-465f-a5d4-d0bde99028c7"
        },
        {
            "eventTime": "2001-07-11T01:16:34",
            "email": "sibyl@hoppe.biz",
            "sessionId": "7efa8a37-7cd9-48f7-8f26-7f7a87329499"
        },
        {
            "eventTime": "2001-07-10T15:09:47",
            "email": "tyrell@whitenitzsche.us",
            "sessionId": "6a2149ce-89fc-4642-8896-5814683a7f43"
        },
        {
            "eventTime": "2001-07-10T11:38:24",
            "email": "daren_jenkins@keeling.co.uk",
            "sessionId": "06d57193-98e8-4754-8693-0c0b728bb948"
        },
        {
            "eventTime": "2001-07-10T06:08:20",
            "email": "kelly.collier@predoviczemlak.biz",
            "sessionId": "a0905eaf-2e09-4cb2-95ac-0d567eb140f2"
        }
    ]
}
```

where
total_count: The total no. of records for the given file (not the paginated size)


## Assumptions:
- The parsed response is paginated with a limit set as 100
- For now only file source is supported, it could be extended to support url source
- Very large files (eg., sample3.txt) are parsed with high latency but without any OOM errors
