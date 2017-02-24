

package com.project.traceability.ontology.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.traceability.GUI.WordExpWindows;

public class StaticData {

	public static final String OWL_ROOT_URI = "http://www.sat.analyser.com/words/";
	public static final String OWL_WORD_ROOT = "Word";
	public static final String OWL_NOUN = "Noun";
	public static final String OWL_VERB = "Verb";
	public static final String OWL_OTHER_WORD = "Others";
	
	public static final String OWL_ATTRIBUTE = "Attributes";
	public static final String OWL_CLASS = "Classes";
	public static final String OWL_OTHER_NOUN = "Other_Noun";
	public static final String OWL_METHOD = "Methods";
	public static final String OWL_OTHER_VERB = "Other_Verb";
	
	public static final String EXP_WORD_NOUN = "Word:Noun";
	public static final String EXP_WORD_VERB= "Word:Verb";
	public static final String EXP_WORD_OTHER = "WORD:Other";
	
	public static final String OWL_FILE_FORMAT = "RDF/XML-ABBREV";
	public static final String OWL_PROPERTY = "";
	
        public static final String OWL_INFOS = "If you add both words"
                +WordExpWindows.word1+ " & " + WordExpWindows.word2 +", "+
                " you do not need to add for futher projects\n\n"+
                "Do you want to add to Dictionary those words?";
    public static boolean isAdded = false;
    public static boolean isStartedJustNow = false;
    public static List<String> parent1 = new ArrayList<>();
    public static List<String> parent2 =new ArrayList<>();
    public static Map<String, List<String>> map = new HashMap<String, List<String>>();
    
}