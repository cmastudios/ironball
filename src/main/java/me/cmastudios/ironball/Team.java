/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cmastudios.ironball;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Connor
 */
public class Team implements Iterable {

    private List<String> players;
    private final TeamType type;

    public Team(TeamType type, List<String> players) {
        this.type = type;
        this.players = players;
    }

    public Iterator iterator() {
        List<Player> ret = new ArrayList();
        for (String player : getPlayers()) {
            ret.add(Bukkit.getPlayerExact(player));
        }
        return ret.iterator();
    }

    public void addPlayer(Player player) {
        Validate.isTrue(!players.contains(player.getName()), "Team already contains player");
        getPlayers().add(player.getName());
    }

    public void removePlayer(Player player) {
        getPlayers().remove(player.getName());
    }

    public List<String> getPlayers() {
        return players;
    }

    public TeamType getType() {
        return type;
    }
}
