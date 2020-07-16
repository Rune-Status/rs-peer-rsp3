package org.rspeer.game;

import jag.game.RSClient;
import org.rspeer.game.component.tab.Skill;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameAccount {

    private final Details details;
    private final Preferences preferences;
    private final Profile profile;

    public GameAccount(Details details, Preferences preferences, Profile profile) {
        this.details = details;
        this.preferences = preferences;
        this.profile = profile;
    }

    public GameAccount(Details details, Preferences preferences) {
        this(details, preferences, new Profile());
    }

    public static GameAccount predefined() {
        RSClient client = Game.getClient();
        return new GameAccount(
                new Details(client.getUsername(), client.getPassword(), ""),
                new Preferences(0, Preferences.DEFAULT_SKILL_CHOICE)
        );
    }

    public Details getDetails() {
        return details;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public Profile getProfile() {
        return profile;
    }

    public static class Details {

        private final String username;
        private final String password;
        private final String pin;

        public Details(String username, String password, String pin, Function<String, String> decryptor) {
            this.username = decryptor.apply(username);
            this.password = decryptor.apply(password);
            this.pin = decryptor.apply(pin);
        }

        public Details(String username, String password, String pin) {
            this(username, password, pin, Function.identity());
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getPin() {
            return pin;
        }

        public boolean isPinPresent() {
            return pin.length() == 4;
        }
    }

    public static class Preferences {

        private static final Skill DEFAULT_SKILL_CHOICE = Skill.ATTACK;

        private final int eventDismissPercent;
        private final Skill eventSkillChoice;

        public Preferences(int eventDismissPercent, Skill eventSkillChoice) {
            this.eventDismissPercent = eventDismissPercent;
            this.eventSkillChoice = eventSkillChoice;
        }

        public int getEventDismissPercent() {
            return eventDismissPercent;
        }

        public Skill getEventSkillChoice() {
            return eventSkillChoice;
        }
    }

    public static class Profile {

        private final Map<String, Object> data;

        public Profile(Map<String, Object> data) {
            this.data = data;
        }

        public Profile() {
            this(new HashMap<>());
        }

        public <T> T get(String key) {
            return (T) data.get(key);
        }
    }
}
