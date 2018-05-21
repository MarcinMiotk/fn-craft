package pure;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CurryingWithImmutableContextTest {


    @Test
    public void curry() {
        final List<Tuple2<Subject, Subject>> pairs = new ArrayList<>();
        pairs.add(Tuple.of(new Subject("A1", 10), new Subject("B1", 10)));
        pairs.add(Tuple.of(new Subject("A2", 10), new Subject("B2", 10)));
        pairs.add(Tuple.of(new Subject("A3", 10), new Subject("B3", 10)));
        pairs.add(Tuple.of(new Subject("A4", 10), new Subject("B4", 10)));
        pairs.add(Tuple.of(new Subject("A5", 10), new Subject("B5", 10)));
        pairs
                .stream()
                .map(Subjects::joinSubjects)
                .collect(
                        FnsCollector::new,
                        FnsCollector::accumulator,
                        FnsCollector::combiner
                )
                .useContext(new ImmutableContext("[", "]", 0))
        .apply((context, subjects) -> {

            System.out.println("SUMMARY: "+context.getCounter());

            subjects.stream().forEach(s->{
                System.out.println(s.getName()+" # "+s.getWeight());
            });



            return true;
        });
    }


    static class FnsCollector {
        static FnsCollector build() {
            return new FnsCollector();
        }

        static void accumulator(
                FnsCollector supplier,
                Function<ImmutableContext, Tuple2<ImmutableContext, Subject>> fn
        ) {
            supplier.add(fn);
        }

        static void combiner(FnsCollector a, FnsCollector b) {

        }

        private final List<Function<ImmutableContext, Tuple2<ImmutableContext, Subject>>> fns = new ArrayList<>();

        void add(Function<ImmutableContext, Tuple2<ImmutableContext, Subject>> it) {
            fns.add(it);
        }

        Tuple2<ImmutableContext, List<Subject>> useContext(ImmutableContext context) {
            final AtomicReference<ImmutableContext> ref = new AtomicReference<>(context);
            final List<Subject> subjects = fns
                    .stream()
                    .map(fn->fn.apply(ref.get()))
                    .peek(ctx->ref.set(ctx._1))
                    .map(Tuple2::_2)
                    .collect(Collectors.toList());
            return Tuple.of(ref.get(), subjects);
        }
    }

    static class Subjects {

        static Function<ImmutableContext, Tuple2<ImmutableContext, Subject>> joinSubjects(Tuple2<Subject, Subject> pair) {
            return join(pair._1, pair._2);
        }

        static Function<ImmutableContext, Tuple2<ImmutableContext, Subject>> join(Subject a, Subject b) {
            return context -> Tuple.of(
                    context.increment(a.getWeight(), b.getWeight()),
                    new Subject(
                            context.getPrefix() + a.getName() + b.getName() + context.getPostfix(),
                            a.getWeight() + b.getWeight()
                    ));
        }
    }

    static class Subject {

        private String name;
        private int weight;

        public Subject(String name, int weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    static class ImmutableContext {
        private final String prefix;
        private final String postfix;
        private final int counter;

        public ImmutableContext(String prefix, String postfix, int counter) {
            this.prefix = prefix;
            this.postfix = postfix;
            this.counter = counter;
        }

        public String getPrefix() {
            return prefix;
        }

        public ImmutableContext setPrefix(String prefix) {
            return new ImmutableContext(prefix, this.postfix, this.counter);
        }

        public String getPostfix() {
            return postfix;
        }

        public ImmutableContext setPostfix(String postfix) {
            return new ImmutableContext(this.prefix, this.postfix, this.counter);
        }

        public int getCounter() {
            return counter;
        }

        public ImmutableContext increment(int... inc) {
            int total = 0;
            for (int x : inc) {
                total += x;
            }
            return new ImmutableContext(this.prefix, this.postfix, this.counter + total);
        }
    }
}
