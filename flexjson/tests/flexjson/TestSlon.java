package flexjson;

import flexjson.forInterface.MyClass;
import flexjson.forInterface.MyContainer;
import flexjson.forInterface.WithObjectField;
import flexjson.valz.LongSum;
import flexjson.valz.RequestMessage;
import flexjson.valz.RequestType;
import flexjson.valz.SubmitRequest;
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

        assertEquals(myContainer, msg2);
    }

    @Test
    public void testWithObjectField() {
        // serialize and deserialize object type field with real class

        WithObjectField withObjectField = new WithObjectField();
        withObjectField.setObj(new MyClass());

        String s = new JSONSerializer()
                .serialize(withObjectField);

        WithObjectField msg2 = new JSONDeserializer<WithObjectField>()
                .use("obj", MyClass.class)
                .deserialize(s);

        assertEquals(withObjectField, msg2);
    }


    @Test
    public void testValz() {
        SubmitRequest<Long> submitRequest = new SubmitRequest<Long>("foo", new LongSum(), 1L);
        RequestMessage msg = new RequestMessage(RequestType.SUBMIT, submitRequest);


        String s = new JSONSerializer()
                .serialize(msg);

        RequestMessage msg2 = new JSONDeserializer<RequestMessage>()
                .use("data.class", SubmitRequest.class)
                .use("data.aggregate.class", LongSum.class)
                .deserialize(s);

        assertEquals(msg, msg2);
    }

//    @Test
//    public void testPrivateFoo() {
//        Foo foo = new Foo(3);
//        foo.y = 5;
//
//        String s = new JSONSerializer()
//                .serialize(foo);
//
//        Foo msg2 = new JSONDeserializer<Foo>()
//                .deserialize(s);
//
//        assertEquals(foo, msg2);
//    }

//    @Test
//    public void testPrivateBar() {
//        Bar<Integer> bar = new Bar<Integer>(3);
//        bar.y = 5;
//        bar.setBar(new Bar<Integer>(2));
//
//
//        String s = new JSONSerializer()
//                .serialize(bar);
//
//        Bar msg2 = new JSONDeserializer<Bar>()
//                .use("x", new IntegerObjectFactory())
//                .use("y", new IntegerObjectFactory())
//                .use("bar.x", new IntegerObjectFactory())
//                .use("bar.y", new IntegerObjectFactory())
//                .deserialize(s);
//
//        assertEquals(bar, msg2);
//    }
}
