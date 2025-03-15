package hu.webarticum.miniconnect.messenger.message.request;

import hu.webarticum.miniconnect.lang.ToStringBuilder;

public class SessionInitRequest implements Request {

    private static final long serialVersionUID = 3228910560308711466L;
    

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
