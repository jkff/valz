package org.valz.util.aggregates;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.SortedMap;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import static org.valz.util.CollectionUtils.*;

public class SortedMapMergeTest {

    private AggregateRegistry registry = null;

    @Before
    public void setUp() {
        registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());
        registry.register(SortedMapMerge.NAME, new SortedMapMerge.ConfigFormatter(registry));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReduceDifferentKeys() {
        SortedMapMerge<Long> aggregate = new SortedMapMerge<Long>(new LongSum());
        SortedMap<String, Long> map1 = sortedMap(ar("one"), ar(1L));
        SortedMap<String, Long> map2 = sortedMap(ar("two"), ar(2L));

        SortedMap<String, Long> res = aggregate.reduce(map1, map2);
        assertEquals(2, res.size());
        assertEquals((Long)1L, res.get("one"));
        assertEquals((Long)2L, res.get("two"));
    }

    @Test
    public void testReduceSameKeys() {
        SortedMapMerge<Long> aggregate = new SortedMapMerge<Long>(new LongSum());
        SortedMap<String, Long> map1 = sortedMap(ar("two"), ar(2L));
        SortedMap<String, Long> map2 = sortedMap(ar("two"), ar(2L));

        SortedMap<String, Long> res = aggregate.reduce(map1, map2);
        assertEquals(1, res.size());
        assertEquals((Long)4L, res.get("two"));
    }
}
