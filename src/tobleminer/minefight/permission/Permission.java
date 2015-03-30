package tobleminer.minefight.permission;

public enum Permission
{
	MPVP_MATCH_JOIN("MineFight.user.joinMatch"),
	MPVP_MATCH_LEAVE("MineFight.user.leaveMatch"),
	MPVP_MATCH_CHANGETEAM("MineFight.user.changeTeam"),
	MPVP_MATCH_LIST("MineFight.user.listMatches"),
	MPVP_MATCH_INFO("MineFight.user.showMatchInfo"),
	MPVP_MATCH_START("MineFight.admin.newMatch"),
	MPVP_MATCH_END("MineFight.admin.endMatch"),
	MPVP_FLAG_ADD("MineFight.admin.addFlag"),
	MPVP_FLAG_DEL("MineFight.admin.removeFlag"),
	MPVP_RS_ADD("MineFight.admin.addRadioStation"),
	MPVP_RS_DEL("MineFight.admin.removeRadioStation"),
	MPVP_INFOSIGN_ADD("MineFight.admin.addInfosign"),
	MPVP_INFOSIGN_DEL("MineFight.admin.removeInfosign"),
	MPVP_INFOSIGN_LIST("MineFight.admin.listInfosigns"),
	MPVP_RELOAD("MineFight.admin.reloadConfig"),
	MPVP_STATS("MineFight.player.stats"),
	MPVP_DEBUG("MineFight.debug"),
	MPVP_HELP("MineFight.help"),
	MPVP_NONE("NONE");
	
	
	private final String perm;
	
	private Permission(String perm)
	{
		this.perm = perm;
	}
	
	@Override
	public String toString()
	{
		return this.perm;
	}
}
