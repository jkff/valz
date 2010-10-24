Valz Protocol Specification
===========================

This document describes the network protocol used for communication among Valz
clients and servers. Clients written in different languages may use it to
connect to the Valz server.



Packet Format
-------------

Packet Envelope
~~~~~~~~~~~~~~~

Serialization format: JSON.

.. note::

    A simple JSON type notation is used below.

::

    Packet = {"type": Type, "data": Request | Response}

    Type = "SUBMIT"
         | "GET_VALUE"
         | "LIST_VALS"
         | "REMOVE_VALUE"
         | "SUBMIT_BIG_MAP"
         | "GET_BIG_MAP_CHUNK"
         | "LIST_BIG_MAPS"
         | "REMOVE_BIG_MAP"


.. deprecated:: 0.2
   ``SUBMIT_BIG_MAP`` operation is covered by ``SUBMIT``.


Standard Aggregated Values
~~~~~~~~~~~~~~~~~~~~~~~~~~

Many operations described below use the following ``Aggregate`` and ``Value``
constructs. ::

    Aggregate = {"name": String, "config": Any}

    Value = Any

These aggregate types and their formats are defined in the protocol:

* ``LongSum``
* ``LongMin``
* ``AggregatePair``
* ``SortedMapMerge``

Any client and server **should** support these types. Peers **may** introduce
new aggregate types. If a server does not support aggregates of the requested
type, its behaviour is unspecified.


``LongSum`` aggregate
~~~~~~~~~~~~~~~~~~~~~

Sum of 64-bit signed integer values. ::

    Value = Int

    Aggregate = {"name": "LongSum", "config": null}

.. note::

    Values **should** be in range ``(-2^63, 2^63 - 1)``.


``LongMin`` aggregate
~~~~~~~~~~~~~~~~~~~~~

Minimum of integer values. ::

    Value = Int

    Aggregate = {"name": "LongMin", "config": null}

.. note::

    Values **should** be in range ``(-2^63, 2^63 - 1)``.


``AggregatePair`` aggregate
~~~~~~~~~~~~~~~~~~~~~~~~~~~

A pair of two aggregate values of any type. Aggregation is performed according
to the law::

    (A, C) + (B, D) = (A + C, B + D)

Types::

    Value = {"first": Value, "second": Value}

    Aggregate = {
        "name": "AggregatePair",
        "config": [Aggregate, Aggregate]
    }


``SortedMapMerge`` aggregate
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Sum of maps with string keys. Merging conflicts are resolved by applying the
specified aggregate function. ::

    Value = {String: Any}

    Aggregate = {
        "name": "SortedMapMerge",
        "config": Aggregate
    }


Network Protocol
----------------

The protocol is a custom remote-procedure call protocol on top of ``HTTP POST``.

Application protocol: HTTP.

TCP port: 9125.

A server **may** use a different port number.

HTTP request and response ``Content-Type`` **should** be ``application/json``.
Character encoding: UTF-8. All requests are sent via ``POST`` method.

An example of an actual network exchange:

Client::

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

Server::

    HTTP/1.1 204 No Content

A client **should** support issuing the ``SUBMIT`` request and **may** support
some issuing the operations listed below for a server.

A server **should** support these operations:

* ``SUBMIT``
* ``GET_VALUE``
* ``LIST_VALS``
* ``REMOVE_VALUE``
* ``GET_BIG_MAP_CHUNK``
* ``LIST_BIG_MAPS``
* ``REMOVE_BIG_MAP``


``SUBMIT`` call
~~~~~~~~~~~~~~~

Adds the value to the global value of a val.

Types::

    Request = {"name": String, "aggregate": Aggregate, "value": Value}

Operation definition::

    Request -> void

.. note::

    ``void`` means that the HTTP response contains no body.


``GET_VALUE`` call
~~~~~~~~~~~~~~~~~~

Returns the global value of a val.

Operation definition::

    String -> {"aggregate": Aggregate, "value": Any}


``LIST_VALS`` call
~~~~~~~~~~~~~~~~~~

Returns a list of all the registered val names.

Operation definition::

    void -> [String]


``REMOVE_VALUE`` call
~~~~~~~~~~~~~~~~~~~~~

Removes the global value of a val.

Operation definition::

    String -> void


``GET_BIG_MAP_CHUNK`` call
~~~~~~~~~~~~~~~~~~~~~~~~~~

Returns ``count`` map elements starting from the given key. If the key value is
``null`` then the response starts the first element in the map.

Types::

    Request = {"name": String, "count": Int, "fromKey": String | null}

    Response = {"aggregate": Aggregate, "value": {String: Any}}

Operation definition::

    Request -> Response


``LIST_BIG_MAPS`` call
~~~~~~~~~~~~~~~~~~~~~~

Returns a list of all the registered maps.

Operation definition::

    void -> [String]


``REMOVE_BIG_MAP`` call
~~~~~~~~~~~~~~~~~~~~~~~

Removes the specified map.

Operation definition::

    String -> void

