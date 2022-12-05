package hu.webarticum.miniconnect.rdmsframework.storage.impl.compound;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.lang.LargeInteger;
import hu.webarticum.miniconnect.rdmsframework.storage.TableSelection;
import hu.webarticum.miniconnect.util.ChainedIterator;

public class DisjunctUnionTableSelection implements TableSelection {
    
    private TableSelection[] selections;
    

    private DisjunctUnionTableSelection(TableSelection[] selections) {
        this.selections = selections;
    }
    
    public static DisjunctUnionTableSelection of(TableSelection... selections) {
        return new DisjunctUnionTableSelection(Arrays.copyOf(selections, selections.length));
    }

    public static DisjunctUnionTableSelection ofCollection(Collection<TableSelection> selections) {
        return new DisjunctUnionTableSelection(selections.toArray(new TableSelection[selections.size()]));
    }
    
    
    @Override
    public Iterator<LargeInteger> iterator() {
        return ChainedIterator.allOf(ImmutableList.of(selections).map(Iterable::iterator));
    }

    @Override
    public boolean containsRow(LargeInteger rowIndex) {
        for (TableSelection selection : selections) {
            if (selection.containsRow(rowIndex)) {
                return true;
            }
        }
        return false;
    }

}
