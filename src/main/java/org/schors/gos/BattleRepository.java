package org.schors.gos;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.model.PlayerLayout;
import org.schors.gos.model.Week;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.time.DayOfWeek.MONDAY;

@Component
@Slf4j
public class BattleRepository {

    private DB db;
    private ObjectMapper objectMapper;
    private ConcurrentMap<String, String> map;

    public BattleRepository(DB db, ObjectMapper objectMapper) {
        this.db = db;
        this.objectMapper = objectMapper;
        map = db.hashMap("weeks", Serializer.STRING, Serializer.STRING).createOrOpen();
    }

    @SneakyThrows
    public Week getCurrentWeek() {
        Week week;
        LocalDate weekStart = LocalDate.now().with(MONDAY);
        String date = weekStart.format(DateTimeFormatter.BASIC_ISO_DATE);
        String weekString = map.get(date);
        if (weekString == null) {
            week = new Week();
            week.setDate(date);
            week.setPlayerLayouts(new ArrayList<>());
            weekString = objectMapper.writeValueAsString(week);
            map.put(date, weekString);
        }
        return objectMapper.readValue(weekString, Week.class);
    }

    public Boolean deleteWeek(String date) {
        return map.remove(date) != null;
    }

    @SneakyThrows
    public Week addPlayerLayout(PlayerLayout playerLayout) {
        Week week = getCurrentWeek();
        boolean alreadyThere = week
            .getPlayerLayouts()
            .stream()
            .anyMatch(pl -> pl.getPlayer().getId().equals(playerLayout.getPlayer().getId()));
        if (alreadyThere) {
            List<PlayerLayout> newLayouts = week.getPlayerLayouts().stream()
                                                .filter(pl -> !pl.getPlayer()
                                                                 .getId()
                                                  .equals(playerLayout.getPlayer().getId()))
              .collect(Collectors.toList());
          newLayouts.add(playerLayout);
          week.setPlayerLayouts(newLayouts);
        } else {
          week.getPlayerLayouts().add(playerLayout);
        }
      map.put(week.getDate(), objectMapper.writeValueAsString(week));
      return week;
    }

  @SneakyThrows
  public Week updateWeek(String date, Week newWeek) {
    String weekString = map.get(date);
    if (weekString != null) {
      map.put(date, objectMapper.writeValueAsString(newWeek));
    }
    return newWeek;
  }
}
