package org.schors.gos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.model.Player;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PlayerRepository {

    private DB db;
    private ObjectMapper objectMapper;
    private ConcurrentMap<String, String> map;

    public PlayerRepository(DB db, ObjectMapper objectMapper) {
        this.db = db;
        this.objectMapper = objectMapper;
        map = db.hashMap("player", Serializer.STRING, Serializer.STRING).createOrOpen();
    }

    public List<Player> getAllPlayers() {
        return map
            .values()
            .stream()
            .map(s -> {
                try {
                    return objectMapper.readValue(s, Player.class);
                } catch (JsonProcessingException e) {
                    log.warn(e.getMessage(), e);
                }
                return null;
            })
            .filter(player -> player != null)
            .collect(Collectors.toList());
    }

    public Player getPlayerById(String id) {
        try {
            return objectMapper.readValue(map.get(id), Player.class);
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    public Player createPlayer(Player player) {
        if (player.getId() == null) {
            player.setId(String.valueOf(player.getName().hashCode()));
        }
        try {
            map.putIfAbsent(player.getId(), objectMapper.writeValueAsString(player));
            return player;
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    public Player updatePlayer(String id, Player player) {
        try {
            map.put(id, objectMapper.writeValueAsString(player));
            return player;
        } catch (JsonProcessingException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    public Boolean deletePlayer(String id) {
        return map.remove(id) != null;
    }
}
