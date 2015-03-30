package tobleminer.minefight.util;

import tobleminer.minefight.util.material.BlockUtil;
import tobleminer.minefight.util.protection.ProtectionUtil;

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
