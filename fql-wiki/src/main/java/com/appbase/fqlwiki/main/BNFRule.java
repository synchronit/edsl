package com.appbase.fqlwiki.main;

public class BNFRule {
	
	private String lhs;
	private String rhs;
	private boolean isRoot;
	
	public BNFRule(String stmt, boolean isRoot)
	{
		String[] aux = stmt.split("::=");

		this.lhs = (aux.length < 1) ? "Error splitting "+stmt : aux[0].replaceAll("&lt;|&gt;","");
		this.rhs = (aux.length < 2) ? null : aux[1];
		this.isRoot = isRoot;
	}
	
	public String getLhs() {
		return lhs;
	}

	public String getRhs() {
		return rhs;
	}

	public boolean isRoot() {
		return isRoot;
	}
	
}
