package cfgInputs;

public class should_LinkToElse_when_ElseIsNotEmpty {
    void name() {
        int i = 1;
        if (i >= 1) {
            i = 2 * 3;
            i += 1;
        } else {
            i = 2 + 2 + 2;
            i -= 2;
        }
    }
}
