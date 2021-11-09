package org.schors.gos.micro.repository;

import org.schors.gos.micro.model.PlayerLayout;
import org.schors.gos.micro.model.Week;

public interface BattleRepository {
  Week getCurrentWeek();

  Boolean deleteWeek(String date);

  Week addPlayerLayout(PlayerLayout playerLayout);

  Week updateWeek(String date, Week newWeek);
}
