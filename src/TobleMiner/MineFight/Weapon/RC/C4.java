package TobleMiner.MineFight.Weapon.RC;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.material.MaterialData;

import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;
import TobleMiner.MineFight.Util.SyncDerp.EntitySyncCalls;

public class C4
{
	public final Block block;
	private final Item item;
	public final float exploStr;
	public final PVPPlayer owner;
	private final Match match;
	private final float killRangeMod;
	private Material blockIdStore;
	private MaterialData blockDataStore;
	private final boolean damageEnviron;
	private boolean exploded = false;
	
	public C4(Block b,Item i,float f,PVPPlayer owner,Match match,float killRangeMod)
	{
		this.block = b;
		this.exploStr = f;
		this.owner = owner;
		if(this.block != null)
		{
			this.blockIdStore = this.block.getType();
			this.blockDataStore = this.block.getState().getData();
			this.block.setType(Material.LAPIS_ORE);
		}
		this.match = match;
		this.killRangeMod = killRangeMod;
		this.item = i;
		this.damageEnviron = match.canEnvironmentBeDamaged();
	}
	
	public void explode()
	{
		if(exploded) return;
		exploded = true;
		if(this.block != null)
		{
			if(this.block.getType().equals(Material.LAPIS_ORE))
			{
				match.createExplosion(owner, this.block.getLocation(), exploStr, exploStr*killRangeMod,"C4");
				if(damageEnviron)
				{
					this.block.setType(Material.AIR);
				}
				else
				{
					this.block.setType(blockIdStore);
					this.block.getState().setData(blockDataStore);
				}
			}
		}
		if(this.item != null)
		{
			Location loc = this.item.getLocation().clone();
			EntitySyncCalls.removeEntity(item);
			match.createExplosion(owner, loc, exploStr, exploStr*killRangeMod,"C4");
		}
	}	
}
