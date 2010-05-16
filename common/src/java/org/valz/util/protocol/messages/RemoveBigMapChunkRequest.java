package org.valz.util.protocol.messages;

import java.util.Collection;

public class RemoveBigMapChunkRequest {
    public final String name;
    public final Collection<String> keys;

    public RemoveBigMapChunkRequest(String name, Collection<String> keys) {
        this.name = name;
        this.keys = keys;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) ^
                (keys == null ? 0 : keys.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RemoveBigMapChunkRequest)) return false;
        RemoveBigMapChunkRequest that = (RemoveBigMapChunkRequest)o;
        if (this.name == null ?  that.name != null : !this.name.equals(that.name)) return false;
        if (this.keys == null ?  that.keys != null : !this.keys.equals(that.keys)) return false;
        return true;
    }

    @Override
    public String toString() {
        return String.format("[%s]", name);
    }
}