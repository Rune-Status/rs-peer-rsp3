package org.rspeer.game.script.passive;

import org.rspeer.game.script.Script;

import java.time.Duration;

public abstract class PassiveScript extends Script {

    private final Duration frequency;

    protected PassiveScript(Duration frequency) {
        this.frequency = frequency;
    }
}
