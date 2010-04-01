package flexjson;

import flexjson.factories.IntegerObjectFactory;
import flexjson.forInterface.MyClass;
import flexjson.forInterface.MyContainer;
import flexjson.forPrivateFields.Bar;
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
    public void testPrivateFoo() {
        Foo foo = new Foo(3);
        foo.y = 5;

        String s = new JSONSerializer()
                .serialize(foo);

        Foo msg2 = new JSONDeserializer<Foo>()
                .deserialize(s);

        assertEquals(foo, msg2);
    }

    @Test
    public void testPrivateBar() {
        Bar<Integer> bar = new Bar<Integer>(3);
        bar.y = 5;
        bar.setBar(new Bar<Integer>(2));


        String s = new JSONSerializer()
                .serialize(bar);

        Bar msg2 = new JSONDeserializer<Bar>()
                .use("x", new IntegerObjectFactory())
                .use("y", new IntegerObjectFactory())
                .use("bar.x", new IntegerObjectFactory())
                .use("bar.y", new IntegerObjectFactory())
                .deserialize(s);

        assertEquals(bar, msg2);
    }}
