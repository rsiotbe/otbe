package com.rsi.rvia.rest.client;

import java.util.Vector;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryBuilder
{
	private static Logger	pLog	= LoggerFactory.getLogger(QueryBuilder.class);
	private Vector<String> fieldList;
	private Vector<String> sorterList;
	private String query;

	public void setFieldList(Vector<String> pFieldList) throws Exception{
		fieldList = pFieldList;	
	}
	public void setSorterList(Vector<String> pSorterList) throws Exception{
		sorterList = pSorterList;	
	}
	public String parseQuery(String query) throws Exception{		
		return query;
	}	
	private void getAvailableFieldList(){
		
	}
}
