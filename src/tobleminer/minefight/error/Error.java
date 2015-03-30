package tobleminer.minefight.error;

public class Error
{
	public final String header;
	public final String body;
	public final String footer;
	public final String location;
	public final ErrorSeverity severity;
	
	public Error(String header,String body,String footer,String location,ErrorSeverity severity)
	{
		this.header = header;
		this.body = body;
		this.footer = footer;
		this.location = location;
		this.severity = severity;
	}
}
