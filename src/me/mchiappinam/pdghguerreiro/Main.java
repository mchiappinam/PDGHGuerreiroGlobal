package me.mchiappinam.pdghguerreiro;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

public class Main extends JavaPlugin {
    private GhostManager manager;
    
	private int guerreiroEtapa = 0;
	private int diaAutoStart;
	private int horaAutoStart;
	private int minAutoStart;
	protected boolean canStart = true;
	
	protected Location spawn;
	protected Location saida;
	protected Location camarote;
	protected Location arenaMenor;

	protected boolean jaTeleportado = false;
	protected boolean pvpOffNovaArena = false;
	protected boolean comecouComMaisDeVinte = false;
	protected boolean darItens = true;
	int tteleportarNovaArena;
	int tliberarPvPNovaArena;
	
	//Mysql
    protected String mysql_url = "";
    protected String mysql_user = "";
    protected String mysql_pass = "";
    protected boolean flatfile = true;
	
	protected HashMap<String,Integer> totalParticipantes = new HashMap<String,Integer>();
	protected List<String> participantes = new ArrayList<String>();
	protected List<String> vips = new ArrayList<String>();
	
	@Override
    public void onEnable() {
		getServer().getConsoleSender().sendMessage("§3[Guerreiro] §2dativando... - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[Guerreiro] §2Acesse: http://pdgh.com.br/");
		
		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		getServer().getPluginCommand("guerreiro").setExecutor(new Comando(this));
		
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}
			catch(Exception e) {}
		}
		
        manager = new GhostManager(this);
		
		diaAutoStart = Utils.strToCalendar(getConfig().getString("AutoStart.Dia"));
		getServer().getConsoleSender().sendMessage("§2<> Data automatica:");
		getServer().getConsoleSender().sendMessage("§2Dia = "+diaAutoStart);
		horaAutoStart = Integer.parseInt(getConfig().getString("AutoStart.Hora").substring(0,2));
		minAutoStart = Integer.parseInt(getConfig().getString("AutoStart.Hora").substring(2,4));
		getServer().getConsoleSender().sendMessage("§2Hora = "+(horaAutoStart<10?"0"+horaAutoStart:horaAutoStart)+":"+(minAutoStart<10?"0"+minAutoStart:minAutoStart));
		
		String ent[] = getConfig().getString("Arena.Entrada").split(";");
		spawn = new Location(getServer().getWorld(ent[0]),Double.parseDouble(ent[1]),Double.parseDouble(ent[2]),Double.parseDouble(ent[3]),Float.parseFloat(ent[4]),Float.parseFloat(ent[5]));
		String sai[] = getConfig().getString("Arena.Saida").split(";");
		saida = new Location(getServer().getWorld(sai[0]),Double.parseDouble(sai[1]),Double.parseDouble(sai[2]),Double.parseDouble(sai[3]),Float.parseFloat(sai[4]),Float.parseFloat(sai[5]));
		String cam[] = getConfig().getString("Arena.Camarote").split(";");
		camarote = new Location(getServer().getWorld(cam[0]),Double.parseDouble(cam[1]),Double.parseDouble(cam[2]),Double.parseDouble(cam[3]),Float.parseFloat(cam[4]),Float.parseFloat(cam[5]));
		String men[] = getConfig().getString("Arena.Menor").split(";");
		arenaMenor = new Location(getServer().getWorld(men[0]),Double.parseDouble(men[1]),Double.parseDouble(men[2]),Double.parseDouble(men[3]),Float.parseFloat(men[4]),Float.parseFloat(men[5]));
		
		getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)==diaAutoStart)
					if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==horaAutoStart)
						if(Calendar.getInstance().get(Calendar.MINUTE)==minAutoStart)
							prepareGuerreiro();
			}
		}, 0, 700);
		
		/**getServer().getScheduler().runTaskTimer(this, new Runnable() {
			public void run() {
				for(Player p : getServer().getOnlinePlayers()) {
					if(core1.getClanManager().getClanPlayer(p.getName())!=null&&sb.getTeam(p.getName().toLowerCase())==null) {
						Team t = sb.registerNewTeam(p.getName().toLowerCase());
						t.setPrefix(formatTag(p));
						t.addPlayer(p);
					}
					else if(core1.getClanManager().getClanPlayer(p.getName())!=null&&sb.getTeam(p.getName().toLowerCase())!=null) {
						Team t = sb.getPlayerTeam(p);
						t.setPrefix(formatTag(p));
					}
					else if(core1.getClanManager().getClanPlayer(p.getName())==null&&sb.getTeam(p.getName().toLowerCase())!=null) {
						sb.getTeam(p.getName().toLowerCase()).unregister();
					}
				}
			}
		}, getConfig().getInt("Update")*20, getConfig().getInt("Update")*20);*/
	}
	
	@Override
    public void onDisable() {
		getServer().getConsoleSender().sendMessage("§3[Guerreiro] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[Guerreiro] §2Acesse: http://pdgh.com.br/");
	}
	
	
	protected void prepareGuerreiro() {
		String ent[] = getConfig().getString("Arena.Entrada").split(";");
		spawn = new Location(getServer().getWorld(ent[0]),Double.parseDouble(ent[1]),Double.parseDouble(ent[2]),Double.parseDouble(ent[3]),Float.parseFloat(ent[4]),Float.parseFloat(ent[5]));
		String sai[] = getConfig().getString("Arena.Saida").split(";");
		saida = new Location(getServer().getWorld(sai[0]),Double.parseDouble(sai[1]),Double.parseDouble(sai[2]),Double.parseDouble(sai[3]),Float.parseFloat(sai[4]),Float.parseFloat(sai[5]));
		String cam[] = getConfig().getString("Arena.Camarote").split(";");
		camarote = new Location(getServer().getWorld(cam[0]),Double.parseDouble(cam[1]),Double.parseDouble(cam[2]),Double.parseDouble(cam[3]),Float.parseFloat(cam[4]),Float.parseFloat(cam[5]));
		String men[] = getConfig().getString("Arena.Menor").split(";");
		arenaMenor = new Location(getServer().getWorld(men[0]),Double.parseDouble(men[1]),Double.parseDouble(men[2]),Double.parseDouble(men[3]),Float.parseFloat(men[4]),Float.parseFloat(men[5]));
		if(guerreiroEtapa!=0)
			return;
		guerreiroEtapa=1;
		tirarTagsAntigas();
		messagePrepare(getConfig().getInt("Timers.Preparar.Avisos"));
	}
	private void messagePrepare(final int vezes) {
		canStart=true;
		if(guerreiroEtapa!=1)
			return;
		canStart=false;
		if(vezes==0) {
			preparedGuerreiro();
		}
		else {
			getServer().broadcastMessage(" ");
			getServer().broadcastMessage("§3§l[Guerreiro] §eEvento guerreiro automático começando!");
			getServer().broadcastMessage("§3§l[Guerreiro] §ePara participar digite: §6§l/guerreiro");
			getServer().broadcastMessage("§3§l[Guerreiro] §eLista dos premios: http://pdgh.com.br/guerreiro");
			getServer().broadcastMessage("§3§l[Guerreiro] §eTempo restante: §c"+vezes*getConfig().getInt("Timers.Preparar.TempoEntre")+" segundos");
			getServer().broadcastMessage("§3§l[Guerreiro] §eJogadores: "+participantes.size());
			getServer().broadcastMessage(" ");
		}
		getServer().getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				canStart=true;
				if(guerreiroEtapa!=1)
					return;
				canStart=false;
				messagePrepare(vezes-1);
			}
		}, 20*getConfig().getInt("Timers.Preparar.TempoEntre"));
	}
	
	
	
	
	
	protected void preparedGuerreiro() {
		if(participantes.size()<2) {
			cancelGurerreiro();
			cancelGurerreiro();
			getServer().broadcastMessage(" ");
			getServer().broadcastMessage("§3§l[Guerreiro] §eEvento guerreiro automático §cCANCELADO!");
			getServer().broadcastMessage("§3§l[Guerreiro] §eMotivo: Quantidade de jogadores menor que 2");
			getServer().broadcastMessage(" ");
			return;
		}
		guerreiroEtapa=2;
		getServer().broadcastMessage(" ");
		getServer().broadcastMessage("§3§l[Guerreiro] §eEvento guerreiro sendo INICIADO!");
		getServer().broadcastMessage("§3§l[Guerreiro] §eTeleporte para o evento BLOQUEADO!");
		getServer().broadcastMessage(" ");
		canStart=false;
		messageIniciando(getConfig().getInt("Timers.Iniciando.Avisos"));
	}
	private void messageIniciando(final int vezes) {
		canStart=true;
		if(guerreiroEtapa!=2)
			return;
		canStart=false;
		if(vezes==0)
			startGuerreiro();
		else {
			sendMessageGuerreiro(" ");
			sendMessageGuerreiro("§3§l[Guerreiro] §eEvento guerreiro automático começando!");
			sendMessageGuerreiro("§3§l[Guerreiro] §eTempo inicial para os jogadores se prepararem!");
			sendMessageGuerreiro("§3§l[Guerreiro] §eTempo restante: §c"+vezes*getConfig().getInt("Timers.Iniciando.TempoEntre")+" segundos");
			sendMessageGuerreiro(" ");
		}
		getServer().getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				canStart=true;
				if(guerreiroEtapa!=2)
					return;
				canStart=false;
				messageIniciando(vezes-1);
			}
		}, 20*getConfig().getInt("Timers.Iniciando.TempoEntre"));
	}
	
	
	
	
	
	protected void startGuerreiro() {
		canStart=true;
		guerreiroEtapa=3;
		if(participantes.size()>20)
			comecouComMaisDeVinte=true;
		sendMessageGuerreiro(" ");
		sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
		sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
		sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
		sendMessageGuerreiro(" ");
	}
	
	
	
	
	
	protected void checkGuerreiroEnd() {
		if(participantes.size()==1)
			if(guerreiroEtapa==3) {
				guerreiroEtapa=4;
				cteleportarNovaArena();
				cliberarPvPNovaArena();
				jaTeleportado = false;
				pvpOffNovaArena = false;
				comecouComMaisDeVinte = false;
				//econ.depositPlayer(cp.getName(), premio);
				String v1 = null;
				for(String p : participantes)
					v1=p;
				int v1_v = -1;
				for(String n : totalParticipantes.keySet())
					if(n.equals(v1))
						v1_v = totalParticipantes.get(n);
				
				darTagsNovas(v1);
				getServer().broadcastMessage(" ");
				getServer().broadcastMessage("§3§l[Guerreiro] §eEvento guerreiro FINALIZADO!");
				getServer().broadcastMessage("§3§l[Guerreiro] §eJogador vencedor: §l"+v1);
				getServer().broadcastMessage("§3§l[Guerreiro] §eLista dos premios: http://pdgh.com.br/guerreiro");
				getServer().broadcastMessage("§3§l[Guerreiro] §eTag §5§l[Guerreiro]§e para "+v1+" ("+v1_v+")");
				getServer().broadcastMessage(" ");
				finalizarGuerreiro();
				return;
			}
	}
	
	
	
	
	protected void finalizarGuerreiro() {
		/**sendMessageGuerreiro(" ");
		sendMessageGuerreiro("§3§l[Guerreiro] §eTempo esgotado!");
		sendMessageGuerreiro("§3§l[Guerreiro] §eFim do evento!");
		sendMessageGuerreiro(" ");*/
		cancelGurerreiro();
	}
	
	protected void darTagsNovas(String v1) {
		getConfig().set("Vencedor", v1.toLowerCase());
		saveConfig();
	}
	
	protected void tirarTagsAntigas() {
		getConfig().set("Vencedor", null);
		saveConfig();
	}
	
	protected void cancelGurerreiro() {
		if(guerreiroEtapa==0)
			return;
		guerreiroEtapa=0;
		for(String n : participantes) {
			getServer().getPlayer(n).teleport(saida);
		}
		participantes.clear();
		totalParticipantes.clear();
		canStart=true;
	}
	
	protected void chat(Player p, String msg, String cor) {
		String args[] = msg.split(" ");
		if((args[0].equalsIgnoreCase("stf"))&&(p.hasPermission("pdgh.coordenador"))) {
			for(Player staff : getServer().getOnlinePlayers())
				if(staff.hasPermission("pdgh.coordenador"))
					staff.sendMessage("§b§l[CHAT STF]§b "+p.getName()+": "+msg.replaceFirst(args[0], "").replaceAll("\\s+"," ").trim());
			return;
		}
		String corArg[] = cor.split("§");
		getServer().broadcastMessage("§7[STAFF] "+cor+p.getName()+"§7: §"+corArg[1]+msg.replaceAll("\\s+"," ").replaceAll("&", "§").trim());
	}
	
	protected int getguerreiroEtapa() {
		return guerreiroEtapa;
	}
	
	@SuppressWarnings("deprecation")
	protected void addPlayer(Player p) {
		p.sendMessage("§3§l[Guerreiro] §eVocê entrou através do servidor (colocar-servidor).");
		if(guerreiroEtapa>1) {
			manager.addGhost(p);
			p.sendMessage("§3§l[Guerreiro] §cO evento já começou. Você está no modo espectador.");
			return;
		}
		if(darItens) {
			clearInv(p);
			p.setFoodLevel(20);
		}
		totalParticipantes.put(p.getName(), 0);
		participantes.add(p.getName());
		p.teleport(spawn);
		p.sendMessage(" ");
		p.sendMessage("§3§l[Guerreiro] §eVocê entrou no evento guerreiro!");
		p.sendMessage("§3§l[Guerreiro] §cPara sair digite: §c§l/guerreiro sair");
		p.sendMessage("§3§l[Guerreiro] §eAgrupe-se com seu clan enquanto o evento está iniciando!");
		p.sendMessage(" ");
		if(darItens) {
			clearInv(p);
			Kit(p);
		    p.updateInventory();
		    for(PotionEffect effect : p.getActivePotionEffects()) {
		    	p.removePotionEffect(effect.getType());
		    	p.sendMessage("§3§l[Guerreiro] §ePoção §6"+effect.getType().getName()+" §eremovida.");
		    }
		}else{
			if(p.hasPermission("pdgh.admin"))
				return;
			if(!vips.contains(p.getName().toLowerCase())) {
				getServer().broadcastMessage("§3§l[Guerreiro] §6§l"+p.getName()+" §eé VIP e entrou no evento guerreiro.");
				vips.add(p.getName().toLowerCase());
			}
		}
	}
	
	public void clearInv(Player p) {
		p.closeInventory();
		p.closeInventory();
		p.closeInventory();
		p.closeInventory();
		p.closeInventory();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.getInventory().clear();
	}
	
	public void Kit(Player p) {
		if(p.hasPermission("pdgh.vip")) {
			ItemStack espada = new ItemStack(Material.DIAMOND_SWORD, 1);
			espada.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
			espada.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
			ItemStack arco = new ItemStack(Material.BOW, 1);
			arco.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE , 5);
			arco.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
			arco.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
			arco.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
			ItemStack elmo = new ItemStack(Material.DIAMOND_HELMET, 1);
			elmo.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 6);
			elmo.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			ItemStack peito = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
			peito.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 6);
			peito.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			ItemStack calca = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
			calca.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 6);
			calca.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			ItemStack bota = new ItemStack(Material.DIAMOND_BOOTS, 1);
			bota.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 6);
			bota.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
			p.getInventory().addItem(espada);
			p.getInventory().addItem(arco);
			p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 30, (short) 1));
			p.getInventory().addItem(new ItemStack(Material.POTION, 20, (short) 8233));
			p.getInventory().addItem(new ItemStack(Material.POTION, 20, (short) 8226));
			p.getInventory().addItem(elmo);
			p.getInventory().addItem(peito);
			p.getInventory().addItem(calca);
			p.getInventory().addItem(bota);
			p.getInventory().setHelmet(elmo);
			p.getInventory().setChestplate(peito);
			p.getInventory().setLeggings(calca);
			p.getInventory().setBoots(bota);
			p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			
			if(!vips.contains(p.getName().toLowerCase())) {
				getServer().broadcastMessage("§3§l[Guerreiro] §6§l"+p.getName()+" §eé VIP e ganhou em toda sua armadura +2 leveis de encantamento, +1 armadura completa e o dobro de todas as poções.");
				vips.add(p.getName().toLowerCase());
			}
			
		}else{
		    ItemStack espada = new ItemStack(Material.DIAMOND_SWORD, 1);
		    espada.addEnchantment(Enchantment.DAMAGE_ALL, 5);
		    espada.addEnchantment(Enchantment.FIRE_ASPECT, 2);
		    ItemStack arco = new ItemStack(Material.BOW, 1);
		    arco.addEnchantment(Enchantment.ARROW_DAMAGE , 5);
		    arco.addEnchantment(Enchantment.ARROW_FIRE, 1);
		    arco.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		    arco.addEnchantment(Enchantment.DURABILITY, 3);
		    ItemStack elmo = new ItemStack(Material.DIAMOND_HELMET, 1);
		    elmo.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
		    elmo.addEnchantment(Enchantment.DURABILITY, 3);
		    ItemStack peito = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
		    peito.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
		    peito.addEnchantment(Enchantment.DURABILITY, 3);
		    ItemStack calca = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
		    calca.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
		    calca.addEnchantment(Enchantment.DURABILITY, 3);
		    ItemStack bota = new ItemStack(Material.DIAMOND_BOOTS, 1);
		    bota.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL , 4);
		    bota.addEnchantment(Enchantment.DURABILITY, 3);
		    p.getInventory().addItem(espada);
		    p.getInventory().addItem(arco);
		    p.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 30, (short) 1));
		    p.getInventory().addItem(new ItemStack(Material.POTION, 10, (short) 8233));
		    p.getInventory().addItem(new ItemStack(Material.POTION, 10, (short) 8226));
		    p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
			p.getInventory().setHelmet(elmo);
			p.getInventory().setChestplate(peito);
			p.getInventory().setLeggings(calca);
			p.getInventory().setBoots(bota);
		}
	}
	
	protected void removePlayer(Player p,int motive) {//0=sair, 1=morrer, 2=quit, 3=kick
		if(!participantes.contains(p.getName()))
			return;
		participantes.remove(p.getName());
		if(guerreiroEtapa<2)
			totalParticipantes.remove(p.getName());
		else if(guerreiroEtapa==3) {
			if(participantes.size()>1) {
				getServer().broadcastMessage("§3§l[Guerreiro] §eRestam "+participantes.size()+" jogadores dentro do guerreiro!");
				checkGuerreiroEnd();
			}
			if((participantes.size()==20)&&(!jaTeleportado)&&(comecouComMaisDeVinte)) {
				getServer().broadcastMessage(" ");
				getServer().broadcastMessage("§3§l[Guerreiro] §eApenas 20 jogadores participando!");
				getServer().broadcastMessage("§3§l[Guerreiro] §eOs jogadores restantes irão ser teleportados para uma arena menor!");
				getServer().broadcastMessage(" ");
				teleportarNovaArena();
			}
			checkGuerreiroEnd();
		}
		p.teleport(saida);
		if(guerreiroEtapa==1) {
			totalParticipantes.remove(p.getName());
			if(motive!=3)
				p.sendMessage("§3§l[Guerreiro] §eVocê saiu do evento guerreiro, para voltar: §c/guerreiro");
			else
				p.sendMessage("§3§l[Guerreiro] §cVocê foi kickado do evento guerreiro");
		}
		else {
			if(motive==0)
				p.sendMessage("§3§l[Guerreiro] §eVocê saiu do evento guerreiro");
			else if(motive==1)
				p.sendMessage("§3§l[Guerreiro] §eVocê morreu no evento guerreiro");
			else if(motive==3)
				p.sendMessage("§3§l[Guerreiro] §cVocê foi kickado do evento guerreiro");
		}
	}
	
	public void liberarPvPNovaArena() {
		tliberarPvPNovaArena = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		int timer = 10;
    		public void run() {
    			if(timer==0) {
    				pvpOffNovaArena=false;
    				for(String n : participantes)
    		  			getServer().getPlayer(n).playSound(getServer().getPlayer(n).getLocation(), Sound.ANVIL_LAND, 1.0F, (byte) 30);
    				sendMessageGuerreiro(" ");
    				sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
    				sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
    				sendMessageGuerreiro("§3§l[Guerreiro] §eVALENDO!");
    				sendMessageGuerreiro(" ");
    				cliberarPvPNovaArena();
    			}
    			timer--;
    		}
		}, 0, 20);
	}
	
	public void teleportarNovaArena() {
		pvpOffNovaArena=true;
		jaTeleportado=true;
		tteleportarNovaArena = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
    		int timer = 20;
    		public void run() {
    			if(timer==0) {
    				for(String n : participantes) {
    					getServer().getPlayer(n).sendMessage("§3§l[Guerreiro] §b§lVocê foi teleportado para uma arena menor!");
    					getServer().getPlayer(n).sendMessage("§3§l[Guerreiro] §b§lO PvP será ativado em 10 segundos.");
    		  			getServer().getPlayer(n).playSound(getServer().getPlayer(n).getLocation(), Sound.ANVIL_LAND, 1.0F, (byte) 30);
    					getServer().getPlayer(n).teleport(arenaMenor);
    				}
    				liberarPvPNovaArena();
    				cteleportarNovaArena();
    			}
    			if(timer==20) {
    				sendMessageGuerreiro("§3§l[Guerreiro] §b§lVocê tem 20 segundos para recolher os itens do evento Guerreiro!");
    				for(String n : participantes)
    		  			getServer().getPlayer(n).playSound(getServer().getPlayer(n).getLocation(), Sound.EXPLODE, 1.0F, (byte) 30);
    			}else if((timer==15)||(timer==10)) {
    				sendMessageGuerreiro("§3§l[Guerreiro] §b§lVocê tem "+timer+" segundos para recolher os itens do evento Guerreiro!");
    			}else if((timer<=5)&&(timer>1))
    				sendMessageGuerreiro("§3§l[Guerreiro] §b§lVocê tem "+timer+" segundos para recolher os itens do evento Guerreiro!");
    			else if(timer==1)
    				sendMessageGuerreiro("§3§l[Guerreiro] §b§lVocê tem "+timer+" segundo para recolher os itens do evento Guerreiro!");
    			timer--;
    		}
		}, 0, 20);
	}

	public void cteleportarNovaArena() {
		getServer().getScheduler().cancelTask(tteleportarNovaArena);
	}

	public void cliberarPvPNovaArena() {
		getServer().getScheduler().cancelTask(tliberarPvPNovaArena);
	}
	
	protected void sendMessageGuerreiro(String msg) {
		for(String n : participantes)
			getServer().getPlayer(n).sendMessage(msg);
	}
	
	/**protected String formatTag(Player p) {	
		String final_tag = "";
		ClanPlayer cp = core1.getClanManager().getClanPlayer(p);
		String ctag = cp.getTagLabel();
		String ntag = cp.getTag();
		String lastcor = "";
		int parte = 0;
		for(int i=0;i<ctag.length();i++) {
			char c = cp.getTagLabel().charAt(i);
			if(Character.compare(Character.toLowerCase(c),Character.toLowerCase(ntag.charAt(parte)))==0&&!lastchar(ctag,i)) {
				if(lastcor.equals(ChatColor.getLastColors(ctag.substring(0,i))))
					final_tag+=c;
				else {
					final_tag+=ChatColor.getLastColors(ctag.substring(0,i))+c;
					lastcor=ChatColor.getLastColors(ctag.substring(0,i));
				}
				parte++;
				if(ntag.length()-1<parte)
					break;
			}
		}
		String pronto = getConfig().getString("ClanTag");
		int max = 16-(pronto.replace("%tag%", "").length());
		if(final_tag.length()>max)
			final_tag = final_tag.substring(0,(max-1));
		if(Character.compare(final_tag.charAt(final_tag.length()-1),'§')==0)
			final_tag = final_tag.substring(0,final_tag.length()-1);
		return pronto.replace("%tag%", final_tag).replaceAll("&", "§");
	}
	
	private boolean lastchar(String str,int pos) {
		if(pos==0)
			return false;
		if(Character.compare(str.charAt(pos-1),'§')==0)
			return true;
		return false;
	}*/
	
}
