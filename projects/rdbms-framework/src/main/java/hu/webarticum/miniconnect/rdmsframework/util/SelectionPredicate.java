package hu.webarticum.miniconnect.rdmsframework.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.InclusionMode;
import hu.webarticum.miniconnect.rdmsframework.storage.TableIndex.NullsMode;
import hu.webarticum.miniconnect.rdmsframework.storage.impl.simple.MultiComparator;

public class SelectionPredicate implements Predicate<ImmutableList<Object>> {
    
    private final Predicate<ImmutableList<Object>> wrappedPredicate;
    

    public SelectionPredicate(
            ImmutableList<?> from,
            InclusionMode fromInclusionMode,
            ImmutableList<?> to,
            InclusionMode toInclusionMode,
            ImmutableList<NullsMode> nullsModes,
            MultiComparator multiComparator) {
        Predicate<ImmutableList<Object>> predicate = null;
        predicate = applyNullsModes(predicate, nullsModes);
        predicate = applyFrom(predicate, from, fromInclusionMode, multiComparator);
        predicate = applyTo(predicate, to, toInclusionMode, multiComparator);
        predicate = ensureFallback(predicate);
        this.wrappedPredicate = predicate;
    }
    
    private static Predicate<ImmutableList<Object>> applyNullsModes(
            Predicate<ImmutableList<Object>> predicate,
            ImmutableList<NullsMode> nullsModes) {
        Map<Integer, NullsMode> specialNullsModeMap = buildSpecialNullsModeMap(nullsModes);
        return applySpecialNullsModeMap(predicate, specialNullsModeMap);
    }

    private static Predicate<ImmutableList<Object>> applySpecialNullsModeMap(
            Predicate<ImmutableList<Object>> predicate,
            Map<Integer, NullsMode> specialNullsModeMap) {
        
        if (specialNullsModeMap.isEmpty()) {
            return predicate;
        }
        
        Predicate<ImmutableList<Object>> specialNullsModePredicate = v -> xxxxx(v, specialNullsModeMap);
        return predicate != null ? predicate.and(specialNullsModePredicate) : specialNullsModePredicate;
    }

    private static boolean xxxxx(ImmutableList<Object> values, Map<Integer, NullsMode> specialNullsModeMap) {
        for (Map.Entry<Integer, NullsMode> entry : specialNullsModeMap.entrySet()) {
            int i = entry.getKey();
            NullsMode nullsMode = entry.getValue();
            Object value = values.get(i);
            if (nullsMode == NullsMode.NO_NULLS) {
                if (value == null) {
                    return false;
                }
            } else if (nullsMode == NullsMode.NULLS_ONLY) {
                if (value != null) { // NOSONAR merged condition would be hard to understand
                    return false;
                }
            }
        }
        return true;
    }
    
    private static Map<Integer, NullsMode> buildSpecialNullsModeMap(ImmutableList<NullsMode> nullsModes) {
        Map<Integer, NullsMode> specialNullsModeMap = new HashMap<>(nullsModes.size());
        int i = 0;
        for (NullsMode nullsMode : nullsModes) {
            if (nullsMode != NullsMode.WITH_NULLS) {
                specialNullsModeMap.put(i, nullsMode);
            }
            i++;
        }
        return specialNullsModeMap;
    }

    private static Predicate<ImmutableList<Object>> applyFrom(
            Predicate<ImmutableList<Object>> predicate,
            ImmutableList<?> from,
            InclusionMode fromInclusionMode,
            MultiComparator multiComparator) {
        
        if (from == null) {
            return predicate;
        }
        
        @SuppressWarnings("unchecked")
        ImmutableList<Object> fromAsObjects = (ImmutableList<Object>) from;
        Predicate<ImmutableList<Object>> fromPredicate;
        if (fromInclusionMode == InclusionMode.INCLUDE) {
            fromPredicate = v -> multiComparator.compare(v, fromAsObjects) >= 0;
        } else {
            fromPredicate = v -> multiComparator.compare(v, fromAsObjects) > 0;
        }
        return predicate != null ? predicate.and(fromPredicate) : fromPredicate;
    }
    
    private static Predicate<ImmutableList<Object>> applyTo(
            Predicate<ImmutableList<Object>> predicate,
            ImmutableList<?> to,
            InclusionMode toInclusionMode,
            MultiComparator multiComparator) {

        if (to == null) {
            return predicate;
        }
        
        @SuppressWarnings("unchecked")
        ImmutableList<Object> toAsObjects = (ImmutableList<Object>) to;
        Predicate<ImmutableList<Object>> toPredicate;
        if (toInclusionMode == InclusionMode.INCLUDE) {
            toPredicate = v -> multiComparator.compare(v, toAsObjects) <= 0;
        } else {
            toPredicate = v -> multiComparator.compare(v, toAsObjects) < 0;
        }
        return predicate != null ? predicate.and(toPredicate) : toPredicate;
    }
    
    private static Predicate<ImmutableList<Object>> ensureFallback(Predicate<ImmutableList<Object>> predicate) {
        return predicate != null ? predicate : v -> true;
    }


    @Override
    public boolean test(ImmutableList<Object> values) {
        return wrappedPredicate.test(values);
    }

}
