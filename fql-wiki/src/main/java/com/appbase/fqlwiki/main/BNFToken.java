package com.appbase.fqlwiki.main;

import java.util.ArrayList;
import java.util.List;

public class BNFToken implements Comparable<BNFToken>{

	public static final String STATEMENT = "STATEMENT";
	public static final String ORIGIN = "ORIGIN";
	public static final String LHS = "LHS";
	public static final String TERMINAL = "TERMINAL";
	public static final String NON_TERMINAL = "NON_TERMINAL";
	public static final String VARIABLE = "VARIABLE";
	
	private String string = null;
	private String type  = null;
	private List<BNFToken> nextTokens = null;	
	
	private BNFToken() 
	{
	}

	public BNFToken(String string)
	{
		this(string, null);
	}
	
	public BNFToken(String string, String type)
	{
		this();
		this.string = string;
		this.nextTokens = new ArrayList<BNFToken>();
		
		if ( type != null && (type.equals(BNFToken.STATEMENT) || type.equals(BNFToken.LHS) || type.equals(BNFToken.ORIGIN)) )
		{
			this.type = type;
		}
		else
		{
			if (FQLGrammar.getInstance().getTerminals().contains(this.string))
			{
				this.type = BNFToken.TERMINAL;
			}
			else
			{
				if (FQLGrammar.getInstance().getNonTerminals().contains(this.string))
				{
					this.type = BNFToken.NON_TERMINAL;
				}
				else
				{
					if (string.equals(string.toLowerCase()))
					{
						this.type = BNFToken.VARIABLE;
					}
				}
			}
		}
	}
	
	public void addNext(BNFToken token)
	{
		this.nextTokens.add(token);
	}

	public String getString() {
		return string;
	}

	void setString(String string) {
		this.string = string;
	}

	public String getType() {
		return type;
	}

	public List<BNFToken> getNextTokens() {
		return nextTokens;
	}
	
	public int compareTo(BNFToken other) {
    	return this.getString().compareTo(other.getString());
	}
	
	public String getGraphAsHTML()
	{
		return getGraphAsHTML("");
	}

	public String getGraphAsHTML(String spaces)
	{
		String html = "";
		String indent = spaces + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		for (BNFToken token : this.getNextTokens())
		{
			String htmlString = "<br/>"+indent+"&rarr;"+token.getString();
// System.out.println(htmlString.replaceAll("&nbsp;", "."));
			html += htmlString;
			html += token.getGraphAsHTML(indent);
		}
		
		return html;
	}
	
	public String getAsJSON()
	{
		String jsonToken = "{ \"string\" : \""    + this.string + "\", " +
				  	 	   "  \"type\" : \""      + this.type   + "\", " +
				  		   "  \"nextTokens\" : " + this.getNextTokensAsJSON() + " }";
		return jsonToken;
	}
	
	public String getNextTokensAsJSON()
	{
		String comma = " ";
		String result = "[";
		for ( BNFToken bnfToken : this.getNextTokens() )
		{
			result += comma + bnfToken.getAsJSON() ;
			comma = ",";
		}
		result += "]";
		
		return result;
	}
	
	public String getAsHTMLJSON()
	{
		return getAsHTMLJSON("");
	}

	public String getAsHTMLJSON(String spaces)
	{
		String indent = spaces + "&nbsp;&nbsp;&nbsp;&nbsp;";
		String jsonToken =  indent + "{ \"string\" : \""        + this.string                          + "\", <br/>" +
							indent + "&nbsp; \"type\"  &nbsp;&nbsp;: \""     + this.type                            + "\", <br/>" +
							indent + "&nbsp; \"nextTokens\" : " + this.getNextTokensAsHTMLJSON(indent) + " }  <br/>";
		return jsonToken;
	}
	
	public String getNextTokensAsHTMLJSON()
	{
		return getNextTokensAsHTMLJSON("");
	}
	
	public String getNextTokensAsHTMLJSON(String spaces)
	{
		String indent = spaces + "&nbsp;&nbsp;&nbsp;&nbsp;";
		String comma = " ";
		String result = "<br/>" + indent + "[ <br/>";
		for ( BNFToken bnfToken : this.getNextTokens() )
		{
			result += comma + bnfToken.getAsHTMLJSON(indent) ;
			comma = ",";
		}
		result += "<br/>" + indent + "] <br/>";
		
		return result;
	}
	
}
