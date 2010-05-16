package org.valz.util.datastores;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.valz.util.aggregates.AggregateRegistry;
import org.valz.util.aggregates.LongSum;
import org.valz.util.datastores.H2DataStore;
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
        registry.register(LongSum.NAME, new LongSum.ConfigFormatter());
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

        dataStore.submit(varName, new LongSum(), 1L);
        assertEquals(1L, dataStore.getValue(varName).getValue());
        assertEquals(new LongSum(), dataStore.getAggregate(varName));

        assertArrayEquals(new String[] {varName}, dataStore.listVars().toArray());


        dataStore.modify(varName, new Calculator<Long>(){
            public Long calculate(Long value) {
                return value + 1L;
            }
        });
        assertEquals(2L, dataStore.getValue(varName).getValue());
    }
}