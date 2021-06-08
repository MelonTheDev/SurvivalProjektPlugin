package wtf.melonthedev.survivalprojektplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardUtils {

    public static Scoreboard createScoreboard(String name, String displayName) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(name, "dummy", displayName);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return scoreboard;
    }

    public static void addScoreboardEntry(Scoreboard scoreboard, String value, int score) {
        scoreboard.getObjectives().forEach(objective -> {
            objective.getScore(value).setScore(score);
        });

    }
}
