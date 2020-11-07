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

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        if(getConfig().getBoolean("Update")){
            new UpdateChecker(this,"HandItemHide").getVersion(version -> {
                if (!this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("利用可能なアップデートがあります。配布フォーラムをご確認ください。");
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

            //プレイヤーを消す。
            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer)player).getHandle());
            for (Player ps : Bukkit.getOnlinePlayers()) {
                ps.hidePlayer(player);
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(packet);
            }
            if(getConfig().getBoolean("FakeMessage")){
            String FakeLogout = getConfig().getString("MSG"+".Logout");
            FakeLogout = FakeLogout.replaceAll("%player",player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',FakeLogout));

            }
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',getConfig().getString("MSG"+".flyOn")));
            return;
        } else {
            player.setPlayerListName(player.getPlayer().getName());
            //player.removePotionEffect(PotionEffectType.INVISIBILITY);

            PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer)player).getHandle());
            for (Player ps : Bukkit.getOnlinePlayers()) {
                ps.showPlayer(player);
                ((CraftPlayer)ps).getHandle().playerConnection.sendPacket(packet);
            }

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
        e.setJoinMessage(null);
        String Login = getConfig().getString("MSG"+".Login");
        Login = Login.replaceAll("%player",e.getPlayer().getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',Login));
    }
    @EventHandler
    public void onLogout(PlayerQuitEvent e){
        e.setQuitMessage(null);
        String Logout = getConfig().getString("MSG"+".Logout");
        Logout = Logout.replaceAll("%player",e.getPlayer().getName());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',Logout));

    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        Player p = ( sender instanceof Player ) ? ( Player )sender:( Player )null;

        if(cmd.getName().equalsIgnoreCase("sp")){
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "/sp <on | off>\n/sp relad");
                return true;
            }
                if (args.length == 1) {
                    if(!(sender.hasPermission("sp.use")))
                    {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes(
                                '&',getConfig().getString("MSG"+".NoPex")));
                        return true;
                    } else {

                    if(p == null) return false;
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

}
