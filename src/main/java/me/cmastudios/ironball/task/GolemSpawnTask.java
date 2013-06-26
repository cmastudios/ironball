package me.cmastudios.ironball.task;

import me.cmastudios.ironball.Arena;
import me.cmastudios.ironball.Game;
import me.cmastudios.ironball.IronBall;
import org.bukkit.Location;
import org.bukkit.entity.IronGolem;
import org.bukkit.scheduler.BukkitTask;

/**
 * Spawns an iron golem at the arena's golem spawn point.
 *
 * @author Connor
 */
public class GolemSpawnTask implements Runnable {

    private final Game game;
    private final IronBall plugin;

    public GolemSpawnTask(Game game, IronBall plugin) {
        this.game = game;
        this.plugin = plugin;
    }

    public void run() {
        Location spawnPoint = Arena.deserializeLocation(game.getArena().getGolemSpawn()).add(0, 3, 0);
        IronGolem golem = game.getArena().getWorld().spawn(spawnPoint, IronGolem.class);
        for (Runnable rnbl : game.getTasks().keySet()) {
            if (rnbl instanceof GolemPositionChecker) {
                // Cancel existing position checker
                game.getTasks().get(rnbl).cancel();
            }
        }
        game.setGolem(golem);
        game.broadcastMessage(IronBall.getPrefix() + IronBall.getString("GAME.SPAWNED"));
        GolemPositionChecker checkTask = new GolemPositionChecker(game, plugin);
        BukkitTask runTaskTimer = plugin.getServer().getScheduler().runTaskTimer(plugin, checkTask, 20L, 20L);
        game.getTasks().put(checkTask, runTaskTimer);
    }
}
