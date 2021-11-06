package org.schors.gos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.mapdb.DB;
import org.mapdb.Serializer;
import org.schors.gos.model.Player;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private DB db;
    private ConcurrentMap<String, String> map;
    private ObjectMapper objectMapper;

    public PlayerServiceImpl(DB db, ObjectMapper objectMapper) {
        this.db = db;
        this.objectMapper = objectMapper;
        map = db.hashMap("player", Serializer.STRING, Serializer.STRING).createOrOpen();
    }

    @SneakyThrows
    @Override
    public Player getPlayer(String id) {
        String data = map.get(id);
        return objectMapper.readValue(data, Player.class);
    }

    @SneakyThrows
    @Override
    public Player createPlayer(Player player) {
        map.putIfAbsent(player.getId(), objectMapper.writeValueAsString(player));
        return player;
    }

    @SneakyThrows
    @Override
    public Player updatePlayer(String id, Player player) {
                map.put(player.getId(), objectMapper.writeValueAsString(player));
        return player;
    }

    @Override
    public Boolean deletePlayer(String id) {
        return map.remove(id) != null;
    }

    @Override
    public Collection<Player> listAllPlayers() {
        return map.values().stream().map(s -> {
            try {
                return objectMapper.readValue(s, Player.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }
}
