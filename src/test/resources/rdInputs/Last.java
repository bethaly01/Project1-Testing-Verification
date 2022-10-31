package rdInputs;

public class Last {
    int f(int a, int b) {
        int x;
        x = a + b;
        if (a > b) {
            while (x < a * 10) {
                x += a + b;
            }
        } else {
            x = b;
        }
        return x;
    }
}
