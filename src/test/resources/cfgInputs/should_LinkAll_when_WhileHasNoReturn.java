package cfgInputs;

public class should_LinkAll_when_WhileHasNoReturn {
    void name() {
        int i = 1;
        while (i < 10) {
            i = 2 * i;
            i++;
        }
    }
}
