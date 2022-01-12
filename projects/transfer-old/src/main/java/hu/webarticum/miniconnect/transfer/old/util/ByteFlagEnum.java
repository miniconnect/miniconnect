package hu.webarticum.miniconnect.transfer.old.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ByteFlagEnum {
    
    public byte flag();
    
    
    public static <T extends ByteFlagEnum> T find(T[] members, byte flag) {
        for (T member : members) {
            if (member.flag() == flag) {
                return member;
            }
        }
        
        List<String> memberStrings = Arrays.stream(members)
                .map(ByteFlagEnum::stringify)
                .collect(Collectors.toList());
        throw new IllegalArgumentException(String.format(
                "No member with flag: %#x; members: %s", flag, memberStrings));
    }
    
    public static String stringify(ByteFlagEnum byteFlagEnum) {
        byte flag = byteFlagEnum.flag();
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(byteFlagEnum.getClass().getSimpleName());
        resultBuilder.append("(");
        resultBuilder.append(String.format("%#x", flag));
        if (flag >= 32) {
            resultBuilder.append(String.format("='%c'", flag));
        }
        resultBuilder.append(")");
        return resultBuilder.toString();
    }
    
}
