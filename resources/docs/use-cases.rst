Use Cases
=========


Unsorted Use Cases
------------------

* Hit count, referer count partitioned by any string key (user, country,
  URL, category, traffic source, ...)
* Average and variance response time (partitioned by user, backend server, ...)
* Cache hit partitioned by server
* Sum of ads revenue
* Number of bytes sent
* Number of logged in users (*realtime!*)
* Hit count partitioned by day (e.g. 2010-10-24)
* Histograms of response times, traffic, revenue, ...
* Arbitrarily nested maps e.g. hit count by category partitioned by country
* Last N events in the system (only last events are stored, no central storage
  is required, aggregate: ``A + B = take(N, merge_sorted_sequences(A, B))``
  (partitioned ...) (not implemented)
* Bounded random sample weighted by "importance" (specified by user) (the
  aggregate is quite complex and is not implemented yet)
* Bounce rate is ``1 - ReferCount(URL) / HitCount(URL)`` (aggregates: sum, pair)
* Approximate unique users, IPs, sender-receiver (for firewalls), search queries
  count (partitioned ...) (not implemented)
* Approximate quantiles: most frequent queries (not implemented)

The domain of the outermost key may be arbitrarily large (backed by a key-value
store).


Use Case: Logistics
-------------------

The event is ``Package = {Customer, Seller, CurCheckPoint, PrevCheckPoint, Time,
Price, Category, CurStatus, PrevStatus, Source, Desination}``.

* Number of packages on their way between each pair of checkpoints (*realtime!*)
* Frequencies of status transitions partioned by checkpoints
* Average and variance or histograms of delivery times partitioned by
  checkpoints
* Frequency of devlivery of package delivery by category partitioned by
  source-destination pairs

