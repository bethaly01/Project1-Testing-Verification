package cfgInputs;

public class should_LinkToElse_when_ElseIsEmpty {
    void name() {
        int i = 1;
        if (i >= 1) {
            i = 2 * 3;
            i += 1;
        }
    }
}
