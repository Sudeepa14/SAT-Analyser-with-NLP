/**
 *
 */
package com.project.traceability.common;

import java.io.File;

import com.project.NLP.file.operations.FilePropertyName;
import static com.project.NLP.file.operations.FilePropertyName.IMAGE_PATH;
import com.project.traceability.staticdata.StaticData;
import com.project.traceability.visualization.VisualizeGraph;

/**
 * 13 Nov 2014
 *
 *
 */
public class PropertyFile {

    public static String requirementXMLPath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"XML Files"+File.separator+"RequirementArtefactFile.xml";
    public static String umlXMLPath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"XML Files"+File.separator+"UMLArtefactFile.xml";
    public static String sourceXMLPath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"XML Files"+File.separator+"SourceCodeArtefactFile.xml";
   // public static final String wordNetDbDirectory = System.getProperty("user.home")+File.separator+"WordNet"+File.separator+"dict";
    public static final String wordNetDbDirectory = "C:\\Users\\SAMITHA-LAP\\WordNet\\dict";

    public static String filePath = StaticData.workspace;
    public static String xmlFilePath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"XML Files";
    public static String docsFilePath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"XML Files"+File.separator;
    public static final String imagePath = IMAGE_PATH ; // img folder in
    public static String configuration_file_path = System.getProperty("user.home")
                +File.separator +"SAT_CONFIGS"+ File.separator +"sat_configuration.xml";
    
    public static String configuration_root = System.getProperty("user.home")
            +File.separator +"SAT_CONFIGS"+ File.separator;
    // project
    private static String projectName = null;
    public static String graphDbPath = "C:\\Users\\SAMITHA-LAP\\Documents\\Neo4j\\default.graphdb";
    private static String generatedGexfFilePath = null;
    private static String relationshipXMLPath = null;
    public static String graphType = null;
    private static VisualizeGraph visual = null;
    public static final String testFilePath =System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator;
    public static final String testXmlFilePath = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"test"+File.separator;
    public static final String testDb = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"Test"+File.separator+"Test.graphdb";
    public static final String testGraphFile = System.getProperty("user.home")+File.separator+"SATAnalyzer"+File.separator+"Test"+File.separator+"Test.gexf";
    public static final String xmlSourceCodeFilePath = PropertyFile.filePath + File.separator + FilePropertyName.XML;
  
    public static String getProjectName() {
        return projectName;
    }

    public static void setProjectName(String projectName) {
        PropertyFile.projectName = projectName;
    }

    public static String getGraphDbPath() {
        return graphDbPath;
    }

    public static void setGraphDbPath(String graphDbPath) {
        PropertyFile.graphDbPath = graphDbPath;
    }

    public static String getGeneratedGexfFilePath() {
        return generatedGexfFilePath;
    }

    public static void setGeneratedGexfFilePath(String generatedGexfFilePath) {
        PropertyFile.generatedGexfFilePath = generatedGexfFilePath;
    }

    public static String getRelationshipXMLPath() {
        return relationshipXMLPath;
    }

    public static void setRelationshipXMLPath(String relationshipXMLPath) {
        PropertyFile.relationshipXMLPath = relationshipXMLPath;
    }

    public static String getGraphType() {
        return graphType;
    }

    public static void setGraphType(String graphType) {
        PropertyFile.graphType = graphType;
    }

    public static VisualizeGraph getVisual() {
        return visual;
    }

    public static void setVisual(VisualizeGraph visual) {
        PropertyFile.visual = visual;
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        PropertyFile.filePath = filePath;
    }

    public static String getXmlFilePath() {
        return xmlFilePath;
    }

    public static void setXmlFilePath(String xmlFilePath) {
        PropertyFile.xmlFilePath = xmlFilePath;
    }

    public static String getTestDb() {
        return testDb;
    }

    public static String getRequirementXMLPath() {
        return requirementXMLPath;
    }

    public static String getUmlXMLPath() {
        return umlXMLPath;
    }

    public static String getSourceXMLPath() {
        return sourceXMLPath;
    }

    public static String getWordNetDbDirectory() {
        return wordNetDbDirectory;
    }

    public static String getImagePath() {
//        return imagePath;
        return "";
    }

    public static String getTestFilePath() {
        return testFilePath;
    }

    public static String getTestXmlFilePath() {
        return testXmlFilePath;
    }

    public static String getTestGraphFile() {
        return testGraphFile;
    }
           
}
