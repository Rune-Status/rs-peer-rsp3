package org.rspeer.ui.component.menu;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.event.EventDispatcher;
import org.rspeer.event.Subscribe;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.ScriptController;
import org.rspeer.game.script.event.ScriptChangeEvent;
import org.rspeer.game.script.loader.ScriptSource;
import org.rspeer.game.script.process.ScriptPool;
import org.rspeer.game.script.process.ScriptProcess;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.component.script.ScriptSelector;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BotToolBar extends JToolBar {

    private final StartButton start;
    private final ReloadButton reload;

    @Inject
    public BotToolBar(@Named("BotDispatcher") EventDispatcher dispatcher, BotFrame frame,
            BotPreferences preferences, ScriptController controller) {
        dispatcher.subscribe(this);

        setFloatable(false);

        add(Box.createHorizontalGlue());

        this.reload = new ReloadButton(controller);
        add(reload);

        start = new StartButton();
        add(start);

        start.addActionListener(e -> {
            if (start.getText().equals("Start")) {
                ScriptSelector selector = new ScriptSelector(preferences, frame, controller);
                selector.display();
            } else {
                controller.stop();
            }
        });
    }

    @Subscribe
    public void notify(ScriptChangeEvent e) {
        SwingUtilities.invokeLater(() -> {
            switch (e.getState()) {
                case RUNNING: {
                    start.setText("Stop");
                    reload.setVisible(true);
                    break;
                }
                case STOPPED: {
                    start.setText("Start");
                    reload.setVisible(false);
                    break;
                }
            }
        });
    }

    public static class StartButton extends JButton {

        public StartButton() {
            setText("Start");
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setFocusable(false);
        }
    }

    public static class ReloadButton extends JButton {

        public ReloadButton(ScriptController controller) {
            setText("Reload");
            setVisible(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setFocusable(false);
            addActionListener(new Action(controller));
        }

        private class Action implements ActionListener {

            private final ScriptController controller;

            private Action(ScriptController controller) {
                this.controller = controller;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                ScriptSource source = controller.getSource();
                ScriptPool pool = controller.getPool();
                if (source != null && pool != null) {
                    controller.stop();
                    controller.setReload(true);
                }
            }
        }
    }
}
