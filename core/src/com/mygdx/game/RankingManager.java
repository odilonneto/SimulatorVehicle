package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class RankingManager {
    private static final String PREFS_NAME = "mygame_ranking";
    private static final String KEY_RANKING = "ranking";
    private static final int MAX_ENTRIES = 5; // máximo de entradas no ranking

    // Classe que representa cada entrada do ranking
    public static class RankingEntry implements Comparable<RankingEntry> {
        public String username;
        public int score;
        public int fuel;

        // Construtor vazio para a serialização
        public RankingEntry() { }

        public RankingEntry(String username, int score, int fuel) {
            this.username = username;
            this.score = score;
            this.fuel = fuel;
        }

        @Override
        public int compareTo(RankingEntry other) {
            // Ordena em ordem decrescente (maior score primeiro)
            return Integer.compare(other.score, this.score);
        }
    }

    public static Array<RankingEntry> loadRanking() {
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        String jsonString = prefs.getString(KEY_RANKING, "");
        if (jsonString.isEmpty()) {
            return new Array<RankingEntry>();
        } else {
            Json json = new Json();
            RankingEntry[] entries = json.fromJson(RankingEntry[].class, jsonString);
            return new Array<RankingEntry>(entries);
        }
    }

    public static void saveRanking(Array<RankingEntry> ranking) {
        Json json = new Json();
        RankingEntry[] entries = ranking.toArray(RankingEntry.class);
        String jsonString = json.toJson(entries);
        Preferences prefs = Gdx.app.getPreferences(PREFS_NAME);
        prefs.putString(KEY_RANKING, jsonString);
        prefs.flush();
    }

    public static void updateRanking(String username, int score, int fuel) {
        Array<RankingEntry> ranking = loadRanking();
        ranking.add(new RankingEntry(username, score, fuel));
        ranking.sort();
        while (ranking.size > MAX_ENTRIES) {
            ranking.removeIndex(ranking.size - 1);
        }
        saveRanking(ranking);
    }
}
