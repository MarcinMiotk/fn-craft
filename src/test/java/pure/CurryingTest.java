package pure;

import org.junit.Test;

import java.util.function.Function;

public class CurryingTest {


    @Test
    public void curry() {
        Currying.curry(Subjects::join)
                .apply(new Subject("A", 10))
                .apply(new Subject("B", 20))
                .apply(new Context("[", "]"));
    }


    static class Subjects {

        static Function<Context, Subject> join(Subject a, Subject b) {
            return context -> new Subject(
                    context.getPrefix() + a.getName() + b.getName() + context.getPostfix(),
                    a.getWeight() + b.getWeight()
            );
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

    static class Context {
        private String prefix;
        private String postfix;

        public Context(String prefix, String postfix) {
            this.prefix = prefix;
            this.postfix = postfix;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getPostfix() {
            return postfix;
        }

        public void setPostfix(String postfix) {
            this.postfix = postfix;
        }
    }

}