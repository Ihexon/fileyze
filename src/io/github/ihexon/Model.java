package io.github.ihexon;

import org.xml.sax.SAXException;

import java.io.IOException;

public class Model {
	private static Model model = null;
	private OptionsParam optionsParam = null;

	public static Model getSingleton() {
		if (model == null) {
			// ZAP: Changed to use the method createSingleton().
			createSingleton();
		}
		return model;
	}

	private static synchronized void createSingleton() {
		if (model == null) {
			model = new Model();
		}
	}



}
