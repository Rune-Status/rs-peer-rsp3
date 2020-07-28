package org.rspeer;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.worker.LoadGameWorker;

import javax.swing.*;

/**
 * Entry point for the application
 */
public class Application {

    private static final Injector injector = Guice.createInjector(new ApplicationModule());

    @Inject
    private BotFrame window;

    @Inject
    private LoadGameWorker loader;
    
    public static void main(String[] args) {
        Application application = injector.getInstance(Application.class);
        application.start();
    }

    public void start() {
        SwingUtilities.invokeLater(() -> {
            window.display();
            loader.execute();
        });
    }
}
