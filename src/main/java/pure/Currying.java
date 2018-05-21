package pure;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Currying {

    public static <A, B, C> Function<A, Function<B, C>> curry(final BiFunction<A, B, C> f) {
        return (A a) -> (B b) -> f.apply(a, b);
    }

    public static <A, B, C> BiFunction<A, B, C> uncurry(Function<A, Function<B, C>> f) {
        return (A a, B b) -> f.apply(a).apply(b);
    }
}
