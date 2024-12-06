package me.mchiappinam.pdghguerreiro;

import me.mchiappinam.pdghguerreiro.Threads;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class Listeners implements Listener {
	private Main plugin;
	public Listeners(Main main) {
		plugin=main;
	}

	@EventHandler
	private void onDeath(PlayerDeathEvent e) {
		if(e.getEntity().getKiller() instanceof Player) {
			Player killer = e.getEntity().getKiller();
			if(plugin.participantes.contains(killer.getName())&&plugin.participantes.contains(e.getEntity().getName())) {
				int k = plugin.totalParticipantes.get(killer.getName());
				plugin.totalParticipantes.remove(killer.getName());
				plugin.totalParticipantes.put(killer.getName(), k+1);
				killer.sendMessage("§3§l[Guerreiro] §eVocê matou "+e.getEntity().getName()+" (total = "+(k+1)+")");
			}
		}
		plugin.removePlayer(e.getEntity(),1);
		plugin.checkGuerreiroEnd();
	}
	
	@EventHandler
	private void onQuit(PlayerQuitEvent e) {
		plugin.removePlayer(e.getPlayer(),2);
	}
	
	@EventHandler
	private void onKick(PlayerKickEvent e) {
		plugin.removePlayer(e.getPlayer(),2);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	private void onDamage(EntityDamageByEntityEvent e) {
		if(plugin.getguerreiroEtapa()!=0)
			if(e.getEntity() instanceof Player)
				if(e.getDamager() instanceof Player||e.getDamager() instanceof Projectile) {
					Player ent = (Player)e.getEntity();
					Player dam = null;
					if(e.getDamager() instanceof Player)
						dam=(Player)e.getDamager();
					else {
						Projectile a = (Projectile) e.getDamager();
						if(a.getShooter() instanceof Player)
							dam=(Player)a.getShooter();
					}
					if(plugin.participantes.contains(ent.getName()))
						if((plugin.getguerreiroEtapa()!=3)||(plugin.pvpOffNovaArena)) {
							e.setCancelled(true);
							if(dam!=null)
								dam.sendMessage("§3§l[Guerreiro] §4PvP desativado no momento!");
							return;
						}
				}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	private void onDamageP(PotionSplashEvent e) {
		for(Entity ent2 : e.getAffectedEntities())
			if(ent2 instanceof Player)
				if(plugin.getguerreiroEtapa()!=0) {
					Player ent = (Player)ent2;
					Player dam = null;
					if(e.getPotion().getShooter() instanceof Player)
						dam=(Player)e.getEntity().getShooter();
					if(plugin.participantes.contains(ent.getName()))
						if((plugin.getguerreiroEtapa()!=3)||(plugin.pvpOffNovaArena)) {
							e.setCancelled(true);
							if(dam!=null)
								dam.sendMessage("§3§l[Guerreiro] §4PvP desativado no momento!");
							return;
						}
				}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
	    if((!plugin.participantes.contains(e.getPlayer().getName()))&&(plugin.getguerreiroEtapa()>1)) {
	    	e.getPlayer().setAllowFlight(true);
	    	e.getPlayer().setFlying(true);
	    	if(e.getPlayer().getLocation().getBlockY()!=70.5) {
	    		e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), e.getPlayer().getLocation().getX(), 70.5, e.getPlayer().getLocation().getZ(), e.getPlayer().getLocation().getYaw(), e.getPlayer().getLocation().getPitch()));
	    		e.getPlayer().sendMessage("§3§l[Guerreiro] §cVocê não pode subir ou descer!");
	    	}
	        e.setCancelled(true);
	    }
	}
	@EventHandler
	private void playerChat(AsyncPlayerChatEvent e){
		e.setCancelled(true);
		if((e.getPlayer().hasPermission("pdgh.coordenador"))||(e.getPlayer().hasPermission("pdgh.construtor"))||(e.getPlayer().hasPermission("pdgh.youtuber"))) {
			if(e.getPlayer().hasPermission("coloredtags.youtuber")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§c");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.construtor")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§a");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.coordenador")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§a§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.moderador")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§2§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.admin")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§4§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.subdiretor")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§b§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.diretor")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§3§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.vicepresidente")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§b§l§n");
				return;
			}else if(e.getPlayer().hasPermission("coloredtags.presidente")) {
				plugin.chat(e.getPlayer(), e.getMessage(), "§3§l§n");
				return;
			}
		}else{
			e.getPlayer().sendMessage("§3§l[Guerreiro] §cApenas a STAFF pode enviar mensagens no chat!");
			return;
		}
	}
	
	@EventHandler
	private void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Threads t = new Threads(plugin,"join",e.getPlayer().getName());
		t.start();
	}

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
}
