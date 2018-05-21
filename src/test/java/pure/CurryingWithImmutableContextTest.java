package pure;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
                .map(SubjectsCombined::concatenate)
                .collect(
                        FnsCollector<ImmutableContext, String>::new,
                        FnsCollector::accumulator,
                        FnsCollector::<ImmutableContext, String>combiner
                )
                .useContext(new ImmutableContext("[", "]", 0))
        .apply((context, subjects) -> {
            System.out.println("SUMMARY: "+context.getCounter());
            subjects.stream().forEach(System.out::println);
            return true;
        });
    }


    static class SubjectsCombined {

        static Function<ImmutableContext, Tuple2<ImmutableContext, String>> concatenate(Function<ImmutableContext, Tuple2<ImmutableContext, Subject>> src) {
            return context -> src.apply(context).apply((ctx, subject) -> Tuple.of(ctx, subject.getName()+"=>"+subject.getWeight()));
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
