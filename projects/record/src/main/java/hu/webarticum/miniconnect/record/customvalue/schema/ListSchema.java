package hu.webarticum.miniconnect.record.customvalue.schema;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import hu.webarticum.miniconnect.lang.ImmutableList;

public class ListSchema implements Schema {
    
    public static final byte FLAG = (byte) 'L';
    
    
    private final Schema itemSchema;


    public ListSchema(Schema itemSchema) {
        this.itemSchema = itemSchema;
    }
    

    public static ListSchema readMainFrom(InputStream in) {
        Schema itemSchema = Schema.readFrom(in);
        return new ListSchema(itemSchema);
    }

    
    @Override
    public void writeTo(OutputStream out) {
        StreamUtil.write(out, FLAG);
        itemSchema.writeTo(out);
    }

    @Override
    public Object readValueFrom(InputStream in) {
        int listSize = StreamUtil.readInt(in);
        List<Object> resultBuilder = new ArrayList<>(listSize);
        for (int i = 0; i < listSize; i++) {
            Object item = itemSchema.readValueFrom(in);
            resultBuilder.add(item);
        }
        return ImmutableList.fromCollection(resultBuilder);
    }

    @Override
    public void writeValueTo(Object value, OutputStream out) {
        @SuppressWarnings("unchecked")
        ImmutableList<Object> listValue = (ImmutableList<Object>) value;
        StreamUtil.writeInt(out, listValue.size());
        for (Object item : listValue) {
            itemSchema.writeValueTo(item, out);
        }
    }

}
