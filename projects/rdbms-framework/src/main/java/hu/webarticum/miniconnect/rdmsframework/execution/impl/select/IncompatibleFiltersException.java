package hu.webarticum.miniconnect.rdmsframework.execution.impl.select;

class IncompatibleFiltersException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    
    public IncompatibleFiltersException(Object existingFilterValue, Object newFilterValue) {
        super(
                "Can not merge incompatible filter values: " +
                existingFilterValue + " (" + existingFilterValue.getClass() + ") +" +
                newFilterValue + " (" + newFilterValue.getClass() + ")");
    }
    
}