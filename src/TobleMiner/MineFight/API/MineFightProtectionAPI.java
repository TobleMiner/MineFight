package TobleMiner.MineFight.API;

import TobleMiner.MineFight.Util.Util;
import TobleMiner.MineFight.Util.Protection.ProtectionUtil;

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
