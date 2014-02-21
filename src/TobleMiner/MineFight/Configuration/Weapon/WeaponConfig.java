package TobleMiner.MineFight.Configuration.Weapon;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer.HitZone;
import TobleMiner.MineFight.Util.IO.File.FileUtil;

public class WeaponConfig 
{
	private final FileConfiguration conf;
	private final File confFile;
	
	public WeaponConfig(File f)
	{
		this.conf = new YamlConfiguration();
		this.confFile = f;
		this.load();
	}
	
	public static WeaponIndex getConfigs(File pluginDir)
	{
		WeaponIndex wi = new WeaponIndex();
		File folder = new File(pluginDir,"weapons");
		if(!folder.exists())
		{
			folder.mkdirs();
			String[] files = {"medigun.wpconf","sniper.wpconf","flamethrower.wpconf"};
			for(String file : files)
			{
				File f = new File(folder,file);
				String lresource = "Defaults/"+file;
				try 
				{
					FileUtil.copyFromInputStreamToFileUtf8(f, WeaponConfig.class.getResourceAsStream(lresource));
				}
				catch (IOException e) 
				{
					Error error = new Error("Error copying weapon configuration!",String.format("The weapon descriptor '%s' culd not be copied to '%s'. Make sure the server has write permissions for that folder.",lresource,f.getAbsolutePath()),"The specified weapon won't work until this error is fixed!", WeaponConfig.class.getName(),ErrorSeverity.SEVERE);
					ErrorReporter.reportError(error);
				}
			}
		}
		for(File f : folder.listFiles())
		{
			if(f.getName().endsWith(".wpconf"))
			{
				wi.add(new WeaponConfig(f).getDescriptor());
			}
		}
		return wi;
	}
	
	private WeaponDescriptor getDescriptor() 
	{
		String name = this.conf.getString("name");
		double cadence = this.conf.getDouble("cadence",0d);
		double speed = this.conf.getDouble("speed",0d);
		String useType = this.conf.getString("usetype");
		String dmgType = this.conf.getString("dmgtype");
		String matname = this.conf.getString("item","Twilight Sparkle");
		short itemdmg = (short)this.conf.getInt("itemdmg",0);
		boolean doTranslate = this.conf.getBoolean("translate",false);
		Material mat = Material.getMaterial(matname);
		if(mat == null)
		{
			Error error = new Error("Error in weapon configuration!",String.format("The weapon descriptor '%s' contains an error. The item material doesn't exist.",this.confFile.getPath()),"The specified weapon won't be displayed correctly until this error is fixed!",this.getClass().getName(),ErrorSeverity.ERROR);
			ErrorReporter.reportError(error);
			mat = Material.IRON_FENCE;
		}
		ConfigurationSection sec = this.conf.getConfigurationSection("damage");
		List<Entry<Double, Double>> entries = new ArrayList<Entry<Double, Double>>();
		if(sec != null)
		{
			for(String key : sec.getKeys(false))
			{
				try
				{
					double dist = Double.parseDouble(key);
					double dmg = sec.getDouble(key,0d);
					entries.add(new AbstractMap.SimpleEntry<Double, Double>(new Double(dist), new Double(dmg/100d)));
				}
				catch(Exception ex)
				{
					Error error = new Error("Error in weapon configuration!",String.format("The weapon descriptor '%s' contains an error. The shot-length-damage map contains a non numeric key.",this.confFile.getPath()),"The specified weapon won't be displayed correctly until this error is fixed!",this.getClass().getName(),ErrorSeverity.ERROR);
					ErrorReporter.reportError(error);
				}
			}
		}
		if(entries.size() == 0)
		{
			Error error = new Error("Abnormality in weapon configuration!",String.format("The weapon descriptor '%s' contains an abnormal situation. The shot-length-damage map contains 0 entries. Adding fake zero damage entry.",this.confFile.getPath()),"This might be an unwanted situation.",this.getClass().getName(),ErrorSeverity.ERROR);
			ErrorReporter.reportError(error);
		}
		List<Entry<HitZone, Double>> multipliers = new ArrayList<Entry<HitZone, Double>>();
		sec = this.conf.getConfigurationSection("hitzones");
		if(sec != null)
		{
			for(HitZone zone : HitZone.values())
			{
				if(sec.contains(zone.name))
				{
					multipliers.add(new AbstractMap.SimpleEntry<HitZone, Double>(zone, sec.getDouble(zone.name)));
				}
				else
				{
					multipliers.add(new AbstractMap.SimpleEntry<HitZone, Double>(zone, 1d));
				}
			}
		}
		return new WeaponDescriptor(name, doTranslate, cadence, speed, useType, dmgType, mat, itemdmg, entries, multipliers);
	}

	public void load()
	{
		try
		{
			this.conf.load(this.confFile);
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed loading weapon configuration!",String.format("The weapon descriptor '%s' could not be loaded. The File contains errors.",this.confFile.getPath()),"The specified weapon won't be available until this error is fixed!",this.getClass().getName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
	}
	
	public void save()
	{
		try
		{
			this.conf.save(this.confFile);
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed saving weapon configuration!",String.format("The weapon descriptor '%s' could not be saved. Make sure you have got write permissions!",this.confFile.getPath()),"The changes made to the specified weapon will be lost!",this.getClass().getName(),ErrorSeverity.SEVERE);
			ErrorReporter.reportError(error);
		}
	}
}
