package org.rspeer.ui.worker;

/*
    Author: zScorpio
    Time: 22:18 (UTC+2)
    Date: Sunday - 05/31/2020
*/

import com.google.inject.Inject;
import com.google.inject.name.Named;
import jag.game.RSClient;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.environment.preferences.event.PreferenceEvent;
import org.rspeer.environment.preferences.type.BotPreference;
import org.rspeer.environment.preferences.type.SceneRenderPreference;
import org.rspeer.event.EventDispatcher;
import org.rspeer.game.Game;
import org.rspeer.game.loader.GameLoader;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.event.SetAppletEvent;
import org.rspeer.ui.event.SplashEvent;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class LoadGameWorker extends BotWorker<RSClient, String> {

    @Inject
    public LoadGameWorker(BotFrame window, BotPreferences preferences, @Named("BotDispatcher") EventDispatcher eventDispatcher) {
        super(window, preferences, eventDispatcher);
    }

    @Override
    protected RSClient work() throws IOException {
        return GameLoader.load(true, new Callback());
    }

    @Override
    protected void notify(String message) {
        SplashEvent event = new SplashEvent(window, message);
        eventDispatcher.dispatch(event);
    }

    @Override
    protected void onFinish() {
        try {
            RSClient client = get();
            SetAppletEvent event = new SetAppletEvent(window, client.asApplet());
            eventDispatcher.dispatch(event);
            if (!preferences.valueOf(SceneRenderPreference.class)) {
                BotPreference<Boolean> preference = preferences.get(SceneRenderPreference.class);
                PreferenceEvent preferenceEvent = new PreferenceEvent(preference);
                eventDispatcher.dispatch(preferenceEvent);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class Callback implements Consumer<RSClient> {

        @Override
        public void accept(RSClient client) {
            publish("Loading RuneScape");
            Game.setClient(client);
            publish("Successfully loaded RuneScape");
        }
    }
}
