package TobleMiner.MineFight.Weapon.RC;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;

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
	private int blockIdStore = 0;
	private byte blockDataStore = 0;
	private final boolean damageEnviron;
	
	public C4(Block b,Item i,float f,PVPPlayer owner,Match match,float killRangeMod)
	{
		this.block = b;
		this.exploStr = f;
		this.owner = owner;
		if(this.block != null)
		{
			this.blockIdStore = this.block.getTypeId();
			this.blockDataStore = this.block.getData();
			this.block.setTypeIdAndData(Material.LAPIS_ORE.getId(),(byte)0,true);
		}
		this.match = match;
		this.killRangeMod = killRangeMod;
		this.item = i;
		this.damageEnviron = match.canEnvironmentBeDamaged();
	}
	
	public void explode()
	{
		if(this.block != null)
		{
			if(this.block.getType().equals(Material.LAPIS_ORE))
			{
				match.createExplosion(owner, this.block.getLocation(), exploStr, exploStr*killRangeMod,"C4");
				if(damageEnviron)
				{
					this.block.setTypeIdAndData(Material.AIR.getId(),(byte)0,true);
				}
				else
				{
					this.block.setTypeIdAndData(blockIdStore, blockDataStore, true);
				}
			}
		}
		if(this.item != null)
		{
			match.createExplosion(owner, this.item.getLocation(), exploStr, exploStr*killRangeMod,"C4");
			EntitySyncCalls.removeEntity(item);
		}
	}
}
