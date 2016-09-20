<!DOCTYPE html>
<%@ page import="com.appbase.fqlwiki.main.*,java.util.List,java.util.Collections,java.util.Map,java.util.Set,java.util.SortedSet" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fqlTags" %>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>


	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css" />
	<link rel="stylesheet" type="text/css" href="css/tab.css" />  
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css" />  
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.structure.min.css" />  
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.theme.min.css" />  
	
	<style>
		code { color: black; background: white; }
	</style>

    <script src="js/tab.js"></script> 
    <script src="js/jquery-1.11.3.min.js"></script> 
    <script src="js/jquery-ui.min.js"></script> 
    <script src="js/jquery.textcomplete.min.js"></script> 

</head>
 
	<% 
		FQLGrammar fqlGrammar = FQLGrammar.getInstance(); 
		List<BNFRule> bnfRules = fqlGrammar.getBNFRules();
		SortedSet<String> terminals = fqlGrammar.getTerminals(); 
		SortedSet<String> nonTerminals = fqlGrammar.getNonTerminals();  

		String bnfRulesHTML = "";
		String statements = "";
		String terminalsHTML = "";
		String nonTerminalsHTML = "";
		String graphsHTML = "";
		String jsonGraph = "";
		String jsonHTMLGraph = "";
		
		for (BNFRule bnfRule : bnfRules)
		{
			if (bnfRule.isRoot())
			{
				statements += bnfRule.getLhs() + "<br/>";
			}
			bnfRulesHTML += (bnfRule.isRoot() ? "<hr/>" : "") + "<p><b>" + bnfRule.getLhs() + "</b> ::= " + bnfRule.getRhs() + "</p>"; 
		}
		
		for (String terminal : terminals)
		{
			terminalsHTML += "<p>" + terminal + "</p>";
		}
		
		for (String nonTerminal : nonTerminals)
		{
			nonTerminalsHTML += "<p>" + nonTerminal + "</p>";
		}

		for (BNFToken bnfToken : fqlGrammar.getLhsDirectory()) 
		{
			graphsHTML += "<p>" + bnfToken.getString() + " ::= " + bnfToken.getGraphAsHTML(); 
//		
//			graphsHTML += "<p>" + bnfToken.getString() + " ::= ";
//			
//			pageContext.setAttribute("bnfToken", bnfToken);
//			pageContext.setAttribute("graphsHTML", graphsHTML);
// % > 
//			<fqlTags:bnfTokenGraph nextTokens="${bnfToken.nextTokens}" />			
// < %
		}

		jsonGraph = fqlGrammar.getGraphAsJSON();
		jsonHTMLGraph = fqlGrammar.getGraphAsHTMLJSON();

	%>

	<div class="w3-container">
		<h2>FQL Interpreter</h2>
		<i>Here is the magic!</i>
	</div>

	<ul class="w3-navbar w3-black">
	  <li><a href="#" onclick="openTab('Statements')">Statements</a></li>
	  <li><a href="#" onclick="openTab('Grammar')">Grammar</a></li>
	  <li><a href="#" onclick="openTab('Terminals')">Terminals</a></li>
	  <li><a href="#" onclick="openTab('Non Terminals')">Non Terminals</a></li>
	  <li><a href="#" onclick="openTab('Graphs')">Graphs</a></li>
	  <li><a href="#" onclick="openTab('Json')">JSON</a></li>
	  <li><a href="#" onclick="openTab('HTMLJson')">Pretty JSON</a></li>
	  <li><a href="#" onclick="openTab('Console')">Console</a></li>
	</ul>

	<div id="Statements" class="w3-container tab">
	  <h2>Statements</h2>
	  <p><%= statements %></p>
	</div>

	<div id="Grammar" class="w3-container tab">
	  <h2>Grammar</h2>
	  <p><%= bnfRulesHTML %></p>
	</div>

	<div id="Terminals" class="w3-container tab">
	  <h2>Terminals</h2>
	  <p><%= terminalsHTML %></p>
	</div>

	<div id="Non Terminals" class="w3-container tab">
	  <h2>Non Terminals</h2>
	  <p><%= nonTerminalsHTML %></p>
	</div>

	<div id="Graphs" class="w3-container tab">
	  <h2>Graphs</h2>
	  <p><%= graphsHTML %></p>
	</div>

	<div id="Json" class="w3-container tab">
	  <h2>JSON</h2>
	  <p><%= jsonGraph %></p>
	</div>

	<div id="HTMLJson" class="w3-container tab">
	  <h2>Pretty JSON</h2>
	  <p><%= jsonHTMLGraph %></p>
	</div>

	<div id="Console" class="w3-container tab">
	  <h2>Console</h2>
	  <div style="float: left;">
		  <p><textarea id="fqlCommand" rows="14" cols="50%"></textarea></p>
	  </div>
	  <div style="float: left; margin-left: 10px;">
		<input type="button" value="RUN" style="width: 70px;" onclick="runFQL();" />
		<div id="run_result"></div>
	  </div>
	</div>

	<script>openTab('Statements');</script>

	<script>
		var terminals = [];
		var nonTerminals = [];
<%
		for (String terminal : terminals)
		{
%>
			terminals.push("<%= terminal %>");
<%
		}
		
		for (String nonTerminal : nonTerminals)
		{
%>
			nonTerminals.push("<%= nonTerminal %>");
<%
		}
%>
	</script>
	
	<script>

		var origins = [];

			var jsonGraph = <%= jsonGraph %> ;

			var matchingNodes = [];
			var suggestions = [];
			
			for ( i=0; i < jsonGraph.length; i++)
			{
				if (jsonGraph[i].type == "ORIGIN") {
					origins.push(jsonGraph[i].nextTokens[0]);
				}
			}
			
			suggestions = getNodesStrings(origins);
			
			function getNodesStrings(nodes)
			{
				var result = [];
				for (n=0; n<nodes.length; n++)
				{
					result.push((nodes[n].string));
				}
				return result;
			}
			
			function getMatchingNodes ( nodes, string ) 
			{
				var matchingNodes = [];
				var varNodes = [];
				for (n=0; n<nodes.length; n++)
				{
					if (nodes[n].string == string) 
					{
						matchingNodes.push(nodes[n]);
					}
					else
					{
						if (nodes[n].type == "VARIABLE")
						{
							varNodes.push(nodes[n]);
						}
					}
				}
				matchingNodes = matchingNodes.length > 0 ? matchingNodes : varNodes;
				return matchingNodes;
			}
			
			function getChildNodes ( nodes )
			{
				var childNodes = [];
				for (n=0; n<nodes.length; n++)
				{
					childNodes = childNodes.concat( nodes[n].nextTokens);
				}
				return childNodes;
			}
			
			function getNextSuggestions()
			{
				var nextSuggestions = origins;

				var content = $('#fqlCommand').val();
				var tokens = (content.trim() == "") ? [] : content.replace(/\s+/g,' ').trim().split(' ');
				var t = 0;
				while ( nextSuggestions.length > 0 && t < tokens.length )
				{
					var token = tokens[t];
					
					var matchingNodes = getMatchingNodes ( nextSuggestions, tokens[t++] );
					nextSuggestions = getChildNodes( matchingNodes );

					while ( t > 1 && t < tokens.length 
					&& getTokenType(token)       == "VARIABLE" 
					&& getTokenType( tokens[t] ) == "VARIABLE" )
					{
						t++;
					}

				}
				return nextSuggestions;
			}
			
			function getTokenType(token)
			{
				var result = "VARIABLE";
				result = (terminals.indexOf(token) > -1) ? "TERMINAL" : result;
				result = (nonTerminals.indexOf(token) > -1) ? "NON_TERMINAL" : result;
				return result;
			}


		function runFQL()
		{
			var variables = {};
			var nextSuggestions = origins;

			var content = $('#fqlCommand').val();
			var tokens = (content.trim() == "") ? [] : content.replace(/\s+/g,' ').trim().split(' ');
			var t = 0;
			while ( nextSuggestions.length > 0 && t < tokens.length )
			{
				var token = tokens[t];

				var matchingNodes = getMatchingNodes ( nextSuggestions, tokens[t++] );

				if (getTokenType(token) == "VARIABLE")
				{
					variables[matchingNodes[0].string] = token;
					if (matchingNodes.length > 1) { $('#run_result').append("<p style='color:red;'>More than one match. Is Grammar ambiguous? (could be an intermediate ambiguity).<p>"); }
				}
									
				nextSuggestions = getChildNodes( matchingNodes );

				while ( t > 1 && t < tokens.length && getTokenType(token) == "VARIABLE" && getTokenType( tokens[t] ) == "VARIABLE" )
				{
					variables[matchingNodes[0].string] += " " + tokens[t++];
				}

			}
			
			for (v in variables)
			{
				$('#run_result').append("<p>" + v + " = " + variables[v] + "</p>"); 
			}
		}

		$(document).ready(function () 
		{
	
			$( "#fqlCommand" ).textcomplete([
			{
//				match: /<(\w*)$/,
				match: /(\w*)$/,
				search: function (term, callback) 
						{
							callback($.map(suggestions, function (suggestion) 
							{
				                return suggestion.indexOf(term) === 0 ? suggestion : null;
							}));
				        },
				index: 1,
				replace: function (suggestion) 
						 {
//								return ['<' + suggestion + '>', '</' + suggestion + '>'];
								return suggestion;
						 }
		    }]);

			$('#fqlCommand').bind('input propertychange', function() {

				suggestions = getNodesStrings( getNextSuggestions() );
				
			});

		});
		
	</script>


</html>
