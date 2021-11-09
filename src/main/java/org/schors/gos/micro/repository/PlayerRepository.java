package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.Player;

import java.util.List;

public interface PlayerRepository {
  List<Player> getAllPlayers();

  Player getPlayerById(String id);

  Player createPlayer(Player player);

  Player updatePlayer(String id, Player player);

  Boolean deletePlayer(String id);
}
