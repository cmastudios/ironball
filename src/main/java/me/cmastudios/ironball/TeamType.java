package me.cmastudios.ironball;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * IronBall teams. Red represents the home team. Blue represents the away team.
 *
 * @author Connor
 */
public enum TeamType {

    RED,
    BLUE;

    public ItemStack getColoredArmorPiece(Material piece) {
        ItemStack helmet = new ItemStack(piece);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(this == TeamType.RED ? Color.RED : Color.BLUE);
        helmet.setItemMeta(meta);
        return helmet;
    }

    public void addItems(Player player) {
        player.getInventory().setHelmet(this.getColoredArmorPiece(Material.LEATHER_HELMET));
        player.getInventory().setChestplate(this.getColoredArmorPiece(Material.LEATHER_CHESTPLATE));
        player.getInventory().setLeggings(this.getColoredArmorPiece(Material.LEATHER_LEGGINGS));
        player.getInventory().setBoots(this.getColoredArmorPiece(Material.LEATHER_BOOTS));
        player.getInventory().addItem(IronBall.STICK);
    }

    public Location getTeamSpawn(Arena arena) {
        if (this == RED) {
            return Arena.deserializeLocation(arena.getRedSpawn());
        } else {
            return Arena.deserializeLocation(arena.getBlueSpawn());
        }
    }

    public String getDisplayName() {
        if (this.equals(RED)) {
            return ChatColor.RED + "red";
        } else {
            return ChatColor.BLUE + "blue";
        }
    }

    public TeamType getOtherTeam() {
        return this == RED ? BLUE : RED;
    }
}
