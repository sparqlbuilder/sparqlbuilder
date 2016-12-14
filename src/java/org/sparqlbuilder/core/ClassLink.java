package org.sparqlbuilder.core;

import java.util.*;

/**
 * クラスへ、あるいはクラスからの1ステップリンクを記述する
 * @author Norio KOBAYASHI
 * @since 28.01.2014
 * @version 29.01.2014
 */
public class ClassLink {

	private String propertyURI = null;
	private String linkedClassURI = null;
	private String linkedLiteralDatatypeURI = null;
	private Direction direction = null;
	private int numOfLinks = 0;
	private int numOfLinkedInstances = 0;
	private int numOfOriginInstances = 0;
	private int numOfOriginClassInstances = 0;
	private int numOfLinkedClassInstances = 0;
	private boolean domainClassLimitedQ = false;
	private boolean rangeClassLimitedQ = false;
	
	
	/**
	 * プロパティURI、リンク先（元）クラス、リンクの向きを与える構成子
	 * 
	 * @param propertyURI　プロパティのURI
	 * @param linkedClassURI　リンクの主語、、あるいはリンクの目的語となっているクラスのURI
	 * @param direction プロパティの向き、linkedClassURIのクラスがリンク先になっているときはDirection.forward,リンク元になっているときはDirection.reverse, それら両方の時はDirection.bothを指定する 
	 * @param numOfLinks 当該プロパティで両端クラスのインスタンス同士をつないでいるリンク数（トリプル数）
	 * @throws Exception
	 * @since 28.01.2014
	 */
	public ClassLink(String propertyURI, String linkedClassURI, String linkedLiteralDatatypeURI, Direction direction, 
				int numLinks, int numOfOriginInstances, int numOfLinkedInstances,
				int numOfOriginClassInstances, int numofLinkedClassInstances,
				boolean domainClassLimitedQ, boolean rangeClassLimitedQ){
		this.propertyURI = propertyURI;
		this.linkedClassURI = linkedClassURI;
		this.linkedLiteralDatatypeURI = linkedLiteralDatatypeURI;
		this.direction = direction;
		this.numOfLinks = numLinks;
		this.numOfLinkedInstances = numOfLinkedInstances;
		this.numOfOriginInstances = numOfOriginInstances;
		this.numOfOriginClassInstances = numOfOriginClassInstances;
		this.numOfLinkedClassInstances = numofLinkedClassInstances;
		this.domainClassLimitedQ = domainClassLimitedQ;
		this.rangeClassLimitedQ = rangeClassLimitedQ;
	}
	
	
	public String toJSONString(Map<String, ClassInfo> cinfo) {
		String json_str ="{";
		
		if( propertyURI != null ) {
			//json_str+="\"propertyURI\":"+"\""+propertyURI+"\",";
                    json_str+="\"predicate\":"+"\""+propertyURI+"\",";
		}
		else{
			json_str+="\"predicate\":"+"\"propertyURI\",";			
		}
		if( linkedClassURI != null ){
			//json_str+="\"linkedClassURI\":"+"\""+linkedClassURI+"\",";
                    json_str+="\"linkedClass\":"+"\""+linkedClassURI+"\",";
		}
		else{
			//json_str+="\"linkedClassURI\":"+"\"linkedClassURI\",";	
                    json_str+="\"linkedClass\":"+"\""+linkedClassURI+"\",";
		}
		if( linkedLiteralDatatypeURI != null ){
			json_str+="\"linkedLiteralDatatypeURI\":"+"\""+linkedLiteralDatatypeURI+"\",";
		}
		else{
			json_str+="\"linkedLiteralDatatypeURI\":"+"\"linkedLiteralDatatypeURI\",";			
		}
                if( direction != null ){
			json_str+="\"direction\":"+"\""+direction.toString()+"\",";
		}
		else{
			json_str+="\"direction\":"+"\"direction\",";			
		}
                if ( linkedClassURI != null ){
                    String label = cinfo.get(linkedClassURI).prlabel;
                    json_str+="\"label\":"+"\""+label+"\"";
                }else if ( linkedLiteralDatatypeURI != null ){
                    String url[] = linkedLiteralDatatypeURI.split("/");
                    String tmplabel = url[url.length-1];
                    String[] tmplabel2 = tmplabel.split("#");
                    String label = tmplabel2[tmplabel2.length-1];
                    json_str+="\"label\":"+"\""+label+"\"";          
                }else{
                    json_str+="\"label\":"+"\""+"\"No Label\""+"\"";               
                }
		json_str+="}";

		return json_str;
	}
        
        
	public int getNumOfLinks() {
		return numOfLinks;
	}




	public void setNumOfLinks(int numOfLinks) {
		this.numOfLinks = numOfLinks;
	}

	/**
	 * 文字列表記を取得する
	 * 
	 * @since 28.01.2014
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(propertyURI);
		if( direction == Direction.forward ){
			sb.append(" --> ");
		}else{
			if( direction == Direction.reverse ){
			sb.append(" <-- ");
			}else{
				sb.append(" <-> ");
			}
		}
		sb.append(linkedClassURI);
		sb.append(" [");
		sb.append(numOfOriginClassInstances);
		sb.append("/");
		sb.append(numOfOriginInstances);
		sb.append("]　---");

		
		sb.append(" [");
		sb.append(numOfLinks);
		sb.append("] --->");

		sb.append(" [");
		sb.append(numOfLinkedClassInstances);
		sb.append("/");
		sb.append(numOfLinkedInstances);
		sb.append("]");
		return sb.toString();
	}
	
	public String getPropertyURI() {
		return propertyURI;
	}
	public void setPropertyURI(String propertyURI) {
		this.propertyURI = propertyURI;
	}
	public String getLinkedClassURI() {
		return linkedClassURI;
	}
	public void setLinkedClassURI(String linkedClassURI) {
		this.linkedClassURI = linkedClassURI;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public final int getNumOfLinkedInstances() {
		return numOfLinkedInstances;
	}

	public final void setNumOfLinkedInstances(int numOfLinkedInstances) {
		this.numOfLinkedInstances = numOfLinkedInstances;
	}


	
	
	public final boolean isDomainClassLimitedQ() {
		return domainClassLimitedQ;
	}


	public final boolean isRangeClassLimitedQ() {
		return rangeClassLimitedQ;
	}


	public final int getNumOfOriginInstances() {
		return numOfOriginInstances;
	}


	public final void setNumOfOriginInstances(int numOfOriginInstances) {
		this.numOfOriginInstances = numOfOriginInstances;
	}


	public final int getNumOfOriginClassInstances() {
		return numOfOriginClassInstances;
	}


	public final void setNumOfOriginClassInstances(int numOfOriginClassInstances) {
		this.numOfOriginClassInstances = numOfOriginClassInstances;
	}


	public final int getNumOfLinkedClassInstances() {
		return numOfLinkedClassInstances;
	}


	public final void setNumOfLinkedClassInstances(int numOfLinkedClassInstances) {
		this.numOfLinkedClassInstances = numOfLinkedClassInstances;
	}


	public final String getLinkedLiteralDatatypeURI() {
		return linkedLiteralDatatypeURI;
	}


	public final void setLinkedLiteralDatatypeURI(String linkedLiteralDatatypeURI) {
		this.linkedLiteralDatatypeURI = linkedLiteralDatatypeURI;
	}


	public final void setDomainClassLimitedQ(boolean domainClassLimitedQ) {
		this.domainClassLimitedQ = domainClassLimitedQ;
	}


	public final void setRangeClassLimitedQ(boolean rangeClassLimitedQ) {
		this.rangeClassLimitedQ = rangeClassLimitedQ;
	}
	
        /*
        private String getLinkedClassLabel(SClass[] classes){
            return QueryPathGenerator.getClassLabelfromList(linkedClassURI, classes);
        }
	*/
        
	// private String[] propertyDomainClassURIs = null;
	// private String[] propertyRangeClassURIs = null;
	
	
	
}
