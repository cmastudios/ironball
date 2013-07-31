package me.cmastudios.ironball;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Persistence of arena and block data.
 *
 * @author Connor
 */
@Entity()
@Table(name = "ib_arena")
public class Arena {

    @Id
    private int id;
    @NotEmpty
    @Length(max = 16)
    private String name;
    private String redSpawn;
    private String blueSpawn;
    private String spectatorSpawn;
    private String refereeSpawn;
    private String golemSpawn;
    private String maxPoint;
    private String minPoint;
    private String redGoalMaxPoint;
    private String redGoalMinPoint;
    private String blueGoalMaxPoint;
    private String blueGoalMinPoint;
    private int maxScore;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRedSpawn() {
        return redSpawn;
    }

    public void setRedSpawn(String redSpawn) {
        this.redSpawn = redSpawn;
    }

    public String getBlueSpawn() {
        return blueSpawn;
    }

    public void setBlueSpawn(String blueSpawn) {
        this.blueSpawn = blueSpawn;
    }

    public String getSpectatorSpawn() {
        return spectatorSpawn;
    }

    public void setSpectatorSpawn(String spectatorSpawn) {
        this.spectatorSpawn = spectatorSpawn;
    }

    public String getRefereeSpawn() {
        return refereeSpawn;
    }

    public void setRefereeSpawn(String refereeSpawn) {
        this.refereeSpawn = refereeSpawn;
    }

    public static Location deserializeLocation(String input) {
        String[] args = input.split(":");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]),
                Double.parseDouble(args[2]), Double.parseDouble(args[3]),
                Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

    public static String serializeLocation(Location loc) {
        return new StringBuilder().append(loc.getWorld().getName()).append(':')
                .append(loc.getBlockX()).append(':').append(loc.getBlockY()).append(':')
                .append(loc.getBlockZ()).append(':').append(Math.round(loc.getPitch())).append(':')
                .append(Math.round(loc.getYaw())).toString();
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(String maxPoint) {
        this.maxPoint = maxPoint;
    }

    public String getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(String minPoint) {
        this.minPoint = minPoint;
    }

    public String getRedGoalMaxPoint() {
        return redGoalMaxPoint;
    }

    public void setRedGoalMaxPoint(String redGoalMaxPoint) {
        this.redGoalMaxPoint = redGoalMaxPoint;
    }

    public String getRedGoalMinPoint() {
        return redGoalMinPoint;
    }

    public void setRedGoalMinPoint(String redGoalMinPoint) {
        this.redGoalMinPoint = redGoalMinPoint;
    }

    public String getBlueGoalMaxPoint() {
        return blueGoalMaxPoint;
    }

    public void setBlueGoalMaxPoint(String blueGoalMaxPoint) {
        this.blueGoalMaxPoint = blueGoalMaxPoint;
    }

    public String getBlueGoalMinPoint() {
        return blueGoalMinPoint;
    }

    public void setBlueGoalMinPoint(String blueGialMinPoint) {
        this.blueGoalMinPoint = blueGialMinPoint;
    }

    public String getGolemSpawn() {
        return golemSpawn;
    }

    public void setGolemSpawn(String golemSpawn) {
        this.golemSpawn = golemSpawn;
    }

    public Region getRegion() {
        Vector vectorMinPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getMinPoint()));
        Vector vectorMaxPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getMaxPoint()));
        return new CuboidRegion(new BukkitWorld(this.getWorld()), vectorMinPoint, vectorMaxPoint);
    }

    public Region getRedGoalRegion() {
        Vector vectorMinPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getRedGoalMinPoint()));
        Vector vectorMaxPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getRedGoalMaxPoint()));
        return new CuboidRegion(new BukkitWorld(this.getWorld()), vectorMinPoint, vectorMaxPoint);
    }

    public Region getBlueGoalRegion() {
        Vector vectorMinPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getBlueGoalMinPoint()));
        Vector vectorMaxPoint = BukkitUtil.toVector(Arena.deserializeLocation(this.getBlueGoalMaxPoint()));
        return new CuboidRegion(new BukkitWorld(this.getWorld()), vectorMinPoint, vectorMaxPoint);
    }

    public World getWorld() {
        return Bukkit.getWorld(Arena.deserializeLocation(this.getMinPoint()).getWorld().getName());
    }

    /**
     * Get an arena by a position in the world. This method queries the
     * database.
     *
     * @param db Plugin database
     * @param loc Location of arena
     * @return arena or null if not found
     */
    public static Arena getArenaByLocation(EbeanServer db, Location loc) {
        List<Arena> arenas = db.find(Arena.class).findList();
        for (Arena arena : arenas) {
            if (arena.getRegion().contains(BukkitUtil.toVector(loc))) {
                return arena;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena) {
            Arena arena = (Arena) o;
            return arena.hashCode() == this.hashCode();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
