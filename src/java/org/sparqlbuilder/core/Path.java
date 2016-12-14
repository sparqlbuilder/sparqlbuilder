package org.sparqlbuilder.core;

import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 起点となるリソースから逐次的に複数リンクで終点リソースまで接続される一つのパスを記述する
 * 
 * @author Yamaguchi
 * @since 28.01.2014
 * @version 29.01.2014
 */
public class Path implements Comparable<Path>{

	/**
	 * パスの起点となるクラスのURI
	 */
	private String startClass;
	//private int width;
        private float width;
        private int min;
        private boolean chk;

	/**
	 * パスの起点から終点に向かって逐次的につながるクラス間リンクのリスト
	 */
	private List<ClassLink> classLinks;
        
	public String toJSONString(Map<String, ClassInfo> cinfo){
		String json_str="";
		json_str+="{\"startClass\":\""+ startClass+"\",";
                // label
                json_str+="\"label\":\""+cinfo.get(startClass).prlabel+"\",";
                if (classLinks != null && classLinks.size() != 0) {
			json_str+="\"classLinks\":[";
			
			//JSONObject[] classLinkObjs = new JSONObject[classLinks.size()];
			for (int i = 0; i < classLinks.size(); i++) {
				if(i>0){json_str += "," ;}
				json_str+= classLinks.get(i).toJSONString(cinfo);
			}
			json_str+="]";                        
		}
                json_str += ",";
                //json_str +="\"score\":\""+width+"\"";
                json_str +="\"score\":\""+""+"\"";
		json_str +="}";
		
		return json_str;
	}

        public String getStartClass() {
		return startClass;
	}

	/*
	 * public String[] getProperties(){ return properties; }
	 * 
	 * public String[] getObjectClasses(){ return objectClasses; }
	 * 
	 * public Direction[] getDirections(){ return directions; }
	 */
	public List<ClassLink> getClassLinks() {
		return classLinks;
	}

	public float getWidth() {
		return width;
	}

	public Path() {
	}

	public Path(String startClass, List<ClassLink> classLinks, int width) {
		this.startClass = startClass;
		this.classLinks = classLinks;
		this.width = width;
	}

	public void setStartClass(String startClass) {
		this.startClass = startClass;
	}

	public void setClassLinks(List<ClassLink> classLinks) {
		this.classLinks = classLinks;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setWidth(double width) {
		this.width = (float) width;
	}
        
        public void setMin(int min){
            this.min = min;
        }
        
        public int getMin(){
            return min;
        }
        
        public void setChk(boolean chk){
            this.chk = chk;
        }
        
        public boolean getChk(){
            return chk;
        }
                
    @Override
    public int compareTo(Path path) {
        if ( this.width - path.getWidth() > 0 ){ return 1; }
        else if ( this.width - path.getWidth() < 0 ){ return -1; }
        else { 
            if ( this.chk == true && path.getChk() == false ){ return 1; }
            if ( this.chk == false && path.getChk() == true ){ return -1; }
            else {
                if ( path.classLinks.size() - this.classLinks.size() > 0 ){ return 1;}
                else if ( path.classLinks.size() - this.classLinks.size() < 0 ){ return -1;}
                else{ 
                    if ( this.min - path.getMin() > 0 ){ return 1; }
                    else if ( this.min - path.getMin() < 0 ){ return -1;}
                    return 0;
                }
            }
        }
        //return this.width - path.getWidth() ;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}