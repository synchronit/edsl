package com.appbase.fqlwiki.main;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

public class FQLGrammar {

	private static FQLGrammar instance = null;

	public static synchronized FQLGrammar getInstance() {
	    if (instance == null) {
	        instance = new FQLGrammar();
	    }
	    return instance;
	}	

	private FQLWikiUtils fqlWikiUtils = FQLWikiUtils.getInstance();
	
	private List<BNFRule> bnfRules  = null;
	private SortedSet<String> terminals = null;
	private SortedSet<String> nonTerminals = null;
	private List<BNFToken> origins = null;
	private Set<BNFToken> lhsDirectory = null;
	
	private FQLGrammar()
	{
		bnfRules = fqlWikiUtils.getBNFRules();
		terminals = fqlWikiUtils.getTerminals();
		nonTerminals = fqlWikiUtils.getNonTerminals();
		lhsDirectory = fqlWikiUtils.getLhsDirectory();
	}
	
	public List<BNFToken> getOrigins() {
		return origins;
	}

	public SortedSet<String> getTerminals() {
		return terminals;
	}

	public SortedSet<String> getNonTerminals() {
		return nonTerminals;
	}

	public List<BNFRule> getBNFRules() {
		return bnfRules;
	}

	public Set<BNFToken> getLhsDirectory() {
		return lhsDirectory;
	}

	public String getGraphAsJSON()
	{
		String comma = " ";
		String result = "[ ";
		for (BNFToken bnfToken : this.lhsDirectory) 
		{
			result += comma +
							"{ \"string\" : \""    + bnfToken.getString() + "\", " +
				  	 	   	"  \"type\" : \""      + bnfToken.getType()   + "\", " +
				  	 	   	"  \"nextTokens\" : " + bnfToken.getNextTokensAsJSON() + " }";
			comma = ",";
		}
		result += " ]";
		return result;
	}
		
	public String getGraphAsHTMLJSON()
	{
		String comma = " ";
		String result = "<code>[ <br/>";
		for (BNFToken bnfToken : this.lhsDirectory) 
		{
			result += comma +
							"&nbsp;&nbsp;&nbsp;{ \"string\" : \""                + bnfToken.getString()             + "\", <br/>" +
				  	 	   	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; \"type\" &nbsp;&nbsp;: \"" + bnfToken.getType()         + "\", <br/>" +
				  	 	   	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; \"nextTokens\" : " + bnfToken.getNextTokensAsHTMLJSON() +  " } <br/>";
			comma = ",";
		}
		result += "<br/>] </code>";
		return result;
	}
		
	
}
