package me.cmastudios.ironball.command;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.cmastudios.ironball.Arena;
import me.cmastudios.ironball.Game;
import me.cmastudios.ironball.IronBall;
import me.cmastudios.ironball.SpawnType;
import me.cmastudios.ironball.TeamType;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {
    private final IronBall plugin;

    public ArenaCommand(IronBall aThis) {
        this.plugin = aThis;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }
        String arenaName = args[0];
        if (arenaName.length() > 16) {
            cs.sendMessage(plugin.getString("ARENA.NAMETOOLONG"));
            return true;
        }
        Arena arena = plugin.getDatabase().find(Arena.class).where().ieq("name", arenaName).findUnique();
        if (args.length == 1) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
            } else {
                cs.sendMessage(plugin.getPrefix() + plugin.getString("ARENA.INFO.1", new Object[] {arenaName}));
                cs.sendMessage(plugin.getPrefix() + plugin.getString("ARENA.INFO.2", new Object[] {arena.getMaxScore()}));
            }
        } else if (args.length == 2 && "set".equals(args[1])) {
            final Player player = plugin.getServer().getPlayerExact(cs.getName());
            if (player == null) {
                cs.sendMessage(plugin.getString("PLAYER404", new Object[] {cs.getName()}));
                return true;
            }
            WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
            Selection selection = worldEdit.getSelection(player);
            if (selection instanceof CuboidSelection) {
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();
                boolean newArena = arena == null;
                if (newArena) {
                    arena = plugin.getDatabase().createEntityBean(Arena.class);
                    arena.setName(arenaName);
                }
                arena.setMinPoint(Arena.serializeLocation(min));
                arena.setMaxPoint(Arena.serializeLocation(max));
                plugin.getDatabase().save(arena);
                cs.sendMessage(plugin.getPrefix() + plugin.getString("ARENA." + (newArena ? "CREATE" : "UPDATE"), new Object[] {arenaName}));
            } else {
                cs.sendMessage(plugin.getString("WE.BADSEL"));
            }
        } else if (args.length == 3 && "setgoal".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            String teamName = args[2];
            TeamType team;
            try {
                team = TeamType.valueOf(teamName.toUpperCase());
            } catch (IllegalArgumentException e) {
                cs.sendMessage(plugin.getString("TEAM.404"));
                return true;
            }
            final Player player = plugin.getServer().getPlayerExact(cs.getName());
            if (player == null) {
                cs.sendMessage(plugin.getString("PLAYER404", new Object[] {cs.getName()}));
                return true;
            }
            WorldEditPlugin worldEdit = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
            Selection selection = worldEdit.getSelection(player);
            if (selection instanceof CuboidSelection) {
                Location min = selection.getMinimumPoint();
                Location max = selection.getMaximumPoint();
                if (team == TeamType.RED) {
                    arena.setRedGoalMaxPoint(Arena.serializeLocation(max));
                    arena.setRedGoalMinPoint(Arena.serializeLocation(min));
                } else {
                    arena.setBlueGoalMaxPoint(Arena.serializeLocation(max));
                    arena.setBlueGoalMinPoint(Arena.serializeLocation(min));
                }
                plugin.getDatabase().save(arena);
                cs.sendMessage(plugin.getPrefix() + plugin.getString("TEAM.GOALUPDATE", new Object[] {team.toString().toLowerCase(), arenaName}));
            } else {
                cs.sendMessage(plugin.getString("WE.BADSEL"));
            }
        } else if (args.length == 3 && "setspawn".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            String spawnTypeName = args[2];
            SpawnType spawnType;
            try {
                spawnType = SpawnType.valueOf(spawnTypeName.toUpperCase());
            } catch (IllegalArgumentException e) {
                cs.sendMessage(plugin.getString("SPAWN.TYPE404"));
                return true;
            }
            final Player player = plugin.getServer().getPlayerExact(cs.getName());
            if (player == null) {
                cs.sendMessage(plugin.getString("PLAYER404", new Object[] {cs.getName()}));
                return true;
            }
            switch (spawnType) {
                case RED:
                    arena.setRedSpawn(Arena.serializeLocation(player.getLocation()));
                    break;
                case BLUE:
                    arena.setBlueSpawn(Arena.serializeLocation(player.getLocation()));
                    break;
                case SPECTATOR:
                    arena.setSpectatorSpawn(Arena.serializeLocation(player.getLocation()));
                    break;
//                case REFEREE:
//                    arena.setRefereeSpawn(Arena.serializeLocation(player.getLocation()));
//                    break;
                case GOLEM:
                    arena.setGolemSpawn(Arena.serializeLocation(player.getLocation()));
                    break;
            }
            plugin.getDatabase().save(arena);
            cs.sendMessage(plugin.getPrefix() + plugin.getString("SPAWN.UPDATE", new Object[] {spawnType.toString().toLowerCase(), arenaName}));
        } else if (args.length == 3 && "maxscore".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            int score;
            try {
                score = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                cs.sendMessage(plugin.getString("NUMSPEC"));
                return true;
            }
            arena.setMaxScore(score);
            plugin.getDatabase().save(arena);
            cs.sendMessage(plugin.getPrefix() + plugin.getString("OPT.UPDATE", new Object[] {"maxscore", arenaName}));
        } else if (args.length == 3 && "rename".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            arena.setName(args[2]);
            plugin.getDatabase().save(arena);
            cs.sendMessage(plugin.getPrefix() + plugin.getString("OPT.UPDATE", new Object[] {"name", arenaName}));
        } else if (args.length == 2 && "delete".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            plugin.getDatabase().delete(arena);
            cs.sendMessage(plugin.getPrefix() + plugin.getString("ARENA.DELETE", new Object[] {arenaName}));
        } else if (args.length == 2 && "list".equals(args[1])) {
            StringBuilder str = new StringBuilder(plugin.getPrefix() + plugin.getString("ARENA.LIST"));
            for (Arena arenaListItem : plugin.getDatabase().find(Arena.class).findList()) {
                str.append(arenaListItem.getName()).append(", ");
            }
            cs.sendMessage(str.toString());
        } else if (args.length == 2 && "stop".equals(args[1])) {
            if (arena == null) {
                cs.sendMessage(plugin.getString("ARENA.404", new Object[] {arenaName}));
                return true;
            }
            for (Game game : plugin.activeGames) {
                if (game.getArena().getName().equals(arenaName)) {
                    game.endGame();
                }
            }
        } else {
            return false;
        }
        return true;
    }
    
}
