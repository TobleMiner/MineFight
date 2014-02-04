package TobleMiner.MineFight;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.Util.Util;

public class EventListener implements Listener
{
	final Main mane;
	
	public EventListener(Main p)
	{
		this.mane = p;
	}
		
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		try
		{
			Player p = event.getPlayer();
			ItemStack is = p.getInventory().getItemInHand();
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			{
				Material material = event.getClickedBlock().getType();
				if(material.equals(Material.SIGN) || material.equals(Material.SIGN_POST) || material.equals(Material.WALL_SIGN))
				{
					Main.gameEngine.rightClickSign(p,event.getClickedBlock());
				}
				if(is != null && is.getType().equals(Material.INK_SACK) && is.getDurability() == (short)4)
				{
					Main.gameEngine.rightClickBlockWithLapis(p,event.getClickedBlock(),p.getInventory());
				}
			}
			if(is != null)
			{
				if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				{
					if(is.getType().equals(Material.DIAMOND))
					{
						Main.gameEngine.rightClickWithDiamond(p);					
					}
					else if(is.getType().equals(Material.BONE))
					{
						Main.gameEngine.rightClickWithBone(p);										
					}
				}
				if(is.getType().equals(Material.WOOD_SWORD))
				{
					if(event.getAction().equals(Action.RIGHT_CLICK_AIR))
					{
						Main.gameEngine.ClickWithWoodenSword(p,true);
					}
					else if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
					{
						Main.gameEngine.ClickWithWoodenSword(p,false);
					}
				}

			}
		}
		catch(Exception e)
		{
			//:D Sunshine Sunshine Ladybugs awake...
			e.printStackTrace();
		}
		
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if(Main.gameEngine.configuration.isMpvpEnabled(event.getPlayer().getWorld()))
		{
			Location l = event.getItemDrop().getLocation();
			boolean isItemAllowed = false;
			if(event.getItemDrop().getItemStack().getType().equals(Material.IRON_INGOT))
			{
				isItemAllowed = Main.gameEngine.configuration.getHandGrenadeAllowedInsideProtection();
			}
			else if(event.getItemDrop().getItemStack().getType().equals(Material.REDSTONE))
			{
				isItemAllowed = Main.gameEngine.configuration.getIMSAllowedInsideProtection();
			}
			else if(event.getItemDrop().getItemStack().getType().equals(Material.INK_SACK))
			{
				isItemAllowed = Main.gameEngine.configuration.getC4allowedInsideProtection();
			}
			else if(event.getItemDrop().getItemStack().getType().equals(Material.CLAY_BALL))
			{
				isItemAllowed = Main.gameEngine.configuration.getM18allowedInsideProtection();
			}
			if(Util.protect.isLocProtected(l) && (!isItemAllowed))
			{
				event.setCancelled(true);
			}
			else
			{
				event.setCancelled(Main.gameEngine.playerDroppedItem(event.getItemDrop(),event.getPlayer()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Material mat = event.getItem().getItemStack().getType();
		if(mat.equals(Material.REDSTONE) || mat.equals(Material.CLAY_BALL) || mat.equals(Material.IRON_INGOT) || mat.equals(Material.INK_SACK))
		{
			event.setCancelled(Main.gameEngine.playerPickUpItem(event.getItem(),event.getPlayer()));
		}
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event)
	{
		Material mat = event.getEntity().getItemStack().getType();
		if(mat.equals(Material.REDSTONE) || mat.equals(Material.CLAY_BALL))
		{
			event.setCancelled(Main.gameEngine.itemDespawn(event.getEntity()));
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if(event.getEntity() instanceof Item)
		{
			Material mat = ((Item)event.getEntity()).getItemStack().getType();
			if(mat.equals(Material.REDSTONE) || mat.equals(Material.CLAY_BALL))
			{
				event.setCancelled(Main.gameEngine.itemDamage((Item)event.getEntity()));
			}
		}
		else if(event.getEntity() instanceof Player)
		{
			event.setCancelled(Main.gameEngine.playerDamage((Player)event.getEntity(),event.getCause()));
		}
	}
	
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event)
	{
		if(event.getEntity() instanceof Item)
		{
			Material mat = ((Item)event.getEntity()).getItemStack().getType();
			if(mat.equals(Material.REDSTONE) || mat.equals(Material.CLAY_BALL))
			{
				event.setCancelled(Main.gameEngine.itemDamage((Item)event.getEntity()));
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setDeathMessage(Main.gameEngine.playerDeath(event.getEntity(),event.getDeathMessage(),event.getDrops()));
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Main.gameEngine.playerChangedWorld(event.getPlayer(),event.getFrom());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Main.gameEngine.playerQuit(event.getPlayer());
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event)
	{
		Projectile proj = event.getEntity();
		if(proj instanceof Arrow)
		{
			Arrow arrow = (Arrow)proj;
			LivingEntity shooter = arrow.getShooter();
			if(shooter instanceof Player)
			{
				Player p = (Player)shooter;
				event.setCancelled(Main.gameEngine.arrowLaunchedByPlayer(p,arrow));
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player p = event.getPlayer();
		Block b = event.getBlock();
		/*List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(b.getWorld());
		boolean isBlockProtected = false;
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				isBlockProtected = pa.isBlockInsideRegion(b);
			}
		}
		if(!isBlockProtected)
		{
			if(p != null && b != null)
			{
				if(b.getType().equals(Material.DISPENSER))
				{*/
					event.setCancelled(Main.gameEngine.blockPlace(p,b));
				/*}
			}
		}
		event.setCancelled(isBlockProtected);*/
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		event.setCancelled(Main.gameEngine.blockBreak(event.getPlayer(),event.getBlock()));
		/*Block b = event.getBlock();
		List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(b.getWorld());
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				event.setCancelled(pa.isBlockInsideRegion(b));
			}
		}*/
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		event.setCancelled(Main.gameEngine.blockBreak(event.getPlayer(),event.getBlock()));
		/*Block b = event.getBlock();
		List<ProtectedArea> lpa = Main.gameEngine.configuration.protectionRegions.get(b.getWorld());
		if(lpa != null)
		{
			for(ProtectedArea pa : lpa)
			{
				event.setCancelled(pa.isBlockInsideRegion(b));
			}
		}*/
	}
		
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			event.setCancelled(Main.gameEngine.blockBreak((Player)event.getEntity(),event.getBlock()));
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(event.getDamager() instanceof Arrow && event.getEntity() instanceof Player)
		{
			event.setCancelled(Main.gameEngine.arrowHitPlayer((Arrow)event.getDamager(),(Player)event.getEntity(),event.getDamage()));
			if(event.isCancelled()) event.setDamage(0d);
		}
		else if(event.getDamager() instanceof Player && event.getEntity() instanceof Player)
		{
			event.setCancelled(Main.gameEngine.playerDamagePlayer((Player)event.getDamager(),(Player)event.getEntity(),event.getDamage()));
			if(event.isCancelled()) event.setDamage(0d);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		event.setRespawnLocation(Main.gameEngine.playerRespawn(event.getPlayer(),event.getRespawnLocation()));
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		if(event.getEntity() instanceof Arrow)
		{
			Main.gameEngine.arrowHit((Arrow)event.getEntity());
		}
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent apce)
	{
		apce.setFormat(Main.gameEngine.playerChat(apce.getFormat(),apce.getPlayer()));
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if(event.getEntity() instanceof Player)
		{
			event.setCancelled(Main.gameEngine.foodLevelChange((Player)event.getEntity()));
		}
	}
}
