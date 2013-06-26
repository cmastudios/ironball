package me.cmastudios.ironball.listener;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.Region;
import me.cmastudios.ironball.Arena;
import me.cmastudios.ironball.Game;
import me.cmastudios.ironball.IronBall;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Plugin event listener.
 *
 * @author Connor
 */
public class IronBallListener implements Listener {

    private final IronBall plugin;

    public IronBallListener(IronBall plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        Vector loc = BukkitUtil.toVector(event.getTo());
        for (Game game : plugin.activeGames) {
            Region region = game.getArena().getRegion();
            if (!region.getWorld().getName().equals(event.getTo().getWorld().getName())) {
                continue;
            }
            if (region.contains(loc) && !game.isPlaying(event.getPlayer())) {
                if (event.getPlayer().isInsideVehicle()) {
                    event.getPlayer().getVehicle().eject();
                }
                event.setTo(Arena.deserializeLocation(game.getArena().getSpectatorSpawn()));
                event.getPlayer().sendMessage(IronBall.getPrefix() + IronBall.getString("ARENA.NOENTRY"));
                return;
            }
        }
        Game game = plugin.getGameByPlayer(event.getPlayer());
        if (game != null && !game.getArena().getRegion().contains(loc)) {
            event.setTo(game.getTeam(event.getPlayer()).getTeamSpawn(game.getArena()));
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.unloadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        this.unloadPlayer(event.getPlayer());
    }

    private void unloadPlayer(Player player) {
        Game game = plugin.getGameByPlayer(player);
        if (game != null) {
            game.removePlayer(player);
            if (game.isEmpty()) {
                game.broadcastMessage(IronBall.getPrefix() + IronBall.getString("GAME.END"));
                game.endGame();
                plugin.activeGames.remove(game);
            } else if (game.areTeamsUnbalanced()) {
                game.broadcastMessage(IronBall.getPrefix() + IronBall.getString("GAME.REBALANCE"));
                game.balanceTeams();
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        Vector loc = BukkitUtil.toVector(event.getTo());
        for (Game game : plugin.activeGames) {
            Region region = game.getArena().getRegion();
            if (!region.getWorld().getName().equals(event.getTo().getWorld().getName())) {
                continue;
            }
            if (region.contains(loc) && !game.isPlaying(event.getPlayer())) {
                event.setTo(Arena.deserializeLocation(game.getArena().getSpectatorSpawn()));
                event.getPlayer().sendMessage(IronBall.getPrefix() + IronBall.getString("ARENA.NOENTRY"));
                return;
            }
        }
        Game game = plugin.getGameByPlayer(event.getPlayer());
        if (game != null && !game.getArena().getRegion().contains(loc)) {
            event.setTo(game.getTeam(event.getPlayer()).getTeamSpawn(game.getArena()));
        }
    }
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (plugin.getGameByPlayer(player) != null) {
                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                    public void run() {
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.setExhaustion(0);
                        player.setSaturation(20);
                        for (ItemStack is : player.getInventory().getArmorContents()) {
                            is.setDurability((short)0); // Repair armor
                        }
                    }
                }, 5L);
            }
        }
    }
}
