package org.valz.util.protocol.messages;

import org.valz.util.Pair;

public class GetBigMapChunkRequest {
    public final String name;
    public final String fromKey;
    public final int count;

    public GetBigMapChunkRequest(String name, String fromKey, int count) {
        this.name = name;
        this.fromKey = fromKey;
        this.count = count;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) ^
                (fromKey == null ? 0 : fromKey.hashCode()) ^
                count;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GetBigMapChunkRequest)) return false;
        GetBigMapChunkRequest that = (GetBigMapChunkRequest)o;
        if (this.name == null ?  that.name != null : !this.name.equals(that.name)) return false;
        if (this.fromKey == null ?  that.fromKey != null : !this.fromKey.equals(that.fromKey)) return false;
        return this.count == that.count;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %d]", name, fromKey, count);
    }
}
