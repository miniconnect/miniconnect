package hu.webarticum.miniconnect.jdbc.provider;

public enum TransactionIsolationLevel {
    
    NONE,
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE,

}
