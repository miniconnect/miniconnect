package hu.webarticum.miniconnect.lang;

import java.util.Iterator;

public interface ReversibleIterable<T> extends Iterable<T> {
    
    public ReversibleIterable<T> reverseOrder();


    public static <T> ReversibleIterable<T> of(Iterable<T> reversed, ReversibleIterable<T> original) {
        return new ReversedIterable<>(reversed, original);
    }
    

    public static class ReversedIterable<T> implements ReversibleIterable<T> {
        
        private final Iterable<T> reversed;
        
        private final ReversibleIterable<T> original;
        
        
        private ReversedIterable(Iterable<T> reversed, ReversibleIterable<T> original) {
            this.reversed = reversed;
            this.original = original;
        }
        

        @Override
        public Iterator<T> iterator() {
            return reversed.iterator();
        }

        @Override
        public ReversibleIterable<T> reverseOrder() {
            return original;
        }
        
    }
    
}
