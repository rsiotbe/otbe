package com.rsi.rvia.rest.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryCustomizer
{
	private static Logger	pLog	= LoggerFactory.getLogger(QueryCustomizer.class);
	private String fieldsList = "*";
	private String sortersList = null;
	private String[] _reserved = new String[7];
	public QueryCustomizer(){
		_reserved [0] = "select" ;
		_reserved [1] = "update" ;
		_reserved [2] = "delete" ;
		_reserved [3] = "insert" ;
		_reserved [4] = "alter"  ;
		_reserved [5] = "drop"   ;
		_reserved [6] = "create" ;
	}
	private String protectInject(String strFields) {
		if(strFields == null)
			return null;
		int i;
		for(i=0; i<_reserved.length; i++){
			strFields = strFields.replaceAll("(?i)" + _reserved[i],"");
		}
		return strFields;
	}
	public void setFieldsList(String pFieldsList) throws Exception{		
		pFieldsList = protectInject(pFieldsList);
		if(pFieldsList != null)
			fieldsList = pFieldsList.replaceAll("([A-z]+)", "\"$1\"");	
	}
	public void setSortersList(String pSortersList) throws Exception{
		pSortersList = protectInject(pSortersList);
		if(pSortersList != null)
			sortersList = pSortersList.replaceAll("([A-z]+)", "\"$1\"");
	}
	public String getParsedQuery(String query) throws Exception{	
		if(sortersList == null && fieldsList == null)			
			return query;
		String qParsed = "Select __XX__ from ( " ;		
		qParsed = qParsed + query;		
		qParsed = qParsed + " ) __ZZ__ ";	
		if(sortersList == null && fieldsList != null){
			String rr = qParsed.replace("__XX__",fieldsList).replace("__ZZ__","");
			return qParsed.replace("__XX__",fieldsList).replace("__ZZ__","");	
		}
		if(sortersList != null && fieldsList == null){
			String rr = qParsed.replace("__XX__","").replace("__ZZ__"," order by " + sortersList);
			return qParsed.replace("__XX__","").replace("__ZZ__"," order by " + sortersList);
		}
		if(sortersList != null && fieldsList != null){
			String rr = qParsed.replace("__XX__",fieldsList).replace("__ZZ__"," order by " + sortersList);
			return qParsed.replace("__XX__",fieldsList).replace("__ZZ__"," order by " + sortersList);
		}		
		return query;
	}	
}
