package hu.webarticum.miniconnect.rdmsframework.storage;

public interface OrderKey {

    public static OrderKey adHoc() {
        return new OrderKey() {};
    }
    
}
