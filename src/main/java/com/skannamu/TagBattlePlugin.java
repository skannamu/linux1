package com.yourname.tagbattle;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TagBattlePlugin extends JavaPlugin implements Listener {

    private final Map<UUID, Integer> playerLives = new HashMap<>();
    private final Set<UUID> eliminated = new HashSet<>();
    private final int MAX_LIVES = 5;
    private boolean gameStarted = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("TagBattlePlugin 활성화됨.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TagBattlePlugin 비활성화됨.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!playerLives.containsKey(player.getUniqueId())) {
            resetPlayer(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!gameStarted) {
            event.setCancelled(true);
            return;
        }

        UUID vUUID = victim.getUniqueId();
        if (eliminated.contains(vUUID)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true); // 직접 데미지를 처리하므로 기본 데미지는 막기

        int lives = playerLives.getOrDefault(vUUID, MAX_LIVES);
        lives--;
        playerLives.put(vUUID, lives);

        Bukkit.broadcastMessage(ChatColor.YELLOW + victim.getName() + "의 목숨이 " + lives + "개 남았습니다.");

        if (lives <= 0) {
            eliminated.add(vUUID);
            victim.setGameMode(GameMode.CREATIVE);
            Bukkit.broadcastMessage(ChatColor.RED + victim.getName() + "은(는) 탈락하였습니다!");
            checkWinner();
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    teleportRandom(victim);
                    victim.setHealth(victim.getMaxHealth());
                }
            }.runTaskLater(this, 1L);
        }
    }

    private void resetPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        playerLives.put(uuid, MAX_LIVES);
        eliminated.remove(uuid);
        player.setGameMode(GameMode.SURVIVAL);
        teleportToSpawn(player);
        player.setHealth(player.getMaxHealth());
        player.sendMessage(ChatColor.AQUA + "당신은 다시 게임에 참가하였습니다! 목숨: " + MAX_LIVES);
    }

    private void teleportToSpawn(Player player) {
        World world = player.getWorld();
        Location spawn = new Location(world, 0.5, world.getHighestBlockYAt(0, 0) + 1, 0.5);
        player.teleport(spawn);
    }

    private void teleportRandom(Player player) {
        World world = player.getWorld();
        Random rand = new Random();
        int x = rand.nextInt(100) - 50;
        int z = rand.nextInt(100) - 50;
        int y = world.getHighestBlockYAt(x, z) + 1;
        player.teleport(new Location(world, x + 0.5, y, z + 0.5));
    }

    private void checkWinner() {
        List<UUID> alive = new ArrayList<>();
        for (UUID uuid : playerLives.keySet()) {
            if (!eliminated.contains(uuid)) {
                alive.add(uuid);
            }
        }

        if (alive.size() == 1) {
            Player winner = Bukkit.getPlayer(alive.get(0));
            if (winner != null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + "🎉 " + winner.getName() + "가 우승하였습니다!! 🎉");
                winner.setGameMode(GameMode.CREATIVE);
                gameStarted = false;
            }
        }
    }

    private void startGame(CommandSender sender) {
        if (gameStarted) {
            sender.sendMessage(ChatColor.RED + "이미 게임이 시작되었습니다.");
            return;
        }

        gameStarted = true;

        for (Player p : Bukkit.getOnlinePlayers()) {
            resetPlayer(p);
            teleportRandom(p);
            p.sendMessage(ChatColor.AQUA + "게임 시작 준비 중입니다. 잠시 대기해주세요...");
            p.setWalkSpeed(0f);
        }

        new BukkitRunnable() {
            int count = 5;

            @Override
            public void run() {
                if (count == 0) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendMessage(ChatColor.GREEN + "게임 시작!");
                        p.setWalkSpeed(0.2f); // 기본 속도로 복원
                    }
                    cancel();
                } else {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "" + count + "...");
                    count--;
                }
            }
        }.runTaskTimer(this, 0L, 20L); // 1초 간격
    }

    private void endGame(CommandSender sender) {
        if (!gameStarted) {
            sender.sendMessage(ChatColor.RED + "게임이 시작되지 않았습니다.");
            return;
        }

        gameStarted = false;

        for (Player p : Bukkit.getOnlinePlayers()) {
            resetPlayer(p);
        }

        Bukkit.broadcastMessage(ChatColor.RED + "게임이 종료되었습니다.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetgame")) {
            if (args.length == 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    resetPlayer(p);
                }
                sender.sendMessage(ChatColor.GREEN + "모든 플레이어가 초기화되었습니다.");
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    resetPlayer(target);
                    sender.sendMessage(ChatColor.GREEN + target.getName() + " 초기화 완료.");
                } else {
                    sender.sendMessage(ChatColor.RED + "해당 플레이어를 찾을 수 없습니다.");
                }
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("startgame")) {
            startGame(sender);
            return true;
        }

        if (command.getName().equalsIgnoreCase("endgame")) {
            endGame(sender);
            return true;
        }

        return false;
    }
}