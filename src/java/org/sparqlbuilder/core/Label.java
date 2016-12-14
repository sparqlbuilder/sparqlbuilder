package org.sparqlbuilder.core;

public class Label {

	private String label = null;
	private String language = null;

	public boolean equals(Object oLabel) {
		if( oLabel == null ){
			return false;
		}
		Label loLabel = (Label) oLabel;
		if (label != null) {
			if (!label.equals(loLabel.getLabel())) {
				return false;
			}
		} else {
			if (loLabel.getLabel() != null) {
				return false;
			}
		}

		if (language != null) {
			return language.equals(loLabel.getLanguage());
		} else {
			return loLabel.getLanguage() == null;
		}
	}

	public Label(String label, String language) {
		this.label = label;
		this.language = language;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		if (label != null) {
			sb.append(label);
		}
		sb.append("\"");
		if (language != null && !language.equals("")) {
			sb.append("@");
			sb.append(language);
		}
		return sb.toString();
	}

}
