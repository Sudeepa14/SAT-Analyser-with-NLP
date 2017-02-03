/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.NLP.UMLToXML.xmlwriter;

/**
 *
 * @author shiyam
 */
import java.io.File;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.project.NLP.file.operations.FilePropertyName;
import com.project.property.config.xml.writer.Adapter;
import com.project.traceability.GUI.ProjectCreateWindow;
import com.project.traceability.model.Attribute;
import com.project.traceability.model.Dependencies;
import com.project.traceability.model.ModelData;
import com.project.traceability.model.Operation;
import com.project.traceability.model.Parameter;
import com.project.traceability.staticdata.StaticData;

public class WriteToXML {

    public static HashMap<String, String> keyIDMap = new HashMap<>();
    public int count = 0;
    public static String fileName = "";
    public String  type = "UMLDiagram";//can change when requirement file writing as xml format
    private static int attrId = 1;
    private static int methodId = 1;

    public void createXML() {
        List<ModelData> classLst = StaticData.classLst;

        //List<Dependencies> depencyList = StaticData.depencyList;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement(StaticData.ARTEFACTS_ROOT);
            Element artifact = doc.createElement(StaticData.ARTEFACT_ROOT);
            doc.appendChild(rootElement);
            rootElement.appendChild(artifact);

            Attr attrType = doc.createAttribute("type");
            attrType.setValue(type);
            artifact.setAttributeNode(attrType);
            for (int i = 0; i < classLst.size(); i++) {
                attrId = 1;
                methodId = 1;
                ModelData tempData = classLst.get(i);

                //putting AretefactElement
                Element artifactElement = doc.createElement(StaticData.ARTEFACTELEMENT_ROOT);
                artifact.appendChild(artifactElement);//append an artifact main root

                //class/Inteface Behavior is adding
                Attr classNameAttr = doc.createAttribute(StaticData.NAME_ROOT);
                classNameAttr.setValue(tempData.getName());
                artifactElement.setAttributeNode(classNameAttr);

                Attr typeAttr = doc.createAttribute(StaticData.TYPE_ROOT);
                typeAttr.setValue(tempData.getType());
                artifactElement.setAttributeNode(typeAttr);

                Attr classIDAttr = doc.createAttribute(StaticData.ID_ROOT);
                String id = getDesignElementID();
                classIDAttr.setValue(id);
                artifactElement.setAttributeNode(classIDAttr);

                if(type.equals("Sourcecode")){
                	Attr typeVisibility = doc.createAttribute(StaticData.VISIBILITY_ROOT);
                	typeVisibility.setValue(tempData.getVisibility());
                    artifactElement.setAttributeNode(typeVisibility);
                    
                    Attr statusAttr = doc.createAttribute(StaticData.STATUS);
                    statusAttr.setValue(tempData.getStatus());
                    artifactElement.setAttributeNode(statusAttr);
                    
                    Attr interfaceAttr = doc.createAttribute(StaticData.INTERFACES);
                    interfaceAttr.setValue(tempData.getInterfaceNames());
                    artifactElement.setAttributeNode(interfaceAttr);
                    
                    Attr superClassAttr = doc.createAttribute(StaticData.SUPER_CLASSES);
                    superClassAttr.setValue(tempData.getSuperclassNames());
                    artifactElement.setAttributeNode(superClassAttr);
                    
                }
                keyIDMap.put(tempData.getId(), id);

                //class/Interface Behavior finished
                //Attribute Element is adding to ArtifactSubElement
                createAttributeElement(doc, artifactElement, tempData);
                //Attribute Element added

                //Operation Element is adding to ArtifactSubElement
                createMethodElement(doc, artifactElement, tempData);
                //Operation Element Added

                //putting INTRACONNECTION
            }

            Element intraConnectionElement = doc.createElement(StaticData.INTRACONNECTION_ROOT);
            artifact.appendChild(intraConnectionElement);//append an artifact main Artifact

            //Connection Element is adding to InterConnectionsElement
            //createConnectionElement(doc, intraConnectionElement);

            // write the content into xml file///chenaged to general method in FilePropertyName
            String fDir = getFileDir();
            System.out.println("------Writing  XML to Workspace/projectname/xml directory : " + fDir);
         	File xmlFile = new File(fDir);
            FilePropertyName.writeToXML(xmlFile.getPath(), doc);

        } catch (ParserConfigurationException pce) {

        	pce.printStackTrace();
            MessageBox box = new MessageBox(ProjectCreateWindow.shell,
                    SWT.ERROR);
            box.setText("File Not Found Error");
            box.setMessage(pce.toString());
            box.open();

        }
    }

    private String getFileDir() {
        String dir = System.getProperty("user.home") + File.separator + "RequirementArtefactFile.xml";//default

        try {
            String root = Adapter.projectPath;
            System.out.println("------Writing Requirement XML -- root--:  " + root);
            File f = new File(root + File.separator + FilePropertyName.XML);
            System.out.println("------Writing Requirement XML -- XML folder path --:  " + f.getPath());
            if (!f.exists()) {
                System.out.println("------Writing Requirement XML -- XML folder path is exist--:  ");
                f.mkdir();
            }
            if (type.equals("Requirement")) {
                System.out.println("------Writing Requirement XML -- type :" + type);
                dir = f.getPath() + File.separator
                        + FilePropertyName.REQUIREMENT_ARTIFACT_NAME;
            } else if(type.equals("Sourcecode")){
            	 System.out.println("------Writing SourceCode XML -- type :" + type);
                 dir = f.getPath() + File.separator
                         + FilePropertyName.SOURCE_ARETEFACT_NAME_MODIFIED;
            }else {
                System.out.println("------Writing UMLDiagram XML -- type :" + type);
                dir = f.getPath() + File.separator
                        + FilePropertyName.UML_ARTIFACT_NAME;
            }
        } catch (Exception e) {

        }

        System.out.println("------Writing Requirement XML -- XML folder path is returned--:  " + dir);
        return dir;
    }

    private String getDesignElementID() {
        String ID;
        if (type.equals("Requirement")) {
            count++;
            ID = "RQ".concat(Integer.toString(count));

        } else if (type.equals("Sourcecode")){
        	count++;
            ID = "SC".concat(Integer.toString(count));
        }else {
            count++;
            ID = "D".concat(Integer.toString(count));

        }
        return ID;
    }

    private String getAttributeID() {
        String ID;
        if (type.equals("Requirement")) {            
            ID = "RQ".concat(Integer.toString(count)).concat("_F").
            		concat(Integer.toString(attrId));

        }else if(type.equals("Sourcecode")) {
        	ID = "SC".concat(Integer.toString(count)).concat("_F").
            		concat(Integer.toString(attrId));
        }else {           
        
            ID = "D".concat(Integer.toString(count)).concat("_F").
            		concat(Integer.toString(attrId));

        }
        attrId++;
        return ID;
    }

    private String getMethodID() {        
        String ID;
        if (type.equals("Requirement")) {            
            ID = "RQ".concat(Integer.toString(count)).concat("_M").concat(Integer.toString(methodId));

        }else if(type.equals("Sourcecode")) {
        	ID = "SC".concat(Integer.toString(count)).concat("_M").
            		concat(Integer.toString(methodId));
        } else {
            ID = "D".concat(Integer.toString(count)).concat("_M").concat(Integer.toString(methodId));

        }
        methodId++;
        return ID;
    }

    private void createAttributeElement(Document doc, Element artifactElement, ModelData tempData) {
        try {

            List<Attribute> attributeLst = tempData.getAttributeList();
            for (int i = 0; i < attributeLst.size(); i++) {
                Attribute attribute = attributeLst.get(i);
                //putting AretefactSubElement
                Element artifactSubElement = doc.createElement(StaticData.ARTEFACTUBELEMENT_ROOT);
                artifactElement.appendChild(artifactSubElement);//append an artifact main root

                Attr attrName = doc.createAttribute(StaticData.NAME_ROOT);
                attrName.setValue(attribute.getName());
                artifactSubElement.setAttributeNode(attrName);

                Attr attrType = doc.createAttribute(StaticData.TYPE_ROOT);
                attrType.setValue(attribute.getType());
                artifactSubElement.setAttributeNode(attrType);

                Attr attrVisibility = doc.createAttribute(StaticData.VISIBILITY_ROOT);
                attrVisibility.setValue(attribute.getVisibility());
                artifactSubElement.setAttributeNode(attrVisibility);

                Attr attrID = doc.createAttribute(StaticData.ID_ROOT);
                attrID.setValue(getAttributeID());
                artifactSubElement.setAttributeNode(attrID);

                Attr attrVariableType = doc.createAttribute(StaticData.VARIABLE_TYPE_ROOT);
                attrVariableType.setValue(attribute.getDataType());
                artifactSubElement.setAttributeNode(attrVariableType); 
                
                if(type.equals("Sourcecode")){
	                Attr attrStatus = doc.createAttribute(StaticData.STATUS);
	                attrStatus.setValue(attribute.getStatus());
	                artifactSubElement.setAttributeNode(attrStatus);
                }
                
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createMethodElement(Document doc, Element artifactElement, ModelData tempData) {
        try {

            List<Operation> mtdList = tempData.getOperationList();
            for (int i = 0; i < mtdList.size(); i++) {
                Operation operation = mtdList.get(i);
                //putting AretefactSubElement
                Element artifactSubElement = doc.createElement(StaticData.ARTEFACTUBELEMENT_ROOT);
                artifactElement.appendChild(artifactSubElement);//append an artifactSubElement main of ArtifactElement

                Attr attrName = doc.createAttribute(StaticData.NAME_ROOT);
                attrName.setValue(operation.getName());
                artifactSubElement.setAttributeNode(attrName);

                Attr attrType = doc.createAttribute(StaticData.TYPE_ROOT);
                attrType.setValue(operation.getType());
                artifactSubElement.setAttributeNode(attrType);

                Attr attrVisibility = doc.createAttribute(StaticData.VISIBILITY_ROOT);
                attrVisibility.setValue(operation.getVisibility());
                artifactSubElement.setAttributeNode(attrVisibility);

                Attr attrID = doc.createAttribute(StaticData.ID_ROOT);
                attrID.setValue(getMethodID());
                artifactSubElement.setAttributeNode(attrID);

                Attr attrReturnType = doc.createAttribute(StaticData.RETURN_TYPE_ROOT);
                attrReturnType.setValue(operation.getReturnType());
                artifactSubElement.setAttributeNode(attrReturnType);

                //newly added variable status for extension module
                Attr attrStatus = doc.createAttribute(StaticData.STATUS);
                String tempStatus = operation.getStatus();
                if(tempStatus == null || tempStatus.equals(""))
                	tempStatus = "";
                attrStatus.setValue(tempStatus);
                artifactSubElement.setAttributeNode(attrStatus);
                
                
                List<Parameter> paramList = operation.getParameterList();
                Attr attrParameters = doc.createAttribute(StaticData.PARAMETER_ROOT);
                String parameterString = "";
                String tempString = operation.getParamString();
                if(tempString.equals("")){
                    tempString = new String();
	                for (int j = 0; paramList != null && j < paramList.size(); j++) {
	                    Parameter param = paramList.get(j);
	                    parameterString += param.getParameterName().concat(":").concat(param.getParameterType());
	
	                    if (j != paramList.size() - 1) {
	                        parameterString += ", ";
	                    }
	                }
                }else{
                	parameterString = tempString;
                }
                attrParameters.setValue(parameterString);
                artifactSubElement.setAttributeNode(attrParameters);
                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void createConnectionElement(Document doc, Element intraConnectionElement) {

        try {

            List<Dependencies> dependenciesesLst = StaticData.depencyList;
            for (int i = 0; dependenciesesLst != null && i < dependenciesesLst.size(); i++) {
                Dependencies dependencies = dependenciesesLst.get(i);
                //putting AretefactSubElement
                Element connectionElement = doc.createElement(StaticData.CONNECTION_ROOT);
                intraConnectionElement.appendChild(connectionElement);//append an artifact main root

                Element typeElement = doc.createElement(StaticData.TYPE_CONNECTION_ROOT);
                typeElement.appendChild(doc.createTextNode(dependencies.getDependency_type()));
                connectionElement.appendChild(typeElement);

                Element startPonintElement = doc.createElement(StaticData.STARTPOINT_ROOT);
                String id = getCurrentDesignId(dependencies.getSource_id());
                startPonintElement.appendChild(doc.createTextNode(id));
                connectionElement.appendChild(startPonintElement);

                Element multiplicitySrcElement = doc.createElement(StaticData.MULTIPLICITY_ROOT);
                multiplicitySrcElement.appendChild(
                        doc.createTextNode(getNomilizedString(dependencies.getMuliplicity_src())));
                connectionElement.appendChild(multiplicitySrcElement);

                Element endPonintElement = doc.createElement(StaticData.ENDPOINT_ROOT);
                id = getCurrentDesignId(dependencies.getTaget_id());
                endPonintElement.appendChild(doc.createTextNode(id));
                connectionElement.appendChild(endPonintElement);

                Element multiplicityTargetElement = doc.createElement(StaticData.MULTIPLICITY_ROOT);
                String textTarget = getNomilizedString(dependencies.getMultiplicity_target());

                multiplicityTargetElement.appendChild(doc.createTextNode(textTarget));
                connectionElement.appendChild(multiplicityTargetElement);

                Element annotationElement = doc.createElement(StaticData.ANNOTATION_ROOT);
                String textAnnotation = getNomilizedString(dependencies.getAnnotation());
                annotationElement.appendChild(doc.createTextNode(textAnnotation));
                connectionElement.appendChild(annotationElement);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getCurrentDesignId(String reference) {

        return keyIDMap.get(reference);
    }

    public String getNomilizedString(String temp) {

        String result = "";

        if (temp == null || temp.isEmpty() || temp.equals("null")
                || temp.equals("data")) {
            return result;
        } else {
            return temp;
        }
    }

    /*  private String getSuitableDrive() {
                List <File>files = Arrays.asList(File.listRoots());
                Map<String,String> driveLetterMap = new HashMap<String,String>();
                  for (File f1 : files) {
                    String s1 = FileSystemView.getFileSystemView().getSystemDisplayName (f1);
                    String s2 = FileSystemView.getFileSystemView().getSystemTypeDescription(f1);
                    System.out.println("getSystemDisplayName : " + s1);
                    System.out.println("getSystemTypeDescription : " + s2);
                   
                   if(s2.equals("Local Disk")){
                     //  AccessController.checkPermission(new FilePermission("/tmp", "read,write"));
                       String driveLetter = s1.split(" ")[2].substring(1,3);
                       File f = new File(driveLetter.concat("\\\\"));
                        if(f.canWrite()) {
                          // write access
                            return driveLetter; 
                        }
                   }
                  }
                 return "D:\\";//default
    }*/
}
