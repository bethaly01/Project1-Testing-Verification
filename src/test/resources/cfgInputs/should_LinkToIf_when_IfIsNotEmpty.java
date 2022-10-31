package cfgInputs;

public class should_LinkToIf_when_IfIsNotEmpty {
    void name() {
        int i = 1;
        if (i >= 1) {
            i = 2 * 3;
            i += 1;
        }
    }
}
