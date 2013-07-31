package me.cmastudios.ironball;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.persistence.PersistenceException;
import me.cmastudios.ironball.command.*;
import me.cmastudios.ironball.listener.IronBallListener;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * IronBall Bukkit plugin main class.
 *
 * @author Connor
 */
public class IronBall extends JavaPlugin {

    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");
    public List<Game> activeGames = new ArrayList();
    public List<Player> arenaBypass = new ArrayList();
    public static ItemStack STICK;

    public IronBall() {
        STICK = new ItemStack(Material.STICK);
        STICK.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        ItemMeta meta = STICK.getItemMeta();
        meta.setDisplayName(IronBall.getString("STICK.NAME"));
        meta.setLore(Arrays.asList(IronBall.getString("STICK.LORE").split("\n")));
        STICK.setItemMeta(meta);
    }

    @Override
    public void onEnable() {
        try {
            for (Class dbClass : this.getDatabaseClasses()) {
                this.getDatabase().find(dbClass).findRowCount();
            }
        } catch (PersistenceException e) {
            this.installDDL();
        }
        this.getServer().getPluginCommand("ironballarena").setExecutor(new ArenaCommand(this));
        this.getServer().getPluginCommand("ironball").setExecutor(new GameCommand(this));
        this.getServer().getPluginManager().registerEvents(new IronBallListener(this), this);
    }

    @Override
    public void onDisable() {
        for (Game game : activeGames) {
            game.endGame();
        }
        activeGames.clear();
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(Arena.class);
        return classes;
    }

    /**
     * Get a localized message.
     *
     * @param key International identifier
     * @return localized string
     */
    public static String getString(String key) {
        Validate.notNull(key);
        Validate.notEmpty(key);
        return ChatColor.translateAlternateColorCodes('&', messages.getString(key)).replace("''", "'");
    }

    public static String getString(String key, Object[] args) {
        Validate.notNull(key);
        Validate.notEmpty(key);
        return MessageFormat.format(ChatColor.translateAlternateColorCodes('&', messages.getString(key)), args);
    }

    public static String getPrefix() {
        return getString("PREFIX");
    }

    /**
     * Get an active arena game by a player.
     *
     * @param player Arena game player
     * @return Game the player resides in, or null if the player is not in a
     * game.
     */
    public Game getGameByPlayer(Player player) {
        for (Game game : this.activeGames) {
            if (game.getPlayerMap().keySet().contains(player.getName())) {
                return game;
            }
        }
        return null;
    }

    /**
     * Get an active game by it's arena.
     *
     * @param arena Arena with active game
     * @return game that contains arena.
     */
    public Game getGameByArena(Arena arena) {
        for (Game game : this.activeGames) {
            if (game.getArena().equals(arena)) {
                return game;
            }
        }
        return null;
    }

    /**
     * Get an active game by location of the arena.
     *
     * @param loc Location inside the arena
     * @return game or null if no arena found at given location
     */
    public Game getGameByLocation(Location loc) {
        final Vector vec = BukkitUtil.toVector(loc);
        for (Game game : this.activeGames) {
            if (game.getArena().getWorld() != loc.getWorld()) {
                continue;
            }
            if (game.getArena().getRegion().contains(vec)) {
                return game;
            }
        }
        return null;
    }

    public void broadcast(String message, List<Player> players) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
}
