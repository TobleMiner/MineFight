package tobleminer.minefight.api;

import tobleminer.minefight.util.Util;
import tobleminer.minefight.util.protection.ProtectionUtil;

public class MineFightProtectionAPI 
{
	public static MineFightProtectionAPI instance;

	public MineFightProtectionAPI()
	{
		instance = this;
	}
	
	public ProtectionUtil getProtections()
	{
		return Util.protect;
	}
}
