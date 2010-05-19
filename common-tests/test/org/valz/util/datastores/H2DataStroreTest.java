package org.valz.util.datastores;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.valz.util.CollectionUtils;
import org.valz.util.aggregates.*;
import org.valz.util.backends.InvalidAggregateException;
import org.valz.util.datastores.H2DataStore;
import org.valz.util.io.IOUtils;
import org.valz.util.keytypes.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.valz.util.CollectionUtils.*;

public class H2DataStroreTest {
    private final String dbname = "h2test";



    private AggregateRegistry aggregateRegistry = null;
    private KeyTypeRegistry keyTypeRegistry = null;
    private H2DataStore dataStore = null;



    private void removeFiles() {
        new File(dbname + ".h2.db").delete();
        new File(dbname + ".lock.db").delete();
        new File(dbname + ".trace.db").delete();
    }

    @Before
    public void setUp() {
        removeFiles();
        aggregateRegistry = AggregateRegistryCreator.create();
        keyTypeRegistry = KeyTypeRegistryCreator.create();
        dataStore = new H2DataStore(dbname, keyTypeRegistry, aggregateRegistry);
    }

    @After
    public void tearDown() {
        IOUtils.closeSilently(dataStore);
        removeFiles();
    }

    @Test
    public void testBasics() throws InvalidAggregateException {
        String name = "var1";

        assertArrayEquals(new String[] {}, dataStore.listVars().toArray());

        dataStore.submit(name, new LongSum(), 1L);
        assertEquals(1L, dataStore.getValue(name).getValue());
        assertEquals(new LongSum(), dataStore.getValue(name).getAggregate());

        assertArrayEquals(new String[] {name}, dataStore.listVars().toArray());

        dataStore.submit(name, new LongSum(), 1L);
        assertEquals(2L, dataStore.getValue(name).getValue());

        dataStore.removeAggregate(name);
        assertArrayEquals(new String[] {}, dataStore.listVars().toArray());
    }

    @Test
    public void testBigMapsSimple() throws InvalidAggregateException {
        String name = "var1";

        KeyType keyType = new KeyString();
        Aggregate aggregate = new LongSum();

        assertArrayEquals(new String[] {}, dataStore.listBigMaps().toArray());

        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap("foo", 1L));
        assertEquals(new LongSum(), dataStore.getBigMapAggregate(name));
        assertEquals(1L, dataStore.getBigMapItem(name, keyType, aggregate, "foo"));
        assertEquals(Collections.singletonMap("foo", 1L), dataStore.getBigMapChunk(name, "", 1).getValue());
    }

    @Test
    public void testBigMapsTwoVars() throws InvalidAggregateException {
        String name = "var1";

        KeyType keyType = new KeyString();
        Aggregate aggregate = new LongSum();

        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap("foo", 1L));
        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap("bar", 1L));
        assertEquals(1L, dataStore.getBigMapItem(name, keyType, aggregate, "foo"));
        assertEquals(1L, dataStore.getBigMapItem(name, keyType, aggregate, "bar"));
        assertEquals(sortedMap(ar("foo", "bar"), ar(1L, 1L)), dataStore.getBigMapChunk(name, "", 100).getValue());

        // check for correct chunk begin
        assertEquals(Collections.singletonMap("foo", 1L), dataStore.getBigMapChunk(name, "foo", 100).getValue());

        // check for correct chunk end
        assertEquals(Collections.singletonMap("bar", 1L), dataStore.getBigMapChunk(name, "", 1).getValue());
    }

    @Test
    public void testBigMapsTwoSubmits() throws InvalidAggregateException {
        String name = "var1";

        KeyType keyType = new KeyString();
        Aggregate aggregate = new LongSum();

        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap("foo", 1L));
        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap("foo", 1L));
        assertEquals(2L, dataStore.getBigMapItem(name, keyType, aggregate, "foo"));
        assertEquals(Collections.singletonMap("foo", 2L), dataStore.getBigMapChunk(name, "", 1).getValue());
    }

    @Test
    public void testBigMapsRemove() throws InvalidAggregateException {
        String name = "var1";
        Aggregate aggregate;

        dataStore.submitBigMap(name, new KeyString(), new LongSum(), Collections.singletonMap("foo", 1L));
        aggregate = dataStore.getBigMapAggregate(name);

        dataStore.removeBigMap(name);
        aggregate = dataStore.getBigMapAggregate(name);

        dataStore.submitBigMap(name, new KeyString(), new LongMin(), Collections.singletonMap("foo", 1L));
        aggregate = dataStore.getBigMapAggregate(name);

        assertEquals(new LongMin(), dataStore.getBigMapAggregate(name));
    }

    @Test
    public void testBigMapsKeyLong() throws InvalidAggregateException {
        String name = "var1";

        KeyType keyType = new KeyLong();
        Aggregate aggregate = new LongSum();

        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap(1L, 1L));
        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap(1L, 1L));

        assertEquals(new LongSum(), dataStore.getBigMapAggregate(name));
        assertEquals(2L, dataStore.getBigMapItem(name, keyType, aggregate, 1L));
    }

    @Test
    public void testBigMapsMultiKey() throws InvalidAggregateException {
        String name = "var1";

        KeyType keyType = new MultiKey(Arrays.asList((KeyType)new KeyString(), new KeyLong()));
        Aggregate aggregate = new LongSum();

        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap(Arrays.asList("foo", 1L), 1L));
        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap(Arrays.asList("foo", 1L), 1L));
        dataStore.submitBigMap(name, keyType, aggregate, Collections.singletonMap(Arrays.asList("foo", 2L), 1L));

        assertEquals(new LongSum(), dataStore.getBigMapAggregate(name));
        assertEquals(2L, dataStore.getBigMapItem(name, keyType, aggregate, Arrays.asList("foo", 1L)));
        assertEquals(1L, dataStore.getBigMapItem(name, keyType, aggregate, Arrays.asList("foo", 2L)));
    }
}
