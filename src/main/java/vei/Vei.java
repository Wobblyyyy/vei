package vei;

import java.io.IOException;

public class Vei {
    public static void main(String[] args) {
        VeiWindow window = new VeiWindow();

        try {
            window.start();
            window.loop();
            window.stop();
        } catch (IOException ignored) {
        }
    }
}
