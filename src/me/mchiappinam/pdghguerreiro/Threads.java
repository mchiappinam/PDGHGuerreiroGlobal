package me.mchiappinam.pdghguerreiro;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Threads extends Thread {
	private Main plugin;
	private String tipo;
	
	private String p_string;
	private int numero;
	private Player player;
	public Threads(Main pl,String tipo2,String player) {
		plugin=pl;
		tipo=tipo2;
		p_string=player;
	}
	public Threads(Main pl,String tipo2,String player,String pkilled) {
		plugin=pl;
		tipo=tipo2;
		p_string=player;
	}
	public Threads(Main pl,String tipo2,Player player2) {
		plugin=pl;
		tipo=tipo2;
		player=player2;
	}
	public Threads(Main pl,String tipo2,List<String> l2,Player p2) {
		plugin=pl;
		tipo=tipo2;
		player=p2;
	}
	public Threads(Main pl,String tipo2,Player p,String p2,int num) {
		plugin=pl;
		tipo=tipo2;
		p_string=p2.trim();
		numero=num;
		player=p;
	}
	public Threads(Main pl,String tipo2,Player player2,String p3,boolean bool2) {
		plugin=pl;
		tipo=tipo2;
		player=player2;
		p_string=p3.trim();
	}
	public Threads(Main pl, String tipo2) {
		plugin=pl;
		tipo=tipo2;
	}
	
	public void run() {
		switch(tipo) {
			case "join": {
			    if(plugin.getguerreiroEtapa()>1) {
					plugin.addPlayer(plugin.getServer().getPlayer(p_string));
			    	return;
			    }
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					PreparedStatement pst3 = con.prepareStatement("SELECT `kills` FROM `rankpvp` WHERE `nome`='"+p_string.trim()+"';");
					ResultSet rs2 = pst3.executeQuery();
					if(!rs2.isBeforeFirst()) {
						PreparedStatement pst5 = con.prepareStatement("INSERT INTO `guerreiro` (`nome`,`kills`,`deaths`,`kd`) VALUES ('"+p_string.trim()+"',0,0,0);");
						pst5.execute();
						pst5.close();
					}
					rs2.close();
					pst3.close();
					con.close();
				}
				catch(Exception e3) {}
				break;
			}
			case "addkill": {
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					PreparedStatement pst = con.prepareStatement("SELECT `kills`,`deaths` FROM `rankpvp` WHERE `nome`='"+p_string.trim()+"';");
					ResultSet rs = pst.executeQuery();
					if(rs.next()) {
						double kd = 0;
						if(rs.getInt("deaths")!=0)
							kd = Double.parseDouble(String.format("%.2f", (1.0*rs.getInt("kills")+1)/rs.getInt("deaths")).replace(",","."));
						else 
							kd = Double.parseDouble(String.format("%.2f", (1.0*rs.getInt("kills")+1)).replace(",","."));
						PreparedStatement pst2 = con.prepareStatement("UPDATE `rankpvp` SET `kills`="+(rs.getInt("kills")+1)+",`kd`="+kd+"  WHERE `nome`='"+p_string.trim()+"';");
						pst2.executeUpdate();
						pst2.close();
					}
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
			case "setkills": {
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					PreparedStatement pst = con.prepareStatement("SELECT `deaths`,`nome` FROM `rankpvp` WHERE `nome`='"+p_string.trim()+"';");
					ResultSet rs = pst.executeQuery();
					if(rs.next()) {
						if(player!=null)
							plugin.getServer().broadcastMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.WHITE+"O admin "+ChatColor.GOLD+player.getName()+ChatColor.WHITE+" mudou o quanto "+ChatColor.GRAY+rs.getString("nome")+ChatColor.WHITE+" matou para "+numero+".");
						else
							plugin.getServer().broadcastMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.WHITE+"O "+ChatColor.GOLD+"Console"+ChatColor.WHITE+" mudou o quanto "+ChatColor.GRAY+p_string.trim()+ChatColor.WHITE+" matou para "+numero+".");
						double kd = 0;
						if(rs.getInt("deaths")!=0)
							kd = Double.parseDouble(String.format("%.2f", (1.0*numero)/rs.getInt("deaths")).replace(",","."));
						else 
							kd = Double.parseDouble(String.format("%.2f", 1.0*numero).replace(",","."));
						PreparedStatement pst2 = con.prepareStatement("UPDATE `rankpvp` SET `kills`="+numero+",`kd`="+kd+"  WHERE `nome`='"+p_string.trim()+"';");
						pst2.executeUpdate();
						pst2.close();
					}
					else {
						if(player!=null)
							player.sendMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.RED+"Jogador não encontrado!");
						else
							plugin.getLogger().info("[ERRO] Jogador nao encontrado!");
					}
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
			case "adddeath": {
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					PreparedStatement pst = con.prepareStatement("SELECT `deaths`,`kills` FROM `rankpvp` WHERE `nome`='"+p_string.trim()+"';");
					ResultSet rs = pst.executeQuery();
					if(rs.next()) {
						double kd = Double.parseDouble(String.format("%.2f", (1.0*rs.getInt("kills"))/(rs.getInt("deaths")+1)).replace(",","."));
						PreparedStatement pst2 = con.prepareStatement("UPDATE `rankpvp` SET `deaths`="+(rs.getInt("deaths")+1)+",`kd`="+kd+" WHERE `nome`='"+p_string.trim()+"';");
						pst2.executeUpdate();
						pst2.close();
					}
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
			case "setdeaths": {
				try {
					Connection con = DriverManager.getConnection(plugin.mysql_url,plugin.mysql_user,plugin.mysql_pass);
					PreparedStatement pst = con.prepareStatement("SELECT `kills`,`nome` FROM `rankpvp` WHERE `nome`='"+p_string.trim()+"';");
					ResultSet rs = pst.executeQuery();
					if(rs.next()) {
						if(player!=null)
							plugin.getServer().broadcastMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.WHITE+"O admin "+ChatColor.GOLD+player.getName()+ChatColor.WHITE+" mudou o quanto "+ChatColor.GRAY+rs.getString("nome")+ChatColor.WHITE+" morreu para "+numero+".");
						else
							plugin.getServer().broadcastMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.WHITE+"O "+ChatColor.GOLD+"Console"+ChatColor.WHITE+" mudou o quanto "+ChatColor.GRAY+p_string.trim()+ChatColor.WHITE+" morreu para "+numero+".");
						double kd = 0;
						if(numero!=0)
							kd = Double.parseDouble(String.format("%.2f", (1.0*rs.getInt("kills"))/numero).replace(",","."));
						else 
							kd = Double.parseDouble(String.format("%.2f", (1.0*rs.getInt("kills"))).replace(",","."));
						PreparedStatement pst2 = con.prepareStatement("UPDATE `rankpvp` SET `deaths`="+numero+",`kd`="+kd+" WHERE `nome`='"+p_string.trim()+"';");
						pst2.executeUpdate();
						pst2.close();
					}
					else {
						if(player!=null)
							player.sendMessage(ChatColor.AQUA+"[RankPVP] "+ChatColor.RED+"Jogador não encontrado!");
						else
							plugin.getLogger().info("Jogador nao encontrado!");
					}
					rs.close();
					pst.close();
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
