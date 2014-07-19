package TobleMiner.MineFight.Language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import TobleMiner.MineFight.Debug.Debugger;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.Util.IO.File.FileUtil;

public class Langfile 
{
	private final File langDir;
	private final HashMap<String,List<String>> dictionary = new HashMap<String,List<String>>();
	
	public Langfile(File file)
	{
		this.langDir = new File(file,"lang");
		try
		{
			if(!langDir.exists())
			{
				langDir.mkdirs();
			}
			String[] files = {"DE_de.lang","EN_uk.lang","EN_us.lang"};
			for(String s : files)
			{
				File langFile = new File(this.langDir,s);
				if(!(langFile.exists() && langFile.isFile()))
				{
					if(!langFile.isFile())
					{
						langFile.delete();
					}
					InputStream is = this.getClass().getResourceAsStream(s);
					FileUtil.copyFromInputStreamToFileUtf8(langFile, is);
				}
			}
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed handling languagefiles!","An error occured while checking the languagefiles: "+ex.getMessage(),"Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
			ErrorReporter.reportError(error);
			ex.printStackTrace();
		}
	}

	public void loadLanguageFileExt(File langFile)
	{
		if(langFile.exists() && langFile.isFile())
		{
			try
			{
				FileReader fr = new FileReader(langFile);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				int cnt = 0;
				String alltrans = "";
				while(line != null)
				{
					int i = line.indexOf("#");
					if(i > -1)
					{
						if(i < 4)
						{
							line = br.readLine();
							continue;
						}
						line = line.substring(0, i-1);
					}
					int equ = line.indexOf('=');
					if(equ > 0)
					{
						String key = line.substring(0,equ);
						String s = line.substring(equ,line.length());
						List<String> alternatives = new ArrayList<String>();
						boolean openDoublequotes = false;
						int j = 0;
						int lastDoubleQuotePos=0;
						while(j<s.length())
						{
							int quotePos = s.indexOf('\"',j);
							if(quotePos < 0)
							{
								break;
							}
							j = quotePos+1;
							if(quotePos > 0)
							{
								if(s.charAt(quotePos-1) != '\\')
								{
									openDoublequotes = !openDoublequotes;
									if(!openDoublequotes)
									{
										alternatives.add(s.substring(lastDoubleQuotePos+1,quotePos).replace("\\",""));
									}
									lastDoubleQuotePos = quotePos;
								}
							}
							else
							{
								openDoublequotes = true;
							}
						}
						this.dictionary.put(key, alternatives);
						alltrans += key+"=";
						for(String alt : alternatives)
							alltrans += "\""+alt+"\",";
						alltrans = alltrans.substring(0, alltrans.length() - 1);
						alltrans += "\n";
						cnt++;
					}
					else
					{
						Error error = new Error("Langfile parse-error!","An error occured while parsing the languagefile '"+langFile.getAbsoluteFile()+"' : "+line,"The message belonging to this entry will be broken!",this.getClass().getCanonicalName(),ErrorSeverity.WARNING);
						ErrorReporter.reportError(error);
					}
					line = br.readLine();
				}
				fr.close();
				br.close();
				Debugger.writeDebugOut(String.format("Added %d translations:\n", cnt) + alltrans);
			}
			catch(Exception ex)
			{
				Error error = new Error("Failed loading languagefile!","An error occured while loading languagefile from '"+langFile.getAbsoluteFile()+"' : "+ex.getMessage(),"Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
				ex.printStackTrace();
				ErrorReporter.reportError(error);
			}
		}
		else
		{
			Error error = new Error("Failed loading languagefile!","The languagefile '"+langFile.getAbsoluteFile()+"' doesn't exist!","Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
			ErrorReporter.reportError(error);
		}
	}
	
	public void loadLanguageFile(String name)
	{
		File langFile = new File(this.langDir,name);
		this.loadLanguageFileExt(langFile);
	}
		
	public String get(String key)
	{
		if(key == null || key.length() == 0) 
			return "";
		List<String> vals = this.dictionary.get(key);
		if(vals == null || vals.size() == 0)
		{
			return String.format("{%s}", key);
		}
		Random rand = new Random();
		return vals.get(rand.nextInt(vals.size()));
	}
}
