package hu.webarticum.miniconnect.record.customvalue;

public enum MetaType {

    STANDARD('!'),
    
    ANY('?'),
    
    ARRAY('A'),

    TUPLE('T'),
    
    STRUCT('S'),
    
    ;
    
    
    private final char flag;
    
    
    private MetaType(char flag) {
        this.flag = flag;
    }
    
    public static MetaType ofFlag(char flag) {
        for (MetaType metaType : MetaType.values()) {
            if (metaType.flag == flag) {
                return metaType;
            }
        }
        throw new IllegalArgumentException("No meta type with flag: " + flag);
    }
    
    
    public char flag() {
        return flag;
    }
    
}
