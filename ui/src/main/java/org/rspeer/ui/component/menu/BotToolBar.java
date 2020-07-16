package org.rspeer.ui.component.menu;

import org.rspeer.environment.Environment;
import org.rspeer.event.Subscribe;
import org.rspeer.game.script.Script;
import org.rspeer.game.script.event.ScriptChangeEvent;
import org.rspeer.game.script.loader.ScriptBundle;
import org.rspeer.game.script.loader.ScriptLoaderProvider;
import org.rspeer.game.script.loader.ScriptProvider;
import org.rspeer.game.script.loader.ScriptSource;
import org.rspeer.ui.component.script.ScriptSelector;

import javax.swing.*;

public class BotToolBar extends JToolBar {

    private final StartButton start;
    private final ReloadButton reload;

    public BotToolBar(Environment environment) {
        environment.getEventDispatcher().subscribe(this);

        setFloatable(false);

        add(Box.createHorizontalGlue());

        reload = new ReloadButton(environment);
        add(reload);

        start = new StartButton();
        add(start);

        start.addActionListener(e -> {
            if (start.getText().equals("Start")) {
                ScriptSelector selector = new ScriptSelector(environment.getBotContext().getFrame(), environment);
                selector.display();
            } else {
                environment.getEventDispatcher().dispatch(new ScriptChangeEvent(
                        environment.getScriptController().getSource(),
                        Script.State.STOPPED,
                        Script.State.RUNNING
                ));
                environment.getScriptController().stop();
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

        public ReloadButton(Environment environment) {
            setText("Reload");
            setVisible(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(true);
            setFocusable(false);
            addActionListener(e -> {
                ScriptSource source = environment.getScriptController().getSource();
                if (source == null) {
                    return;
                }
                Script currentScript = environment.getScriptController().getPool().getActive();
                if (currentScript == null) {
                    return;
                }
                ScriptProvider loader = new ScriptLoaderProvider().get();
                Script.State currentState = currentScript.getState();
                environment.getEventDispatcher().dispatch(
                        new ScriptChangeEvent(source, Script.State.STOPPED, currentState)
                );
                environment.getScriptController().stop();

                ScriptBundle bundle = loader.load();
                ScriptSource reloaded = bundle.findShallow(source);
                if (reloaded != null) {
                    environment.getScriptController().start(loader, reloaded);
                    environment.getEventDispatcher().dispatch(
                            new ScriptChangeEvent(reloaded, Script.State.RUNNING, Script.State.STOPPED)
                    );
                }
            });
        }
    }
}
