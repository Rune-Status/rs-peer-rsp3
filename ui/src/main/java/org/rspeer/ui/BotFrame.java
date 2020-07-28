package org.rspeer.ui;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.rspeer.commons.Configuration;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.environment.preferences.event.PreferenceEvent;
import org.rspeer.environment.preferences.type.AlwaysOnTopPreference;
import org.rspeer.environment.preferences.type.SceneRenderPreference;
import org.rspeer.event.EventDispatcher;
import org.rspeer.event.Subscribe;
import org.rspeer.game.script.ScriptController;
import org.rspeer.ui.component.menu.BotMenuBar;
import org.rspeer.ui.component.menu.BotToolBar;
import org.rspeer.ui.component.splash.Splash;
import org.rspeer.ui.event.SetAppletEvent;
import org.rspeer.ui.event.SplashEvent;
import org.rspeer.ui.event.UIEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class BotFrame extends Window<JFrame> {

    private final BotPreferences preferences;
    private final EventDispatcher eventDispatcher;
    private final BotMenuBar menu;

    private Splash splash;

    @Inject
    public BotFrame(BotPreferences preferences, @Named("BotDispatcher") EventDispatcher eventDispatcher,
                    ScriptController controller) {
        super(new JFrame(Configuration.getApplicationTitle()));
        this.preferences = preferences;
        this.eventDispatcher = eventDispatcher;
        this.menu = new BotMenuBar(preferences, this);
        applyDefaults();
        applyComponents(new BotToolBar(eventDispatcher, this, preferences, controller));
        applyListeners();
    }

    public JFrame getFrame() {
        return super.frame;
    }

    private void applyDefaults() {
        System.setProperty("sun.awt.noerasebackground", "true");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void applyComponents(BotToolBar botToolBar) {
        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setAlwaysOnTop(preferences.valueOf(AlwaysOnTopPreference.class));

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        splash = new Splash();
        frame.add(splash, BorderLayout.CENTER);

        frame.setJMenuBar(this.menu);
        frame.add(botToolBar, BorderLayout.NORTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(frame.getSize());
        frame.validate();
        //TODO: Implement logger & save its show/hide state in the preferences file
    }

    private void applyListeners() {
        eventDispatcher.subscribe(this);
        /*
                Typically, we should add a windowClosing callback for the frame
                but we're not doing that here since the setDefaultCloseOperation
                method is taking an EXIT_ON_CLOSE value
         */
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }

    @Subscribe
    public void notify(UIEvent e) {
        if (e.getSource() == this) {
            if (e instanceof SplashEvent) {
                SplashEvent event = (SplashEvent) e;
                splash.setMessage(event.getMessage());
            } else if (e instanceof SetAppletEvent) {
                SetAppletEvent event = (SetAppletEvent) e;
                if (splash != null) {
                    splash = null;
                }
                BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout();
                Component previousComp = layout.getLayoutComponent(BorderLayout.CENTER);
                frame.remove(previousComp);
                frame.add(event.getApplet(), BorderLayout.CENTER);
            }
        }
    }

    @Subscribe
    public void notify(PreferenceEvent e) {
        if (e.getSource() instanceof SceneRenderPreference) {
            menu.getRenderScene().setSelected(false);
        }
    }
}
