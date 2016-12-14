package org.sparqlbuilder.core;

import java.util.ArrayList;
import java.util.HashSet;

public class LabelMap {

	private String resourceURI = null;
	private HashSet<Label> labels = null;
	
	public LabelMap(){
		labels = new HashSet<Label>();
	}
	
	public LabelMap(String resourceURI, Label[] labelArray) {
		labels = new HashSet<Label>();
		this.resourceURI = resourceURI;
		if( labelArray != null ){
			for(Label label: labelArray){
				labels.add(label);
			}
		}
	}

	public void addLabel(Label label){
		labels.add(label);
	}

	
	public Label[] getLabels(){
		return labels.toArray(new Label[0]);
	}

	public Label[] getLabels(String language){
		Label[] lbs = getLabels();
		ArrayList<Label> labelAL = new ArrayList<Label>();
		for(Label label: lbs){
			if( language == null ){
				labelAL.add(label);
			}else{
				if( language.equals(label.getLanguage())){
					labelAL.add(label);
				}
			}
		}
		return labelAL.toArray(new Label[0]);
	}

	public void setResourceURI(String uri){
		resourceURI = uri;
	}

	public String getResourceURI(){
		return resourceURI;
	}
	
}
