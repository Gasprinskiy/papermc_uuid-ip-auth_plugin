package com.crimsonwarpedcraft.uuidipauth;

public class PlayerEntry {
  public String name;
  public String uuid;
  public String ip;

  public PlayerEntry(String uuid, String username, String ip) {
    this.uuid = uuid;
    this.name = username;
    this.ip = ip;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public String getIp() {
    return ip;
  }
}