package org.rspeer.ui.worker;

/*
    Author: zScorpio
    Time: 22:18 (UTC+2)
    Date: Sunday - 05/31/2020
*/

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.rspeer.environment.preferences.BotPreferences;
import org.rspeer.event.EventDispatcher;
import org.rspeer.ui.BotFrame;
import org.rspeer.ui.Window;

import javax.swing.*;
import java.util.List;

public abstract class BotWorker<T, V> extends SwingWorker<T, V> {

    protected final Window<JFrame> window;
    protected final BotPreferences preferences;
    protected final EventDispatcher eventDispatcher;

    @Inject
    public BotWorker(BotFrame window, BotPreferences preferences, @Named("BotDispatcher") EventDispatcher eventDispatcher) {
        this.window = window;
        this.preferences = preferences;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    protected T doInBackground() throws Exception {
        return work();
    }

    @Override
    protected void process(List<V> chunks) {
        notify(chunks.get(chunks.size() - 1));
    }

    @Override
    protected void done() {
        onFinish();
    }

    protected abstract T work() throws Exception;

    protected abstract void notify(V message);

    protected abstract void onFinish();
}
