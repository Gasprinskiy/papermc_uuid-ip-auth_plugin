package com.crimsonwarpedcraft.uuidipauth;

public class PlayerEntry {
  public String username;
  public String uuid;
  public String ip;

  public PlayerEntry(String uuid, String username, String ip) {
    this.uuid = uuid;
    this.username = username;
    this.ip = ip;
  }

  public String getUuid() {
    return uuid;
  }

  public String getUsername() {
    return username;
  }

  public String getIp() {
    return ip;
  }
}