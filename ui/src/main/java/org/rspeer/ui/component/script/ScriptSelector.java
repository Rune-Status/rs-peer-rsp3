package org.rspeer.ui.component.script;

/*
    Author: zScorpio
    Time: 09:22 (UTC+2)
    Date: Tuesday - 06/02/2020
*/

import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.game.script.ScriptController;
import org.rspeer.game.script.loader.ScriptBundle;
import org.rspeer.game.script.loader.ScriptLoaderProvider;
import org.rspeer.game.script.loader.ScriptProvider;
import org.rspeer.game.script.loader.ScriptSource;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.Window;
import org.rspeer.ui.component.layout.WrapLayout;
import org.rspeer.ui.locale.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ScriptSelector extends Window<JDialog> {

    private final BotPreferences preferences;
    private final BotFrame botFrame;
    private final Viewport viewport;
    private final ScriptProvider loader;

    public ScriptSelector(BotPreferences preferences, BotFrame botFrame, ScriptController controller) {
        super(new JDialog(botFrame.getFrame(), Message.SCRIPT_SELECTOR.getActive(preferences), true));
        this.botFrame = botFrame;
        this.preferences = preferences;
        ScriptLoaderProvider provider = new ScriptLoaderProvider();
        this.viewport = initializeViewport(controller);
        this.loader = provider.get();

        try {
            frame.setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        reload();

        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(botFrame.getFrame());
    }

    private Viewport initializeViewport(ScriptController controller) {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Viewport viewport = new Viewport(controller);

        JScrollPane scroll = new JScrollPane(viewport);
        scroll.getVerticalScrollBar().setUnitIncrement(5);

        int minw = scroll.getVerticalScrollBar().getMinimumSize().width + 2;
        int hgap = Viewport.HGAP;
        int vgap = Viewport.VGAP;

        Dimension minSize = new Dimension(
                (ScriptBox.DEFAULT_WIDTH + hgap) * 3 + hgap + minw,
                (ScriptBox.DEFAULT_HEIGHT + vgap) * 3 + vgap
        );

        scroll.setMinimumSize(minSize);
        scroll.setPreferredSize(minSize);
        scroll.setBorder(null);

        frame.add(scroll, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
        });
        return viewport;
    }

    private void reload() {
        SwingWorker<ScriptBundle, Void> worker = new SwingWorker<ScriptBundle, Void>() {
            @Override
            protected ScriptBundle doInBackground() {
                ScriptBundle bundle = loader.load();
                bundle.addAll(loader.predefined());
                return bundle;
            }

            @Override
            protected void done() {
                try {
                    ScriptBundle bundle = get();

                    viewport.removeAll();
                    bundle.forEach(viewport::addBox);
                    viewport.revalidate();

                    JScrollPane scrollPane = (JScrollPane) viewport.getParent().getParent();
                    scrollPane.getVerticalScrollBar().setValue(0);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @Override
    public void display() {
        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }


    /*
            Static just to make it clear that we're referring to ScriptButton
                         when calling getWidth(), getHeight(), etc.
         */
    private static class ScriptButton extends JButton {

        private static final int GAP = 3;
        private static final int INSET = 6;
        private static final int VGAP = GAP - 3;
        private final ScriptSource script;
        private final Insets insets;

        public ScriptButton(ScriptSource script) {
            this.script = script;
            this.insets = new Insets(INSET - 3, INSET + 2, INSET + 4, INSET + 2);
        }

        @Override
        public void paint(Graphics g2) {
            super.paint(g2);
            Graphics2D g = (Graphics2D) g2;
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            paintCustom(g);
        }

        private void paintCustom(Graphics2D g) {
            int bottomY = getHeight() - insets.bottom;

            Font plain = g.getFont();
            Font bold = plain.deriveFont(Font.BOLD);

            g.setFont(bold);
            String developer = "Developer: ";

            int developerWidth = g.getFontMetrics().stringWidth(developer);
            int developerHeight = g.getFontMetrics().getHeight();

            g.drawString(developer, insets.left, bottomY);
            g.setFont(plain);
            g.drawString(script.getDeveloper(), developerWidth + insets.left, bottomY);

            g.setFont(bold);
            String version = "Version: ";
            int versionWidth = g.getFontMetrics().stringWidth(version);

            bottomY = bottomY - developerHeight - VGAP;

            g.drawString(version, insets.left, bottomY);
            g.setFont(plain);
            g.drawString(String.valueOf(script.getVersion()), versionWidth + insets.left, bottomY);
        }
    }

    public class ScriptBox extends JPanel {

        public static final int DEFAULT_WIDTH = 200;
        public static final int DEFAULT_HEIGHT = 100;

        private final ScriptController controller;
        private final ScriptSource source;

        public ScriptBox(ScriptController controller, ScriptSource source) {
            this.controller = controller;
            this.source = source;

            setLayout(new GridBagLayout());
            setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
            setMaximumSize(getMinimumSize());
            setPreferredSize(getMinimumSize());

            initializeComponents();
        }

        private void initializeComponents() {
            setBorder(new TitledBorder(source.getName()));

            ScriptButton button = new ScriptButton(source);
            button.setBorder(null);

            button.addActionListener(act -> {
                controller.start(loader, source);
                dispose();
            });

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.weightx = 1;
            constraints.weighty = 1;
            constraints.fill = GridBagConstraints.BOTH;

            add(button, constraints);
        }
    }

    private class Viewport extends JPanel {

        private static final int HGAP = 4;
        private static final int VGAP = 4;

        private final ScriptController controller;

        private Viewport(ScriptController controller) {
            this.controller = controller;
            setLayout(new WrapLayout(WrapLayout.LEFT, HGAP, VGAP));
        }

        private void addBox(ScriptSource source) {
            add(new ScriptBox(controller, source));
        }
    }
}
