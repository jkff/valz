package org.valz.server;

import org.junit.*;
import org.valz.util.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.io.IOUtils;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class H2DataStroreTest {
    private final String dbname = "h2test";



    private AggregateRegistry registry = null;
    private H2DataStore dataStore = null;

    

    private void removeFiles() {
        new File(dbname + ".h2.db").delete();
        new File(dbname + ".lock.db").delete();
        new File(dbname + ".trace.db").delete();
    }

    @Before
    public void setUp() {
        removeFiles();
        registry = new AggregateRegistry();
        registry.register(LongSum.NAME, new LongSum.ConfigParser());
        dataStore = new H2DataStore(dbname, registry);
    }

    @After
    public void tearDown() {
        IOUtils.closeSilently(dataStore);
        removeFiles();
    }

    @Test
    public void testBasics() {
        String varName = "var1";

        assertArrayEquals(new String[] {}, dataStore.listVars().toArray());

        dataStore.createAggregate(varName, new LongSum(), 1L);
        assertEquals(1, dataStore.getValue(varName));
        assertEquals(new LongSum(), dataStore.getAggregate(varName));

        assertArrayEquals(new String[] {varName}, dataStore.listVars().toArray());


        dataStore.setValue(varName, 2);
        assertEquals(2, dataStore.getValue(varName));
    }

    @Test
    public void testSet() {
        String varName = "var1";

        dataStore.createAggregate(varName, new LongSum(), 1L);
        dataStore.setValue(varName, 2);
        assertEquals(2, dataStore.getValue(varName));
    }
}
