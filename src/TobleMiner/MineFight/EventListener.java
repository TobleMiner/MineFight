package TobleMiner.MineFight;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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
		Main.gameEngine.playerInteract(event);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Main.gameEngine.playerDroppedItem(event);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Main.gameEngine.playerPickUpItem(event);
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event)
	{
		Main.gameEngine.itemDespawn(event);
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		Main.gameEngine.entityDamage(event);
	}
	
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event)
	{
		Main.gameEngine.entityCombust(event);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		Main.gameEngine.playerDeath(event);
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
		Main.gameEngine.projectileLaunched(event);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		event.setCancelled(Main.gameEngine.blockPlace(event));
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		event.setCancelled(Main.gameEngine.blockBreak(event));
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event)
	{
		event.setCancelled(Main.gameEngine.blockDamaged(event));
	}
		
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event)
	{
		event.setCancelled(Main.gameEngine.entityChangeBlock(event));
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Main.gameEngine.entityDamageByEntity(event);
		if(event.isCancelled())
			event.setDamage(0d);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		event.setRespawnLocation(Main.gameEngine.playerRespawn(event.getPlayer(),event.getRespawnLocation()));
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		Main.gameEngine.projectileHit(event);
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent apce)
	{
		Main.gameEngine.playerChat(apce);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		Main.gameEngine.foodLevelChange(event);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		Main.gameEngine.entityExplosion(event);
	}
}
