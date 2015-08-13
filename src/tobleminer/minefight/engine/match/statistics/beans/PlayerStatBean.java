package tobleminer.minefight.engine.match.statistics.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "stats")
public class PlayerStatBean
{
	@Id
	private String	name;
	@Column
	private Double	points;
	@Column
	private Long	kills;
	@Column
	private Long	deaths;
	@Column
	private Long	flagCaps;
	@Column
	private Long	flagDefs;
	@Column
	private Long	rsArms;
	@Column
	private Long	rsDisarms;
	@Column
	private Long	rsDestruct;

	public Double getPoints()
	{
		return this.points;
	}

	public void setPoints(Double points)
	{
		this.points = points;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Long getKills()
	{
		return this.kills;
	}

	public void setKills(Long kills)
	{
		this.kills = kills;
	}

	public Long getDeaths()
	{
		return this.deaths;
	}

	public void setDeaths(Long deaths)
	{
		this.deaths = deaths;
	}

	public Long getFlagCaps()
	{
		return this.flagCaps;
	}

	public void setFlagCaps(Long flagCaps)
	{
		this.flagCaps = flagCaps;
	}

	public Long getFlagDefs()
	{
		return this.flagDefs;
	}

	public void setFlagDefs(Long flagDefs)
	{
		this.flagDefs = flagDefs;
	}

	public Long getRsArms()
	{
		return this.rsArms;
	}

	public void setRsArms(Long rsArms)
	{
		this.rsArms = rsArms;
	}

	public Long getRsDisarms()
	{
		return this.rsDisarms;
	}

	public void setRsDisarms(Long rsDisarms)
	{
		this.rsDisarms = rsDisarms;
	}

	public Long getRsDestruct()
	{
		return this.rsDestruct;
	}

	public void setRsDestruct(Long rsDestruct)
	{
		this.rsDestruct = rsDestruct;
	}

	public void setupDefaults()
	{
		this.setPoints(new Double(0d));
		this.setKills(new Long(0L));
		this.setDeaths(new Long(0L));
		this.setFlagCaps(new Long(0L));
		this.setFlagDefs(new Long(0L));
		this.setRsArms(new Long(0L));
		this.setRsDisarms(new Long(0L));
		this.setRsDestruct(new Long(0L));
	}
}