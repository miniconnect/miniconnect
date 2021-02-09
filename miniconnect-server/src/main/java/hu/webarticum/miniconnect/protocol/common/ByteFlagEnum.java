package hu.webarticum.miniconnect.protocol.common;

public interface ByteFlagEnum {
    
    public byte flag();
    
    
    public static <T extends ByteFlagEnum> T find(T[] members, byte flag) {
        for (T member : members) {
            if (member.flag() == flag) {
                return member;
            }
        }
        
        throw new IllegalArgumentException(String.format(
                "No member with flag: %#x", flag));
    }
    
}
