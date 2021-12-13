package hu.webarticum.miniconnect.rdmsframework.storage.impl.simpletable;

import java.util.Comparator;

import hu.webarticum.miniconnect.util.data.ImmutableList;

public class DefaultComparator implements Comparator<ImmutableList<Object>> {

    @Override
    public int compare(ImmutableList<Object> values1, ImmutableList<Object> values2) {
        int cmp = 0;
        int length1 = values1.size();
        int length2 = values2.size();
        int commonLength = length1 < length2 ? length1 : length2;
        for (int i = 0; i < commonLength; i++) {
            cmp = compareSingle(values1.get(i), values2.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(length1, length2);
    }

    private int compareSingle(Object value1, Object value2) {
        if (value1 instanceof Comparable) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            int cmp = ((Comparable) value1).compareTo(value2);
            return cmp;
        }
        
        return 0;
    }

}
