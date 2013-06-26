package me.cmastudios.ironball.task;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import me.cmastudios.ironball.Game;
import me.cmastudios.ironball.IronBall;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;

/**
 * Timer to start game after specific amount of time.
 *
 * @author Connor
 */
public class GameStartTimer implements Runnable {

    private final Game game;
    private final IronBall plugin;

    public GameStartTimer(Game game, IronBall plugin) {
        this.game = game;
        this.plugin = plugin;
    }

    public void run() {
        if (game.canStartGame()) {
            // Clear entities from the arena
            for (Entity ent : game.getArena().getWorld().getEntities()) {
                Vector vec = BukkitUtil.toVector(ent.getLocation());
                if (game.getArena().getRegion().contains(vec)) {
                    if (ent instanceof Projectile || ent instanceof Creature
                            || ent instanceof Item || ent instanceof Vehicle) {
                        ent.remove();
                    }
                }
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, new GolemSpawnTask(game, plugin), 100L);
            game.broadcastMessage(IronBall.getPrefix() + IronBall.getString("GAME.SPAWNING"));
        } else {
            game.broadcastMessage(IronBall.getPrefix() + IronBall.getString("GAME.NOTENOUGH"));
            game.endGame();
            plugin.activeGames.remove(game);
        }
    }
}
