package org.schors.gos;

import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import org.schors.gos.micro.model.Player;
import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiHandler {

    final PlayerRepository playerRepository;
    final BattleRepository battleRepository;

    public void getPlayers(RoutingContext rc) {
        rc.response().end(Json.encode(playerRepository.getAllPlayers()));
    }

    public void getPlayer(RoutingContext rc) {
        rc.response().end(Json.encode(playerRepository.getPlayerById(rc.pathParam("id"))));
    }

    public void createPlayer(RoutingContext rc) {
        Player player = rc.getBodyAsJson().mapTo(Player.class);
        rc.response().end(Json.encode(playerRepository.createPlayer(player)));
    }

    public void updatePlayer(RoutingContext rc) {
        Player player = rc.getBodyAsJson().mapTo(Player.class);
        rc.response().end(Json.encode(playerRepository.updatePlayer(rc.pathParam("id"), player)));
    }

  public void deletePlayer(RoutingContext rc) {
    rc.response().end(Json.encode(playerRepository.deletePlayer(rc.pathParam("id"))));
  }

  public void getCurrentWeek(RoutingContext rc) {
    rc.response().end(Json.encode(battleRepository.getCurrentWeek()));
  }

  public void deleteWeek(RoutingContext rc) {
    rc.response().end(Json.encode(battleRepository.deleteWeek(rc.pathParam("id"))));
  }

  public void updateWeek(RoutingContext rc) {
    Week week = rc.getBodyAsJson().mapTo(Week.class);
    rc.response().end(Json.encode(battleRepository.updateWeek(rc.pathParam("id"), week)));
  }

  public void addPlayerLayout(RoutingContext rc) {
    PlayerLayout playerLayout = rc.getBodyAsJson().mapTo(PlayerLayout.class);
    rc.response().end(Json.encode(battleRepository.addPlayerLayout(playerLayout)));
  }
}
