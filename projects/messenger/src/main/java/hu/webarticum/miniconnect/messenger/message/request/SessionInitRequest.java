package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.util.data.ToStringBuilder;

public class SessionInitRequest implements Request {

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SessionInitRequest;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).build();
    }

}
