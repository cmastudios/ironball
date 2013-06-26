package me.cmastudios.ironball.task;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import me.cmastudios.ironball.Game;
import me.cmastudios.ironball.IronBall;
import me.cmastudios.ironball.TeamType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Connor
 */
public class GolemPositionChecker extends BukkitRunnable {
    
    private final Game game;
    private final IronBall plugin;
    
    public GolemPositionChecker(Game game, IronBall plugin) {
        this.plugin = plugin;
        this.game = game;
    }
    
    public void run() {
        if (!plugin.activeGames.contains(game)) {
            // Game ended
            return;
        }
        IronGolem golem = game.getGolem();
        if (golem == null) {
            /*
             * The golem should never be null at this point - GolemSpawnTask
             * initializes this - cancel event because there's some black magic
             * going on here.
             */
            // In this case, we are the zombie task - so we must cancel ourself.
            plugin.getLogger().warning("Uncancelled instance of GolemPositionChecker "
                    + "task! ID: " + game.getTasks().get(this).getTaskId() + " Class: " + this.toString());
            game.getTasks().remove(this).cancel();
        }
        if (golem.isDead()) {
            // TODO: Add score to team
            Player killer = golem.getKiller();
            if (killer != null && game.isPlaying(killer)) {
                TeamType team = game.getTeam(killer);
                if (game.addPoint(team.getOtherTeam(), 1)) {
                    return;
                }
            }
            plugin.getLogger().info("Golem has died in game " + game.toString());
            plugin.getServer().getScheduler().runTask(plugin, new GolemSpawnTask(game, plugin));
            return;
        }
        Vector loc = BukkitUtil.toVector(golem.getLocation());
        Region region = game.getArena().getRegion();
        if (!region.contains(loc)) {
            // We lost the golem, so lets kill him and spawn a new one
            // TODO: Check for out-of-bounds penalty
            if (golem.getLastDamageCause() instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) golem.getLastDamageCause();
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();
                    if (game.isPlaying(damager)) {
                        if (game.addPoint(game.getTeam(damager).getOtherTeam(), 1)) {
                            return;
                        }
                    }
                }
            }
            golem.remove();
            plugin.getLogger().info("Golem has gone out-of-bounds in game " + game.toString());
            plugin.getServer().getScheduler().runTask(plugin, new GolemSpawnTask(game, plugin));
            return;
        }
        Region redGoal = game.getArena().getRedGoalRegion();
        Region blueGoal = game.getArena().getBlueGoalRegion();
        if (redGoal.contains(loc)) {
            // TODO: Add score to blue team
            if (game.addPoint(TeamType.BLUE, 1)) {
                return;
            }
            golem.remove();
            plugin.getLogger().info("Blue team scored goal in game " + game.toString());
            plugin.getServer().getScheduler().runTask(plugin, new GolemSpawnTask(game, plugin));
            return;
        }
        if (blueGoal.contains(loc)) {
            // TODO: Add score to red team
            if (game.addPoint(TeamType.RED, 1)) {
                return;
            }
            golem.remove();
            plugin.getLogger().info("Red team scored goal in game " + game.toString());
            plugin.getServer().getScheduler().runTask(plugin, new GolemSpawnTask(game, plugin));
            return;
        }
    }
}
