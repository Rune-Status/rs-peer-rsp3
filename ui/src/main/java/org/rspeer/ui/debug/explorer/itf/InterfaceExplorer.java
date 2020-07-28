package org.rspeer.ui.debug.explorer.itf;

import com.google.inject.Inject;
import org.rspeer.commons.AWTUtil;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.event.Subscribe;
import org.rspeer.game.Game;
import org.rspeer.game.component.Interfaces;
import org.rspeer.game.event.RenderEvent;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.locale.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.function.Supplier;

//TODO: Add separate viewport & info refresh buttons
public class InterfaceExplorer extends JFrame {

    private static final int VIEWPORT_WIDTH = 220;
    private static final int INFO_WIDTH = 360;

    private final JTree tree;

    private Object render = null;
    private Supplier<TreeModel> provider = new DefaultModelProvider();

    @Inject
    public InterfaceExplorer(BotPreferences preferences, BotFrame frame) {
        super(Message.INTERFACE_EXPLORER.getActive(preferences));

        try {
            setIconImage(ImageIO.read(getClass().getResource("/icon.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new BorderLayout());

        this.tree = new JTree();
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        JMenuItem hierarchical = new JCheckBoxMenuItem("Hierarchical View", false);
        add(hierarchical, BorderLayout.NORTH);
        hierarchical.addActionListener(x -> {
            provider = hierarchical.isSelected() ? new HierarchicalModelProvider() : new DefaultModelProvider();
            tree.setModel(provider.get());
        });

        JScrollPane viewport = new JScrollPane(tree);
        viewport.setMinimumSize(new Dimension(VIEWPORT_WIDTH, -1));
        viewport.setPreferredSize(viewport.getMinimumSize());

        JScrollPane info = new JScrollPane(buildInfoPanel(tree));
        info.setMinimumSize(new Dimension(INFO_WIDTH, -1));
        info.setPreferredSize(info.getMinimumSize());
        add(viewport, BorderLayout.WEST);
        add(info, BorderLayout.CENTER);

        tree.setModel(provider.get());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                Game.getEventDispatcher().subscribe(InterfaceExplorer.this);
            }

            @Override
            public void windowClosing(WindowEvent e) {
                Game.getEventDispatcher().unsubscribe(InterfaceExplorer.this);
            }
        });

        pack();
        setMinimumSize(new Dimension(getWidth(), frame.getFrame().getHeight()));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(frame.getFrame());
        setVisible(true);
    }

    private InfoPanel buildInfoPanel(JTree tree) {
        InfoPanel panel = new InfoPanel();

        // Inset value 4 is the JTree's default
        // A forced bottom-inset of value 10 is set by the InfoPanel
        panel.setBorder(new EmptyBorder(new Insets(4, 4, 0, 4)));

        tree.getSelectionModel().addTreeSelectionListener(new SelectionListener(panel));
        return panel;
    }

    @Subscribe
    public void notify(RenderEvent e) {
        Graphics g = e.getSource();
        if (render instanceof ComponentNode) {
            ComponentNode node = (ComponentNode) render;
            draw(g, node);
        } else if (render instanceof InterfaceNode) {
            InterfaceNode node = (InterfaceNode) render;
            for (ComponentNode child : node.children) {
                draw(g, child);
            }
        }
    }

    private void draw(Graphics g, ComponentNode node) {
        AWTUtil.drawBorderedRectangle(g, node.component.getBounds());
        for (ComponentNode component : node.children) {
            draw(g, component);
        }
    }

    private class SelectionListener implements TreeSelectionListener {

        private final JPanel panel;

        private SelectionListener(JPanel panel) {
            this.panel = panel;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            panel.removeAll();

            Object selected = tree.getLastSelectedPathComponent();
            render = selected;

            if (selected instanceof InterfaceNode) {
                InterfaceNode node = (InterfaceNode) selected;
                panel.add(new InfoLabel("Address: " + node.group));
                panel.add(new InfoLabel("Component Count: " + Interfaces.query().groups(node.group).results().size()));
            } else if (selected instanceof ComponentNode) {
                ComponentNode node = (ComponentNode) selected;
                panel.add(new InfoLabel("Address: " + node.component.toAddress().toShortString()));
                panel.add(new InfoLabel("UID: " + node.component.getUid()));
                panel.add(new InfoLabel("Bounds: " + node.component.getBounds()));
                panel.add(new InfoLabel("Item: " + node.component.getItem()));
                panel.add(new InfoLabel("Type: " + node.component.getType()));
                panel.add(new InfoLabel("Material: " + node.component.getMaterialId()));
                panel.add(new InfoLabel("Sprite: " + node.component.getSpriteId()));
                panel.add(new InfoLabel("Foreground: " + node.component.getForeground()));
                panel.add(new InfoLabel("Shadow: " + node.component.getShadowColor()));
                panel.add(new InfoLabel("Model (type): " + node.component.getModelId() + " (" + node.component.getModelType() + ")"));
                panel.add(new InfoLabel("Config: " + node.component.getConfig()));
                panel.add(new InfoLabel("Text: " + node.component.getText()));
                panel.add(new InfoLabel("Name: " + node.component.getName()));
                panel.add(new InfoLabel("Actions: " + node.component.getActions()));
                panel.add(new InfoLabel("Tooltip: " + node.component.getToolTip()));
            }
            panel.repaint();
            panel.revalidate();
        }
    }
}
