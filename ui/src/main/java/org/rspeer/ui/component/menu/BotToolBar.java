package org.rspeer.ui.component.menu;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.event.EventDispatcher;
import org.rspeer.event.Subscribe;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.event.ScriptChangeEvent;
import org.rspeer.game.script.process.ScriptController;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.component.script.ScriptSelector;

import javax.swing.*;

public class BotToolBar extends JToolBar {

    private final StartButton start;

    @Inject
    public BotToolBar(@Named("BotDispatcher") EventDispatcher dispatcher, BotFrame frame,
            BotPreferences preferences, ScriptController controller) {
        dispatcher.subscribe(this);

        setFloatable(false);

        add(Box.createHorizontalGlue());

        start = new StartButton();
        add(start);

        start.addActionListener(e -> {
            if (start.getText().equals("Start")) {
                ScriptSelector selector = new ScriptSelector(dispatcher, preferences, frame, controller);
                selector.display();
            } else {
                dispatcher.dispatch(new ScriptChangeEvent(
                        controller.getSource(),
                        Script.State.STOPPED,
                        Script.State.RUNNING
                ));
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
                    break;
                }
                case STOPPED: {
                    start.setText("Start");
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
}
