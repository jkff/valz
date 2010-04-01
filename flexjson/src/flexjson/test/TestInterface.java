package flexjson.test;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.test.slon.MyClass;
import flexjson.test.slon.MyContainer;
import org.junit.Test;

public class TestInterface {
    @Test
    public void testInterface() {
        MyContainer myContainer = new MyContainer();
        myContainer.set_interface(new MyClass());

        String s = new JSONSerializer()
                .serialize(myContainer);

        MyContainer msg2 = new JSONDeserializer<MyContainer>()
                .use("_interface", MyClass.class)
                .deserialize(s);
    }
}
