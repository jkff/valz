Valz Protocol Specification
===========================

This document describes the network protocol used for communication among `valz`
clients and servers. Clients written in different languages may use it to
connect to the `valz` server.


Contents
--------

1. Packet Format
    1. Packet Envelope
    2. Standard Aggregated Values
2. Network Protocol


1. Packet Format
----------------

### 1.1. Packet Envelope

Serialization format: JSON.

Note: A simple JSON type notation is used below.

    Packet = {"type": Type, "data": Request | Response}

    Type = "SUBMIT"
         | "GET_VALUE"
         | "LIST_VALS"
         | "REMOVE_VALUE"
         | "SUBMIT_BIG_MAP"    // Deprecated
         | "GET_BIG_MAP_CHUNK"
         | "LIST_BIG_MAPS"
         | "REMOVE_BIG_MAP"


### 1.2. Standard Aggregated Values

Many operations described below use the following `Aggregate` and `Value`
constructs.

    Aggregate = {"name": String, "config": Any}

    Value = Any

These aggregate types and their formats are defined in the protocol:

* `LongSum`
* `LongMin`
* `AggregatePair`
* `SortedMapMerge`

Any client and server **should** support these types. Peers **may** introduce
new aggregate types. If a server does not support aggregates of the requested
type, its behaviour is unspecified.


#### `LongSum` aggregate

Sum of integer values.

    Value = Int

    Aggregate = {"name": "LongSum", "config": null}

Note: Values **should** be in range `(-2^63, 2^63 - 1)`.


#### `LongMin` aggregate

Minimum of integer values.

    Value = Int

    Aggregate = {"name": "LongMin", "config": null}

Note: Values **should** be in range `(-2^63, 2^63 - 1)`.


#### `AggregatePair` aggregate

A pair of two aggregate values of any type.

    Value = {"first": Value, "second": Value}

    Aggregate = {
        "name": "AggregatePair",
        "config": [Aggregate, Aggregate]
    }


#### `SortedMapMerge` aggregate

Sum of maps with string keys. Merging conflicts are resolved by applying the
specified aggregate function.

    Value = {String: Any}

    Aggregate = {
        "name": "SortedMapMerge",
        "config": Aggregate
    }



2. Network Protocol
-------------------

The protocol is a custom remote-procedure call protocol on top of `HTTP POST`.

Application protocol: HTTP.

TCP port: 9125.

A server **may** use a different port number.

HTTP request and response `Content-Type`: `application/json`. Character
encoding: UTF-8. All requests are sent via `POST` method.

An example of an actual network exchange:

Client:

     POST / HTTP/1.1
     Host: example.com
     Content-Type: application/json

     {
         "type": "SUBMIT",
         "data": {
             "name": "visits",
             "aggregate": {"name": LongSum, "config": null},
             "value": 1
         }
     }

Server:

    HTTP/1.1 204 No Content

A client **should** support the `SUBMIT` operation and **may** support some
operations listed below for a server.

A server **should** support these operations:

* `SUBMIT`
* `GET_VALUE`
* `LIST_VALS`
* `REMOVE_VALUE`
* `GET_BIG_MAP_CHUNK`
* `LIST_BIG_MAPS`
* `REMOVE_BIG_MAP`


### 2.1. `SUBMIT` call

Adds the value to the global value of a val.

Types:

    Request = {"name": String, "aggregate": Aggregate, "value": Value}

Operation definition:

    Request -> void

Note: `void` means that the HTTP response contains no body.


### 2.2. `GET_VALUE` call

Returns the global value of a val.

Operation definition:

    String -> {"aggregate": Aggregate, "value": Any}


### 2.3. `LIST_VALS` call

Returns a list of all the registered val names.

Operation definition:

    void -> [String]


### 2.4. `REMOVE_VALUE` call

Removes the global value of a val.

Operation definition:

    String -> void


### 2.5. `GET_BIG_MAP_CHUNK` call

Returns `count` map elements starting from the given key. If the key value is
`null` then the response starts the first element in the map.

Types:

    Request = {"name": String, "count": Int, "fromKey": String | null}

    Response = {"aggregate": Aggregate, "value": {String: Any}}

Operation definition:

    Request -> Response


### 2.6. `LIST_BIG_MAPS` call

Returns a list of all the registered maps.

Operation definition:

    void -> [String]


### 2.7. `REMOVE_BIG_MAP` call

Removes the specified map.

Operation definition:

    String -> void

