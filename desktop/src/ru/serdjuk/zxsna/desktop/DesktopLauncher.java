package ru.serdjuk.zxsna.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import ru.serdjuk.zxsna.zxsna;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration conf = new Lwjgl3ApplicationConfiguration();
        conf.setResizable(true);
        conf.setWindowSizeLimits(800,600,2048,2048);
        conf.useVsync(true);
        new Lwjgl3Application(new zxsna(), conf);
    }
}
