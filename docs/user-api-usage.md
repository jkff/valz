A doctest in Jython
===================

TBD


valz Setup
----------

Set up paths to valz' client JAR files:

    >>> import sys
    >>> jars = [
    ...     'client/build/valz-client.jar',
    ...     'common/build/valz-common.jar',
    ...     'common/lib/log4j/log4j-1.2.15.jar',
    ...     'common/lib/json/jsontools-core-1.7.jar',
    ... ]
    >>> for jar in jars:
    ...    sys.path.append(jar)

so we can, for example, import `Valz` class:

    >>> from org.valz.client import Valz

Some stupid assertion:

    >>> Valz.__name__
    'Valz'


Simple Usage
------------

Trying to do anything useful...

    >>> from java.lang import Long
    >>> from org.valz.backends import DatastoreBackend
    >>> from org.valz.datastores.memory import MemoryDataStore
    >>> from org.valz.model import SortedMapMerge, LongSum

Create a memory-based store:

    >>> backend = DatastoreBackend(MemoryDataStore())
    >>> Valz.init(backend)

Register some `Val`:

    >>> val = Valz.register('foo', LongSum())

Use `val` for reporting:

    >>> val.submit(Long(10))
    >>> val.submit(Long(32))

Read the aggregated value:

    >>> sample = backend.getValue('foo')
    >>> sample.getAggregate().getName()
    u'LongSum'
    >>> sample.getValue()
    42L

