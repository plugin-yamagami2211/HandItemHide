package me.yama2211.hih;

import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        if(getConfig().getBoolean("Update")){
            new UpdateChecker(this,"HandItemHide").getVersion(version -> {
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().warning("利用可能なアップデートがあります。配布フォーラムをご確認ください。\nリンク:https://ym21.ml/amc4e");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void Flag(Player player,Boolean flag){
        if(flag){
            //player.setPlayerListName(ChatColor.BLACK + player.getPlayer().getName());
            //暗視付与
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION,1000000000,0));
            //プレイヤーを消す。
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle());
            for (Player ps : Bukkit.getOnlinePlayers()) {
                ps.hidePlayer(player);
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(packet);
            }
            //ログアウト偽造
            if(getConfig().getBoolean("FakeMessage")){
            String FakeLogout = getConfig().getString("MSG"+".Logout");
            FakeLogout = FakeLogout.replaceAll("%player",player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',FakeLogout));
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("MSG"+".flyOn")));
            return;
        } else {
            //暗視削除
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            //プレイヤー表示
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle());
            for (Player ps : Bukkit.getOnlinePlayers()) {
                ps.showPlayer(player);
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(packet);
            }
            //ログイン偽装
            if(getConfig().getBoolean("FakeMessage")) {
                String FakeLogin = getConfig().getString("MSG" + ".Login");
                FakeLogin = FakeLogin.replaceAll("%player", player.getName());
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', FakeLogin));
                }
            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("MSG"+".flyOff")));
        return;
        }
    @EventHandler
    public void onLogin(PlayerJoinEvent e){
        if(getConfig().getBoolean("LoginMessage")){
        e.setJoinMessage(null);
        String Login = getConfig().getString("MSG"+".Login");
        Login = Login.replaceAll("%player",e.getPlayer().getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',Login));
        }
    }
    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        if(getConfig().getBoolean("LogoutMessage")){
        e.setQuitMessage(null);
        String Logout = getConfig().getString("MSG"+".Logout");
        Logout = Logout.replaceAll("%player",e.getPlayer().getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',Logout));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes(
                    '&',getConfig().getString("MSG"+".NoPlayer")));
        }
        else {
            Player p = (Player) sender;

        if(cmd.getName().equalsIgnoreCase("sp")){
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "/sp <on | off>\n/sp reload");
                return true;
            }
                if (args.length == 1) {
                    if(!(sender.hasPermission("sp.use")))
                    {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                                '&',getConfig().getString("MSG"+".NoPex")));
                        return true;
                    } else {

                    //sp on
                    if (args[0].equalsIgnoreCase("on")) {
                        Flag(p,true);
                        return true;
                    }

                    //sp off
                    if (args[0].equalsIgnoreCase("off")) {
                        Flag(p,false);
                        return true;
                    }
                    //sp reload
                        if (args[0].equalsIgnoreCase("reload")) {
                            if(!(sender.hasPermission("sp.reload")))
                            {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes(
                                        '&',getConfig().getString("MSG"+".NoPex")));
                                return true;
                            } else {
                                reloadConfig();
                                sender.sendMessage(ChatColor.translateAlternateColorCodes(
                                        '&',getConfig().getString("MSG"+".Reload")));
                            }
                        }

                }

            }
        }

        return true;
    }
        return true;
    }

}
