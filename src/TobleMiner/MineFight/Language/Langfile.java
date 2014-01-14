package TobleMiner.MineFight.Language;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;

public class Langfile 
{
	private final String langDir;
	private final HashMap<String,List<String>> dictionary = new HashMap<String,List<String>>();
	
	public Langfile(File file)
	{
		String basePath = file.getAbsolutePath();
		this.langDir = basePath+File.separator+"lang";
		try
		{
			File langDir = new File(this.langDir);
			if(!langDir.isDirectory() || !langDir.exists())
			{
				langDir.mkdir();
				String[] files = {"DE_de.lang","EN_uk.lang","EN_us.lang"}; //Buck conventions, use ponies instead!
				for(String s : files)
				{
					File langFile = new File(this.langDir+File.separator+s);
					InputStream is = this.getClass().getResourceAsStream(s);
					FileWriter fw = new FileWriter(langFile);
					while(is.available() > 0)
					{
						fw.write(is.read());
					}
					is.close();
					fw.close();
				}
			}
		}
		catch(Exception ex)
		{
			Error error = new Error("Failed handling languagefiles!","An error occured while checking the languagefiles: "+ex.getMessage(),"Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
			ErrorReporter.reportError(error);
		}
	}
	
	public void loadLanguageFile(String name)
	{
		File langFile = new File(this.langDir+File.separator+name);
		if(langFile.exists() && langFile.isFile())
		{
			this.dictionary.clear();
			try
			{
				FileReader fr = new FileReader(langFile);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
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
			}
			catch(Exception ex)
			{
				Error error = new Error("Failed loading languagefile!","An error occured while loading languagefile from '"+langFile.getAbsoluteFile()+"' : "+ex.getMessage(),"Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
				ErrorReporter.reportError(error);
			}
		}
		else
		{
			Error error = new Error("Failed loading languagefile!","The languagefile '"+langFile.getAbsoluteFile()+"' doesn't exist!","Most messages will be missing until this problem is fixed!",this.getClass().getCanonicalName(),ErrorSeverity.ETERNALCHAOS);
			ErrorReporter.reportError(error);
		}
	}
	
	public String get(String key)
	{
		List<String> vals = this.dictionary.get(key);
		Random rand = new Random();
		return vals.get(rand.nextInt(vals.size()));
	}
}
