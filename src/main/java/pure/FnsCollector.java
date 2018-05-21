package pure;

import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

class FnsCollector<A, B> {

    private final List<Function<A, Tuple2<A, B>>> fns = new ArrayList<>();

    static FnsCollector build() {
        return new FnsCollector();
    }

    static <A, B> void accumulator(
            FnsCollector supplier,
            Function<A, Tuple2<A, B>> fn
    ) {
        supplier.add(fn);
    }

    static <A,B> void combiner(FnsCollector<A,B> a, FnsCollector<A,B> b) {

    }

    private void add(Function<A, Tuple2<A, B>> it) {
        fns.add(it);
    }

    Tuple2<A, List<B>> useContext(A context) {
        final AtomicReference<A> ref = new AtomicReference<>(context);
        final List<B> subjects = fns
                .stream()
                .map(fn->fn.apply(ref.get()))
                .peek(ctx->ref.set(ctx._1))
                .map(Tuple2::_2)
                .collect(Collectors.toList());
        return Tuple.of(ref.get(), subjects);
    }
}
