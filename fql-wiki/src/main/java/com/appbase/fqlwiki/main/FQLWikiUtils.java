package com.appbase.fqlwiki.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FQLWikiUtils {

	private static FQLWikiUtils instance = null;

	public static synchronized FQLWikiUtils getInstance() {
	    if (instance == null) {
	        instance = new FQLWikiUtils();
	    }
	    return instance;
	}		
	
	public static final String URL = "http://fitnesse.applicationbase.org/Reference";
	
	private List<BNFRule> bnfRules = null;
	private SortedSet<String>   terminals = null;
	private SortedSet<String>   nonTerminals = null;
	
	private Set<BNFToken> lhsDirectory = null;
	
	private String[] symbolsArray = { "[", "]", "(", ")", "|" };
	private List<String> bnfSymbols = Arrays.asList( symbolsArray );
	
	private FQLWikiUtils()
	{
	}
	
	public List<BNFRule> getBNFRules()
	{
		if (bnfRules == null)
		{
			bnfRules = new ArrayList<BNFRule>();
			List<String> stmtLinks = this.getStmtLinks(URL);
			
			for (String link : stmtLinks)
			{
				boolean isRoot = true;

				try
				{
					Document doc = Jsoup.connect(link)
							.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
							.followRedirects(true)
							.timeout(60000)
							.get();
					
					Elements elements = doc.select("fql-stmt");
					
					isRoot = true;
					for (Element e : elements)
					{
						bnfRules.add(new BNFRule(e.html(), isRoot));
						isRoot = false;
					}

				}
			    catch (IOException e) {
			    	bnfRules.add(new BNFRule("Error while accesing "+link+" : "+e.toString(), isRoot));
			    }
			}
		
		}
		return this.bnfRules;
	}

	private List<String> getStmtLinks(String url)
	{
		List<String> links = new ArrayList<String>();

		try 
		{
			Document doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
					.followRedirects(true)
					.get();
			
			Elements elements = doc.select("fql-link a");
			
			links = new ArrayList<String>();
			
			for (Element e : elements)
			{
				links.add(e.attr("abs:href"));
			}
			
		} catch (Exception e) {
			links.add("Error accesing "+url+" : "+e.toString());
	    }

		return links;
	}
	
	private String removeHTMLTags(String string)
	{
		String result = string
							.replaceAll("(<b>|<\\/b>)", "")
							.replaceAll("(<i>|<\\/i>)", "");
		return result;
	}

	/*
	private String removeSpecialChars(String string)
	{
		String result = string;
        String pattern = "[A-Za-z0-9]+"; 
        boolean hasAlphanumericChars = Pattern.compile(pattern).matcher(string).find();
        if (hasAlphanumericChars)
        {
        	result = string.replaceAll("[^A-Za-z0-9_]", "");
        }
        return result;
	}
	*/
	
	private String separateTerminalChars(String string)
	{
		String result = string;

//		result = result.replaceAll("(?<!')\\[(?=')", " [ ");

		if (result != null)
		{
			result = result.replaceAll("\\[", " [ ");
			result = result.replaceAll("\\]", " ] ");

			result = result.replaceAll("\\{", " { ");
			result = result.replaceAll("}", " } ");
			
			result = result.replaceAll("(?<!\\<[bB]\\>)\\((?!\\<\\/[bB]\\>)", " <b>(</b> ");
			result = result.replaceAll("(?<!\\<[bB]\\>)\\)(?!\\<\\/[bB]\\>)", " <b>)</b> ");
	
			result = result.replaceAll("\\|", " | ");
		}
		
		return result;
	}
	
	private void tokenizeRules()
	{
		terminals = new TreeSet<String>();
		nonTerminals = new TreeSet<String>();
		
		lhsDirectory = new HashSet<BNFToken>();
		
		for (BNFRule bnfRule : bnfRules)
		{
			String lhs = bnfRule.getLhs();
			String rhs = separateTerminalChars(bnfRule.getRhs());

			BNFToken token = new BNFToken( lhs, (bnfRule.isRoot() ? BNFToken.ORIGIN : BNFToken.LHS) );
			BNFToken lhsToken = token;
			lhsDirectory.add(token);
			
			BNFToken previousToken = token;
			String type = null;

			String[] words = rhs.split(" ");

			Stack<BNFToken> stack = new Stack<BNFToken>();
			Stack<BNFToken> orStack = new Stack<BNFToken>();
			Stack<BNFToken> optionsStack = new Stack<BNFToken>();

			int closeOptional = 0;
			boolean firstOROption = true;
			boolean lastOROption = false;
			
			for (String w : words)
			{
				String word = w.trim();

				if (word.trim().length() > 0)
				{
					if ( bnfSymbols.contains( word ) )
					{
						if (word.equals("[")) {
							stack.push(previousToken);
						}
						if (word.equals("]")) {
							closeOptional++;
						}
						if (word.equals("(")) {
							orStack.push(previousToken);
							firstOROption = true;
						}
						if (word.equals("|")) {
							optionsStack.push(previousToken);
							firstOROption = true;
							
							if (orStack.size() == 0) {
								orStack.push(lhsToken);
							}
						}
						if (word.equals(")")) {
							lastOROption = true;
						}
					}
					else
					{
						if (word.matches("&lt;.+&gt;"))
						{
							word = word.replaceAll("&lt;|&gt;","");
							nonTerminals.add(word);
							type = BNFToken.NON_TERMINAL;
						}
						else
						{
							word = removeHTMLTags(word);
							if (word.equals(word.toUpperCase())) 
							{
//								word = removeSpecialChars(word);	// interim solution: to be removed once special chars are properly processed
	
								terminals.add(word);
								type = BNFToken.TERMINAL;
							}
							else
							{
								type = BNFToken.VARIABLE;
							}
						}

						BNFToken newToken = new BNFToken(word, type);

						if (closeOptional > 0) 
						{
							stack.pop().addNext(newToken);
							closeOptional--;
						}

						if (orStack.size() > 0 && firstOROption) 
						{
							orStack.peek().addNext(newToken);
							firstOROption = false;
						}
						else 
						{
							if (newToken.getType().equals(BNFToken.VARIABLE) 
							&&  previousToken.getType().equals(BNFToken.VARIABLE))
							{
								String newString = previousToken.getString() + "_" + newToken.getString();
								previousToken.setString(newString);
								newToken = previousToken;
							}
							else
							{
								previousToken.addNext(newToken);	// Normal Case
							}
						}
							

						if (lastOROption) 
						{
							while (optionsStack.size() > 0) 
							{
								optionsStack.pop().addNext(newToken);
							}
							lastOROption = false;
							orStack.pop();
						}
						
						previousToken = newToken;
					}
				}				
			}
		}
	}
	

	public SortedSet<String> getTerminals()
	{
		if (terminals == null)
		{
			this.tokenizeRules();			
		}
		return terminals;
	}
	
	
	public SortedSet<String> getNonTerminals()
	{
		if (nonTerminals == null)
		{
			this.tokenizeRules();			
		}
		return nonTerminals;
	}
	
	public Set<BNFToken> getLhsDirectory()
	{
		if (lhsDirectory == null)
		{
			this.tokenizeRules();
		}
		return lhsDirectory;
	}

}
