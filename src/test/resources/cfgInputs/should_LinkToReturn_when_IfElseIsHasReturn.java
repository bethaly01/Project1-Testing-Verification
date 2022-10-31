package cfgInputs;

public class should_LinkToReturn_when_IfElseIsHasReturn {
    void name() {
        int i = 1;
        if (i >= 1) {
            i = 2 * 3;
            i += 1;
            return;
        } else {
            i = 2 + 2 + 2;
            i -= 2;
            return;
        }
    }
}
