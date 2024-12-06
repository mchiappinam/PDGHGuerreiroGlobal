package me.mchiappinam.pdghguerreiro;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {
	private Main plugin;
	public Comando(Main main) {
		plugin=main;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("guerreiro")) {
			if(args.length==0) {
				if(sender==plugin.getServer().getConsoleSender()) {
					sender.sendMessage("§3§l[Guerreiro] §cConsole bloqueado de executar o comando!");
					return true;
				}
				if(plugin.getguerreiroEtapa()==0) {
					sender.sendMessage("§3§l[Guerreiro] §cO evento guerreiro não está acontecendo!");
					return true;
				}
				if(plugin.getguerreiroEtapa()>1) {
					sender.sendMessage("§3§l[Guerreiro] §cO evento guerreiro já começou!");
					return true;
				}
				if(plugin.participantes.contains(sender.getName())) {
					sender.sendMessage("§3§l[Guerreiro] §cVocê já entrou no evento guerreiro!");
					return true;
				}
				if(plugin.getConfig().contains("Bans."+sender.getName().toLowerCase())) {
					sender.sendMessage("§3§l[Guerreiro] §cVocê está banido do evento guerreiro!");
					sender.sendMessage("§3§l[Guerreiro] §cBanido por "+plugin.getConfig().getString("Bans."+sender.getName().toLowerCase()+".Por")+" em "+plugin.getConfig().getString("Bans."+sender.getName().toLowerCase()+".Data"));
					return true;
				}
                if(((Player)sender).isInsideVehicle()) {
				     sender.sendMessage("§3§l[Guerreiro] §cVocê está dentro de um veículo!");
				     return true;
				}
                if(((Player)sender).isDead()) {
				     sender.sendMessage("§3§l[Guerreiro] §cVocê está morto!");
				     return true;
				}
				plugin.addPlayer((Player)sender);
				return true;
			}else{
				if(args[0].equalsIgnoreCase("sair")) {
					if(plugin.getguerreiroEtapa()==0) {
						sender.sendMessage("§3§l[Guerreiro] §cO evento guerreiro não está aberto!");
						return true;
					}
					if(plugin.getguerreiroEtapa()!=1) {
						sender.sendMessage("§3§l[Guerreiro] §cVocê não pode sair agora!");
						return true;
					}
					plugin.removePlayer((Player)sender,0);
					return true;
				}
				//outro cmds, admin!
				if(!sender.hasPermission("pdgh.admin")) {
					sender.sendMessage("§3§l[Guerreiro] §cVocê não tem permissão para executar esse comando!");
					return true;
				}
				if(args[0].equalsIgnoreCase("forcestart")) {
					if(plugin.getguerreiroEtapa()!=0) {
						sender.sendMessage("§3§l[Guerreiro] §cJá existe um evento guerreiro sendo executado!");
						return true;
					}
					if(plugin.getguerreiroEtapa()==0&&!plugin.canStart) {
						sender.sendMessage("§3§l[Guerreiro] §cUm evento guerreiro está sendo finalizado!");
						return true;
					}
					sender.sendMessage("§3§l[Guerreiro] §oEvento guerreiro sendo iniciado!");
					plugin.prepareGuerreiro();
					return true;
				}
				if(args[0].equalsIgnoreCase("forcestop")) {
					if(plugin.getguerreiroEtapa()==0) {
						sender.sendMessage("§3§l[Guerreiro] §cNão há nenhum evento guerreiro sendo executado!");
						return true;
					}
					plugin.cancelGurerreiro();
					sender.sendMessage("§3§l[Guerreiro] §oEvento guerreiro sendo parado!");
					return true;
				}
				if(args[0].equalsIgnoreCase("kick")) {
					if(args.length<2) {
						sender.sendMessage("§3§l[Guerreiro] §c/guerreiro kick <nome>");
						return true;
					}
					String nome = args[1].toLowerCase();
					Player p = plugin.getServer().getPlayer(nome);
					if(p==null) {
						sender.sendMessage("§3§l[Guerreiro] §cJogador não encontrado!");
						return true;
					}
					plugin.removePlayer(p, 3);
					sender.sendMessage("§3§l[Guerreiro] §o"+nome+" foi kickado do evento guerreiro!");
					return true;
				}
				if(args[0].equalsIgnoreCase("ban")) {
					if(args.length<2) {
						sender.sendMessage("§3§l[Guerreiro] §c/guerreiro ban <nome>");
						return true;
					}
					String nome = args[1].toLowerCase();
					plugin.getConfig().set("Bans."+nome+".Por", sender.getName());
					plugin.getConfig().set("Bans."+nome+".Data", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
					plugin.saveConfig();
					Player p = plugin.getServer().getPlayerExact(nome);
					if(p!=null)
						plugin.removePlayer(p, 3);
					sender.sendMessage("§3§l[Guerreiro] §o"+nome+" foi banido dos eventos guerreiro!");
					return true;
				}
				if(args[0].equalsIgnoreCase("unban")) {
					if(args.length<2) {
						sender.sendMessage("§3§l[Guerreiro] §c/guerreiro ban <nome>");
						return true;
					}
					String nome = args[1].toLowerCase();
					if(!plugin.getConfig().contains("Bans."+nome)) {
						sender.sendMessage("§3§l[Guerreiro] §cNome não encontrado!");
						return true;
					}
					plugin.getConfig().set("Bans."+nome, null);
					plugin.saveConfig();
					sender.sendMessage("§3§l[Guerreiro] §o"+nome+" foi desbanido dos eventos guerreiro!");
					return true;
				}
				if(args[0].equalsIgnoreCase("setspawn")) {
					if(sender==plugin.getServer().getConsoleSender()) {
						sender.sendMessage("§3§l[Guerreiro] §cConsole bloqueado de executar o comando!");
						return true;
					}
					Player p = (Player)sender;
					plugin.spawn=p.getLocation();
					plugin.getConfig().set("Arena.Entrada", plugin.spawn.getWorld().getName()+";"+plugin.spawn.getX()+";"+plugin.spawn.getY()+";"+plugin.spawn.getZ()+";"+plugin.spawn.getYaw()+";"+plugin.spawn.getPitch());
					plugin.saveConfig();
					sender.sendMessage("§3§l[Guerreiro] §oSpawn marcado!");
					return true;
				}
				if(args[0].equalsIgnoreCase("setsaida")) {
					if(sender==plugin.getServer().getConsoleSender()) {
						sender.sendMessage("§3§l[Guerreiro] §cConsole bloqueado de executar o comando!");
						return true;
					}
					Player p = (Player)sender;
					plugin.saida=p.getLocation();
					plugin.getConfig().set("Arena.Saida", plugin.saida.getWorld().getName()+";"+plugin.saida.getX()+";"+plugin.saida.getY()+";"+plugin.saida.getZ()+";"+plugin.saida.getYaw()+";"+plugin.saida.getPitch());
					plugin.saveConfig();
					sender.sendMessage("§3§l[Guerreiro] §oSaída marcada!");
					return true;
				}
				if(args[0].equalsIgnoreCase("setarenamenor")) {
					if(sender==plugin.getServer().getConsoleSender()) {
						sender.sendMessage("§3§l[Guerreiro] §cConsole bloqueado de executar o comando!");
						return true;
					}
					Player p = (Player)sender;
					plugin.arenaMenor=p.getLocation();
					plugin.getConfig().set("Arena.Menor", plugin.arenaMenor.getWorld().getName()+";"+plugin.arenaMenor.getX()+";"+plugin.arenaMenor.getY()+";"+plugin.arenaMenor.getZ()+";"+plugin.arenaMenor.getYaw()+";"+plugin.arenaMenor.getPitch());
					plugin.saveConfig();
					sender.sendMessage("§3§l[Guerreiro] §oArena menor marcada!");
					return true;
				}
				sendHelp((Player)sender);
			}
			return true;
		}
		return true;
	}
	
	private void sendHelp(Player p) {
		p.sendMessage("§d§lPDGHGuerreiro - Comandos do plugin:");
		p.sendMessage("§2/guerreiro ? -§a- Lista de comandos");
		p.sendMessage("§c/guerreiro forcestart -§a- Força o inicio do evento guerreiro");
		p.sendMessage("§c/guerreiro forcestop -§a- Força a parada do evento guerreiro");
		p.sendMessage("§2/guerreiro kick <nome> -§a- Kicka um jogador do evento guerreiro");
		p.sendMessage("§2/guerreiro ban <nome> -§a- Bane um jogador do evento guerreiro");
		p.sendMessage("§2/guerreiro unban <nome> -§a- Desbane um jogador do evento guerreiro");
		p.sendMessage("§2/guerreiro setspawn -§a- Marca local de spawn do evento guerreiro");
		p.sendMessage("§2/guerreiro setsaida -§a- Marca local de saida do evento guerreiro");
		p.sendMessage("§2/guerreiro setarenamenor -§a- Marca local da arena menor do evento guerreiro");
		p.sendMessage("§2/guerreiro info -§a- Mostra quantos jogadores estão dentro do evento guerreiro");
	}

}
