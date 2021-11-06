package org.schors.gos.service;

import org.schors.gos.model.Player;

import java.util.Collection;

public interface PlayerService {

    Player getPlayer(String id);
    Player createPlayer(Player player);
    Player updatePlayer(String id, Player player);
    Boolean deletePlayer(String id);
    Collection<Player> listAllPlayers();
}
