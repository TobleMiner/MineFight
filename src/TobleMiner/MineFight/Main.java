package TobleMiner.MineFight;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import TobleMiner.MineFight.Command.CommandHandler;
import TobleMiner.MineFight.Configuration.Container.FlagContainer;
import TobleMiner.MineFight.Configuration.Container.RadioStationContainer;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.ErrorHandling.Logger;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Gamemode;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Conquest.Flag;
import TobleMiner.MineFight.GameEngine.Match.Gamemode.Rush.RadioStation;
import TobleMiner.MineFight.LegalFu.LicenseHandler;
import TobleMiner.MineFight.PacketModification.ProtocolLibSafeLoader;
import TobleMiner.MineFight.Permissions.Permission;
import TobleMiner.MineFight.Permissions.PermissionManager;
import TobleMiner.MineFight.Util.Util;

public class Main extends JavaPlugin
{
	private final EventListener eventListener = new EventListener(this);
	public static Main main;
	public static PermissionManager pm;
	public static ProtocolLibSafeLoader plsl;
	public static Logger logger;
	public static Util util;
	
	public Main()
	{
		Main.main = this;
	}
	
	public static GameEngine gameEngine;
	
	@Override
	public void onEnable()
	{
		init();
		logger.log(Level.INFO,gameEngine.dict.get("onEnable"));
	}
	
	public void init()
	{
		Main.logger = new Logger(this);
		Main.util = new Util();
		Main.gameEngine = new GameEngine(this);
		logger.log(Level.INFO,gameEngine.dict.get("preEnable"));
		Bukkit.getPluginManager().registerEvents(eventListener, this);
		if(!(new LicenseHandler().init(this)))
		{
			Error err = new Error("License check failed!","The plugins license could not be copied into the plugin's folder!", "The plugin won't start until the license is copied.", this.getClass().getName(), ErrorSeverity.DOUBLERAINBOOM);
			ErrorReporter.reportError(err);
			return;
		}
		Main.pm = new PermissionManager();
		Main.plsl = new ProtocolLibSafeLoader(this);
		Timer timer = new Timer();
		GlobalTimer gtimer = new GlobalTimer();
		timer.schedule(gtimer,10,10);
	}
	
	@Override
	public void onDisable()
	{
		logger.log(Level.INFO,gameEngine.dict.get("onDisable"));
		Main.gameEngine.endAllMatches();
	}
		
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String args[])
	{
		return CommandHandler.handleCommand(args, sender);
	}	
	
	public File getPluginDir()
	{
		return this.getDataFolder();
	}
}
