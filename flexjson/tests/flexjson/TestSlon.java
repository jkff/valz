package flexjson;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.forInterface.MyClass;
import flexjson.forInterface.MyContainer;
import flexjson.forPrivateFields.Foo;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSlon {
    @Test
    public void testInterface() {
        // serialize and deserialize interface field with real class

        MyContainer myContainer = new MyContainer();
        myContainer.set_interface(new MyClass());

        String s = new JSONSerializer()
                .serialize(myContainer);

        MyContainer msg2 = new JSONDeserializer<MyContainer>()
                .use("_interface", MyClass.class)
                .deserialize(s);
    }

    @Test
    public void testPrivate() {
        Foo foo = new Foo(3);
        foo.y = 5;

        String s = new JSONSerializer()
                .serialize(foo);

        Foo msg2 = new JSONDeserializer<Foo>()
                .deserialize(s);

        assertEquals(foo, msg2);
    }
}
