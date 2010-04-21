package flexjson.valz;

import java.util.Iterator;

public class LongSum extends AbstractAggregate<Long> {

    @Override
    public Long reduce(Iterator<Long> stream) {
        long res = 0;
        while (stream.hasNext()) {
            res += stream.next();
        }
        return res;
    }

    @Override
    public Long reduce(Long item1, Long item2) {
        return item1 + item2;
    }
}