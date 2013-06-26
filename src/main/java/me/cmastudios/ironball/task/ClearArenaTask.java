/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cmastudios.ironball.task;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import me.cmastudios.ironball.Arena;
import me.cmastudios.ironball.IronBall;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * Removes all projectiles, creatures, and players from an arena.
 *
 * @author Connor
 */
public class ClearArenaTask implements Runnable {

    private final IronBall plugin;
    private final Arena arena;

    public ClearArenaTask(IronBall plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void run() {
        Region region = arena.getRegion();
        for (Entity ent : arena.getWorld().getEntities()) {
            Vector vec = BukkitUtil.toVector(ent.getLocation());
            if (region.contains(vec)) {
                if (ent instanceof Projectile || ent instanceof Creature) {
                    ent.remove(); // Risky, may comod
                }
            }
        }
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            Vector vec = BukkitUtil.toVector(player.getLocation());
            if (region.contains(vec)) {
                player.teleport(Arena.deserializeLocation(arena.getSpectatorSpawn()), TeleportCause.PLUGIN);
            }
        }
    }
}
