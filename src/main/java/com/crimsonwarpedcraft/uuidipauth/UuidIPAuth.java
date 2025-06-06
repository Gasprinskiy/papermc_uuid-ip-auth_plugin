package com.crimsonwarpedcraft.uuidipauth;

import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import org.bukkit.Location;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.event.Listener;

public class UuidIPAuth extends JavaPlugin implements Listener {
  private List<PlayerEntry> players;

  private SpawnCoords getSpawnCoords() {
    File file = new File(Bukkit.getServer().getWorldContainer(), "spawn_coords.json");

    if (!file.exists()) {
      getLogger().warning("spawn_coords.json not found!");
      return null;
    }

    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
      Gson gson = new Gson();

      return gson.fromJson(reader, SpawnCoords.class);
    } catch (Exception e) {
      getLogger().severe("could not parse coords: " + e.getMessage());
      return null;
    }
  }

  private void loadPlayers() {
    File file = new File(Bukkit.getServer().getWorldContainer(), "players.json");

    if (!file.exists()) {
      getLogger().warning("players.json not found!");
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
      Gson gson = new Gson();
      Type listType = new TypeToken<List<PlayerEntry>>() {
      }.getType();

      players = gson.fromJson(reader, listType);
      getLogger().info("Loaded " + players.size() + " players from players.json");
    } catch (Exception e) {
      getLogger().severe("Failed to load players: " + e.getMessage());
    }
  }

  @Override
  public void onEnable() {
    loadPlayers();

    PaperLib.suggestPaper(this);

    getLogger().info("PlayerInfoLogger active!");
    getServer().getPluginManager().registerEvents(this, this);
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (players == null || players.isEmpty()) {
      player.kick(Component.text("You are not in the list, go fuck yourself"));
      return;
    }

    String uuid = player.getUniqueId().toString();
    String ip = event.getPlayer().getAddress().getAddress().getHostAddress();

    PlayerEntry foundPlayer = players.stream()
        .filter(p -> (p.getUuid() != null && p.getUuid().equals(uuid)) && (p.getIp() != null && p.getIp().equals(ip)))
        .findFirst()
        .orElse(null);

    if (foundPlayer == null) {
      player.kick(Component.text("You are not in the list, go fuck yourself"));
      return;
    }

    if (!player.hasPlayedBefore()) {
      Location spawnLocation;

      World world = Bukkit.getWorld("world");

      SpawnCoords confirCoords = getSpawnCoords();
      if (confirCoords != null) {
        spawnLocation = new Location(world, confirCoords.getX(), confirCoords.getY(), confirCoords.getZ());
      } else {
        spawnLocation = world.getSpawnLocation();
      }

      player.teleport(spawnLocation);
    }

    getLogger().info("Player" + foundPlayer.name + "connected");
  }
}
