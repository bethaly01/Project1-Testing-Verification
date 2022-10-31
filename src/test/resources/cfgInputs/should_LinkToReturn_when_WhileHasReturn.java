package cfgInputs;

public class should_LinkAll_when_WhileHasReturn {
    String name() {
        int i = 1;
        while (i < 10) {
            i = 2 * i;
            return "String";
        }
    }
}
