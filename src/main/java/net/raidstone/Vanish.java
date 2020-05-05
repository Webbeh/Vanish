package net.raidstone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @author weby@we-bb.com [Nicolas Glassey]
 * @version 1.0.0
 * @since 05/05/2020
 */
public class Vanish extends JavaPlugin implements Listener {
    private final Set<UUID> toHide = new HashSet<UUID>();
    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void checkIfNeedToVanish(PlayerLoginEvent event)
    {
        Player p = event.getPlayer();
        if(!p.hasPermission("is.mod")) return;
        if(!event.getHostname().toLowerCase().startsWith("vanish")) return;
        toHide.add(p.getUniqueId());
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void  joinVanished(PlayerJoinEvent event)
    {
        UUID u = event.getPlayer().getUniqueId();
        if(toHide.contains(u)) {
        event.setJoinMessage("");
        event.getPlayer().sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "You are vanished.");
            for(Player p : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(this, event.getPlayer());
            }
        } else {
            for(UUID uu : toHide)
            {
                Player p = Bukkit.getPlayer(uu);
                if(p==null) continue;
                
                event.getPlayer().hidePlayer(this,p);
            }
        }
    }
    
    @EventHandler
    public void serverPingList(ServerListPingEvent event)
    {
        //Remove this player from the server list names.
        Iterator<Player> it = event.iterator();
        while(it.hasNext())
        {
            Player p = it.next();
            if(toHide.contains(p.getUniqueId())) it.remove();
        }
    }
    
    @EventHandler (priority = EventPriority.HIGH)
    public void  quitVanished(PlayerQuitEvent event)
    {
        UUID u = event.getPlayer().getUniqueId();
        if(toHide.contains(u)) {
            event.setQuitMessage("");
    
            for (Player p : Bukkit.getOnlinePlayers())
                p.showPlayer(this, event.getPlayer());
            toHide.remove(u);
        } else {
            for(UUID uu : toHide)
            {
                Player p = Bukkit.getPlayer(uu);
                if(p==null) continue;
                event.getPlayer().showPlayer(this,p);
            }
        }
    }
}
