package TobleMiner.MineFight.Util;

import TobleMiner.MineFight.Util.Material.BlockUtil;
import TobleMiner.MineFight.Util.Protection.ProtectionUtil;

public class Util
{
	public static BlockUtil block;
	public static ProtectionUtil protect;
	
	public Util()
	{
		Util.block = new BlockUtil();
		Util.protect = new ProtectionUtil();
	}
}
