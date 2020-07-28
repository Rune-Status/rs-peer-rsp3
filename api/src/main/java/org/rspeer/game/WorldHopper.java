package org.rspeer.game;

import jag.game.RSWorld;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.rspeer.game.adapter.component.InterfaceComponent;
import org.rspeer.game.component.InterfaceComposite;
import org.rspeer.game.component.Interfaces;

public class WorldHopper {

    public static boolean hopTo(int world) {
        if (Game.isLoggedIn()) {
            return GameHopper.hopTo(world);
        } else {
            return ClientHopper.hopTo(world);
        }
    }

    public static class ClientHopper {

        public static boolean hopTo(int world) {
            RSWorld[] clientWorlds = Game.getClient().getWorlds();
            if (clientWorlds == null) {
                Game.getClient().loadWorlds();
            } else {
                RSWorld clientWorld = Arrays.stream(clientWorlds)
                                            .filter(rsWorld -> rsWorld.getId() == world)
                                            .findFirst()
                                            .orElse(null);
                if (clientWorld != null) {
                    Game.getClient().setWorld(clientWorld);
                    return true;
                }
            }
            return false;
        }
    }

    public static class GameHopper {

        private static long loadingSince;

        public static boolean hopTo(int world) {
            if (isWorldSwitcherOpen()) {
                if (isWorldSwitcherLoading()) {
                    handleStuckLoading();
                } else {
                    InterfaceComponent worldComponent = getWorldComponent(world);
                    if (worldComponent != null && worldComponent.interact("Switch")) {
                        loadingSince = 0;
                        return true;
                    }
                }
            } else {
                openWorldSwitcher();
            }
            return false;
        }

        public static boolean isWorldSwitcherOpen() {
            return Interfaces.query(InterfaceComposite.WORLD_SELECT)
                             .texts(text -> text.startsWith("Loading") || text.startsWith("Current"))
                             .results()
                             .first() != null;
        }

        public static boolean isWorldSwitcherLoading() {
            return Interfaces.query(InterfaceComposite.WORLD_SELECT)
                             .texts(text -> text.startsWith("Loading"))
                             .results()
                             .first() != null;
        }

        public static boolean openWorldSwitcher() {
            InterfaceComponent component = Interfaces.query(InterfaceComposite.LOGOUT_TAB)
                                                     .actions(action -> action.equals("World Switcher"))
                                                     .results()
                                                     .first();
            return component != null && component.interact("World Switcher");
        }

        private static void handleStuckLoading() {
            if (loadingSince == 0) {
                loadingSince = System.currentTimeMillis();
            } else if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - loadingSince) > 5) {
                InterfaceComponent closeButton = getCloseButton();
                if (closeButton != null && closeButton.interact("Close")) {
                    loadingSince = 0;
                }
            }
        }

        private static InterfaceComponent getWorldComponent(int world) {
            return Interfaces.query(true)
                             .groups(InterfaceComposite.WORLD_SELECT)
                             .names(name -> name.startsWith("<col=ff9040>" + world))
                             .actions(action -> action.equals("Switch"))
                             .results()
                             .first();
        }

        private static InterfaceComponent getCloseButton() {
            return Interfaces.query(InterfaceComposite.WORLD_SELECT)
                             .actions(action -> action.equals("Close"))
                             .results()
                             .first();
        }
    }
}
