package TobleMiner.MineFight.GameEngine.Match.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import TobleMiner.MineFight.Main;
import TobleMiner.MineFight.ErrorHandling.Error;
import TobleMiner.MineFight.ErrorHandling.ErrorReporter;
import TobleMiner.MineFight.ErrorHandling.ErrorSeverity;
import TobleMiner.MineFight.GameEngine.GameEngine;
import TobleMiner.MineFight.GameEngine.Match.Match;
import TobleMiner.MineFight.GameEngine.Match.Statistics.Beans.PlayerStatBean;
import TobleMiner.MineFight.GameEngine.Player.PVPPlayer;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;

public class StatHandler 
{
	public final EbeanServer db;
	
	public boolean lStats = false;
	public boolean gStats = false;
	private boolean lInstaUp = false;
	private boolean gInstaUp = false;
	
	public StatHandler(EbeanServer db) 
	{
		this.db = db;
	}

	public void reload(GameEngine ge) 
	{
		this.lStats = ge.configuration.lStatisticsEnabled();
		this.gStats = ge.configuration.gStatisticsEnabled();
		this.lInstaUp = ge.configuration.lStatInstantUpdateEnabled();
		this.gInstaUp = ge.configuration.gStatInstantUpdateEnabled();
		if(this.lStats)
		{
			try
			{
				db.find(PlayerStatBean.class).findRowCount();
			}
			catch(Exception ex)
			{
				Main.logger.log(Level.INFO, ge.dict.get("createDb"));
				Main.main.installDDL();
			}
		}
	}
	
	public void updateMatch(Match m)
	{
		if((!lInstaUp) || (!gInstaUp))
		{
			for(PVPPlayer p : new ArrayList<PVPPlayer>(m.getPlayers()))
			{
				this.updatePlayerInternal(p,StatType.POINTS, StatUpdateType.ADD, new Long(Math.round(p.getPoints())));
				this.updatePlayerInternal(p,StatType.KILLS, StatUpdateType.ADD, new Long(Math.round(p.kills)));
				this.updatePlayerInternal(p,StatType.DEATHS, StatUpdateType.ADD, new Long(Math.round(p.deaths)));
			}
		}
	}
	
	public void updatePlayer(PVPPlayer p, StatType st, StatUpdateType sut, Object val)
	{
		if(lInstaUp || gInstaUp)
		{
			this.updatePlayerInternal(p, st, sut, val);
		}
	}
	
	public PlayerStatBean getBean(String name)
	{
		Query<PlayerStatBean> query = db.find(PlayerStatBean.class);
		query.where().eq("name", name);
		List<PlayerStatBean> beans = query.findList();
		PlayerStatBean bean = null;
		if(beans != null && beans.size() > 0)
		{
			if(beans.size() > 1)
			{
				Error err = new Error("Inconsistent database!", String.format("Multiple entries for player \"%s\" found.", name), "Nuke your database!", this.getClass().getName(), ErrorSeverity.ETERNALCHAOS);
				ErrorReporter.reportError(err);
			}
			bean = beans.get(0);
		}
		return bean;
	}
	
	private void updatePlayerInternal(PVPPlayer p, StatType st, StatUpdateType sut, Object val)
	{
		if(lStats)
		{
			PlayerStatBean bean = this.getBean(p.thePlayer.getName());
			if(bean == null)
			{
				bean = this.db.createEntityBean(PlayerStatBean.class);
				bean.setName(p.thePlayer.getName());
				bean.setupDefaults();
			}
			switch(st)
			{
				case POINTS: 
					switch(sut)
					{
						case ADD: bean.setPoints(bean.getPoints() + (Double)val); break;
						case SUB: bean.setPoints(bean.getPoints() - (Double)val); break;
						case SET: bean.setPoints((Double)val); break;
					}break;
				case KILLS:
					switch(sut)
					{
						case ADD: bean.setKills(bean.getKills() + (Long)val); break;
						case SUB: bean.setKills(bean.getKills() - (Long)val); break;
						case SET: bean.setKills((Long)val); break;
					}break;
				case DEATHS: 
					switch(sut)
					{
						case ADD: bean.setDeaths(bean.getDeaths() + (Long)val); break;
						case SUB: bean.setDeaths(bean.getDeaths() - (Long)val); break;
						case SET: bean.setDeaths((Long)val); break;
					}break;
				case FLAGCAP: 
					switch(sut)
					{
						case ADD: bean.setFlagCaps(bean.getFlagCaps() + (Long)val); break;
						case SUB: bean.setFlagCaps(bean.getFlagCaps() - (Long)val); break;
						case SET: bean.setFlagCaps((Long)val); break;
					}break;
				case FLAGDEF: 
					switch(sut)
					{
						case ADD: bean.setFlagDefs(bean.getFlagDefs() + (Long)val); break;
						case SUB: bean.setFlagDefs(bean.getFlagDefs() - (Long)val); break;
						case SET: bean.setFlagDefs((Long)val); break;
					}break;					
				case RSARM: 
					switch(sut)
					{
						case ADD: bean.setRsArms(bean.getRsArms() + (Long)val); break;
						case SUB: bean.setRsArms(bean.getRsArms() - (Long)val); break;
						case SET: bean.setRsArms((Long)val); break;
					}break;					
				case RSDISARM: 
					switch(sut)
					{
						case ADD: bean.setRsDisarms(bean.getRsDisarms() + (Long)val); break;
						case SUB: bean.setRsDisarms(bean.getRsDisarms() - (Long)val); break;
						case SET: bean.setRsDisarms((Long)val); break;
					}break;					
				case RSDESTROY: 
					switch(sut)
					{
						case ADD: bean.setRsDestruct(bean.getRsDestruct() + (Long)val); break;
						case SUB: bean.setRsDestruct(bean.getRsDestruct() - (Long)val); break;
						case SET: bean.setRsDestruct((Long)val); break;
					}break;
				case TIMEPLAYED:
					break;
			default:
				break;					
			}
			this.db.save(bean);
		}
		if(gStats)
		{
			
		}
	}
}
