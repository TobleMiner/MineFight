package TobleMiner.MineFight.LegalFu;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.Util.Hashing.HashUtil;
import TobleMiner.MineFight.Util.IO.StreamUtil;
import TobleMiner.MineFight.Util.IO.File.FileUtil;

public class LicenseHandler
{
	public boolean init(Main mane)
	{
		try
		{
			File license = new File(mane.getPluginDir(),"LICENSE");
			if(license.exists())
			{
				InputStream is = this.getClass().getResourceAsStream("LICENSE");
				BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF8"));
				String licenseGood = StreamUtil.getDataAsUTF8(br);
				String licenseProbe = FileUtil.getAllTextUTF8(license);
				br.close();
				if(!HashUtil.hashMatchStringUTF8(licenseGood, licenseProbe))
				{
					Main.logger.log(Level.INFO,"LICENSE checksum missmatch. Replacing LICENSE file.");
					license.delete();
				}
				else
				{
					return true;
				}
			}
			if(!license.exists())
			{
				try
				{
					InputStream is = this.getClass().getResourceAsStream("LICENSE");
					FileUtil.copyFromInputStreamToFileUtf8(license, is);
					is.close();
					return true;
				}
				catch(Exception ex){}
				return false;
			}
			return false;
		}
		catch(Exception ex){ex.printStackTrace();}
		return false;
	}
}
