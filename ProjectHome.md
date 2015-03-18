## **Don't panic!** See [the documentation](http://jkff.info/valz/) for overview. ##


---


Valz is basically a real-time analogue of Google Sawzall. It allows you to define system-wide "aggregate variables" and emit values into them. This can be used for monitoring the behavior of distributed systems (e.g. simple hit counting, or quantiles of request times, and many more complex things) and is as easy to use as logging statements.
The aggregation laws are like SQL aggregate functions (independent on order of aggregation); this allows to have near-infinite scalability.

Valz is supported by Atlassian's opensource license for [Clover](http://www.atlassian.com/software/clover/overview), the great code coverage tool.