package org.valz.server;

import org.junit.*;
import org.valz.util.aggregates.LongSum;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class H2DataStroreTest {

    private H2DataStore dataStore = null;

    @Before
    public void setUp() {
        String filename = "h2test";
        boolean res = new File(filename + ".h2.db").delete();
        new File(filename + ".lock.db").delete();
        new File(filename + ".trace.db").delete();
        dataStore = new H2DataStore(filename);
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
        String varName = "var2";

        dataStore.createAggregate(varName, new LongSum(), 1L);
        dataStore.setValue(varName, 2);
        assertEquals(2, dataStore.getValue(varName));
    }
}
