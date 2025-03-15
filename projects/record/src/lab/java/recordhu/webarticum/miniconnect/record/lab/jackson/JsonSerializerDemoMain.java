package recordhu.webarticum.miniconnect.record.lab.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.custom.CustomValue;

public class JsonSerializerDemoMain {

    public static void main(String[] args) throws JsonProcessingException {
        ImmutableMap<Integer, Object> data = ImmutableMap.of(
                3, "alma",
                5, ImmutableMap.of(
                        "a", "b",
                        "c", "d"),
                9, ImmutableList.of(34, 54, 76, 93, 4),
                13, new CustomValue("custom"));
        CustomValue customValue = new CustomValue(data);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(customValue));
    }

}
