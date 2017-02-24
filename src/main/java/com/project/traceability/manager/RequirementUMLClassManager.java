package com.project.traceability.manager;

import com.project.NLP.SourceCodeToXML.WriteToXML;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.project.NLP.file.operations.FilePropertyName;
import com.project.traceability.GUI.CompareWindow;
import com.project.traceability.GUI.HomeGUI;
import static com.project.traceability.manager.RequirementSourceClassManager.relationNodes;
import com.project.traceability.model.ArtefactElement;
import com.project.traceability.model.ArtefactSubElement;
import com.project.traceability.model.WordsMap;
import com.project.traceability.ontology.models.MatchWords;
import com.project.traceability.ontology.models.ModelCreator;
import com.project.traceability.ontology.models.StaticData;
import com.project.traceability.semanticAnalysis.SynonymWords;
import com.project.traceability.utils.Constants.ImageType;

public class RequirementUMLClassManager {

    static List<String> umlClasses = new ArrayList<String>();
    static List<String> requirementClasses = new ArrayList<String>();
    public static List<String> relationNodes = new ArrayList<String>();

    static String projectPath;
    static TableItem tableItem;
    static TreeItem classItem;

    static Image exactImage = FilePropertyName.exactimg;
    static Image violateImage = FilePropertyName.violoationimg;//new Image(CompareWindow.display, FilePropertyName.IMAGE_PATH+ "violation.jpg");

    /**
     * check whether the requirement classes are implemented in UML
     *
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static List<String> compareClassNames(String projectPath) {
        relationNodes = new ArrayList<String>();
        relationNodes.clear();
        RequirementsManger.readXML(projectPath);
        requirementClasses = ClassManager.getReqClassName(projectPath);
        Map<String, ArtefactElement> reqMap = RequirementsManger.requirementArtefactElements;
        Iterator<Entry<String, ArtefactElement>> requirementIterator = reqMap
                .entrySet().iterator();
        UMLArtefactManager.readXML(projectPath);
        Map<String, ArtefactElement> UMLMap = UMLArtefactManager.UMLAretefactElements;
        Iterator<Entry<String, ArtefactElement>> umlIterator = null;

        if (CompareWindow.tree != null && HomeGUI.isComaparing) {
            TreeColumn column1 = new TreeColumn(CompareWindow.tree, SWT.LEFT);
            column1.setText("RequirementsXML File");
            column1.setWidth(300);

            TreeColumn column2 = new TreeColumn(CompareWindow.tree, SWT.LEFT);
            column2.setText("UML-XML file");
            column2.setWidth(300);
        }
        StaticData.isStartedJustNow = true;
        while (requirementIterator.hasNext()) {
            Map.Entry pairs = requirementIterator.next();
            ArtefactElement reqArtefactElement = (ArtefactElement) pairs
                    .getValue();
            String name = reqArtefactElement.getName();
            List<ArtefactSubElement> reqAttributeElements = reqArtefactElement
                    .getArtefactSubElements();
            if (reqArtefactElement.getType().equalsIgnoreCase("Class")) {
                umlIterator = UMLMap.entrySet().iterator();

                while (umlIterator.hasNext()) {

                    Map.Entry pairs1 = umlIterator.next();
                    ArtefactElement UMLArtefactElement = (ArtefactElement) pairs1
                            .getValue();
                    WordsMap w1 = new WordsMap();
                    String umlName = UMLArtefactElement.getName();
                    String requirementName = name;
                    w1 = SynonymWords.checkSymilarity(UMLArtefactElement.getName(), name,
                            reqArtefactElement.getType());
                    boolean isMatched = w1.isIsMatched();
                    if (!isMatched) {
                        //wordNet dictionary does not have any matching word
                        //call our dictionary model.owl 
                        ModelCreator model = ModelCreator.getModelInstance();
                        model.setPath("");
                        isMatched = model.isMatchingWords(requirementName, umlName);
                        StaticData.isStartedJustNow = false;
                        if (!isMatched) {
                            //if it is not match by our dictionary 
                            //call the check similarity algorithm or edit distance
                            //based on edit distance we find out the similarity
                            isMatched = MatchWords.compareStrings(requirementName, umlName);
                        }
                    }
                    if (UMLArtefactElement.getType().equalsIgnoreCase("Class")
                            && (UMLArtefactElement.getName().equalsIgnoreCase(
                                    name) | isMatched)) {
                        System.out.println("Compared for Reuqirement and UML " + "RequirementClass:-"
                                + requirementName + "UMLClass:->" + umlName);
                        compareSubElements(classItem, reqArtefactElement, UMLArtefactElement);
                        UMLMap.remove(UMLArtefactElement.getArtefactElementId());
                        reqMap.remove(reqArtefactElement.getArtefactElementId());
                        requirementIterator = reqMap.entrySet().iterator();
                        break;
                    }

                }
            }
        }
        RelationManager.addLinks(relationNodes);

        if (UMLMap.size() > 0 || reqMap.size() > 0) {
            requirementIterator = reqMap.entrySet().iterator();
            umlIterator = UMLMap.entrySet().iterator();

            while (requirementIterator.hasNext()) {
                Map.Entry<String, ArtefactElement> artefact = requirementIterator
                        .next();
                if (CompareWindow.tree != null
                        && !CompareWindow.shell.isDisposed() && HomeGUI.isComaparing) {
                    TreeItem item = new TreeItem(CompareWindow.tree, SWT.NONE);
                    item.setForeground(Display.getDefault().getSystemColor(
                            SWT.COLOR_RED));
                    item.setText(0, artefact.getValue().getName());
                    item.setData("0", artefact.getValue());
                    item.setImage(0, violateImage);
                    addSubItems(0, item, artefact.getValue().getArtefactSubElements());
                }
            }

            while (umlIterator.hasNext()) {
                Map.Entry<String, ArtefactElement> artefact = umlIterator
                        .next();
                if (CompareWindow.tree != null
                        && !CompareWindow.shell.isDisposed() && HomeGUI.isComaparing) {
                    TreeItem item = new TreeItem(CompareWindow.tree, SWT.NONE);
                    item.setForeground(Display.getDefault().getSystemColor(
                            SWT.COLOR_RED));
                    item.setText(1, artefact.getValue().getName());
                    item.setData("1", artefact.getValue());
                    item.setImage(1, violateImage);
                    addSubItems(1, item, artefact.getValue().getArtefactSubElements());
                }
            }
        }
        return relationNodes;
    }

    @SuppressWarnings("rawtypes")
    public static int compareClassCount() {
        SourceCodeArtefactManager.readXML(projectPath);
        RequirementsManger.readXML(projectPath);
        Iterator it = UMLArtefactManager.UMLAretefactElements.entrySet()
                .iterator();
        int countUMLClass = 0;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            ArtefactElement artefactElement = (ArtefactElement) pairs
                    .getValue();
            if (artefactElement.getType().equalsIgnoreCase("Class")) {

                countUMLClass++;
            }
            List<ArtefactSubElement> artefactSubElements = artefactElement
                    .getArtefactSubElements();
            it.remove(); // avoids a ConcurrentModificationException
        }
        // UMLArtefactManager.readXML();
        Iterator it1 = RequirementsManger.requirementArtefactElements
                .entrySet().iterator();
        int countReqClass = 0;
        while (it1.hasNext()) {
            Map.Entry pairs = (Entry) it1.next();
            ArtefactElement artefactElement = (ArtefactElement) pairs
                    .getValue();
            if (artefactElement.getType().equalsIgnoreCase("Class")) {
                countReqClass++;
            }
            List<ArtefactSubElement> artefactSubElements = artefactElement
                    .getArtefactSubElements();
            it1.remove(); // avoids a ConcurrentModificationException
        }

        if (countUMLClass == countReqClass) {
            System.out.println("class compared");
        }
        return countUMLClass;
    }

    public static void addSubItems(int column, TreeItem item, List<ArtefactSubElement> list) {
        for (int i = 0; i < list.size(); i++) {
            TreeItem subItem = new TreeItem(item, SWT.NONE);
            subItem.setText(column, list.get(i).getName());
            subItem.setData("" + column + "", list.get(i));
        }
    }

    public static void compareSubElements(TreeItem classItem,
            ArtefactElement reqArtefactElement,
            ArtefactElement UMLArtefactElement) {

        relationNodes.add(reqArtefactElement
                .getArtefactElementId());
        relationNodes.add("Req Class To UML Class");
        relationNodes.add(UMLArtefactElement
                .getArtefactElementId());
        if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
            relationNodes.add(com.project.traceability.staticdata.StaticData.DEFAULT_STATUS);
        }
        if (CompareWindow.tree != null
                && !CompareWindow.tree.isDisposed() && HomeGUI.isComaparing) {
            classItem = new TreeItem(CompareWindow.tree, SWT.NONE);
            classItem.setText(0, reqArtefactElement.getName());
            classItem.setData("0", reqArtefactElement);
            classItem.setImage(exactImage);
            classItem.setForeground(Display.getDefault()
                    .getSystemColor(SWT.COLOR_DARK_BLUE));
            classItem.setText(1, UMLArtefactElement.getName());
            classItem.setData("1", UMLArtefactElement);

        }
        ArrayList<ArtefactSubElement> UMLAttributesList = new ArrayList<ArtefactSubElement>();
        ArrayList<ArtefactSubElement> UMLMethodsList = new ArrayList<ArtefactSubElement>();

        ArrayList<ArtefactSubElement> reqAttributesList = new ArrayList<ArtefactSubElement>();
        ArrayList<ArtefactSubElement> reqMethodsList = new ArrayList<ArtefactSubElement>();

        ArrayList<WordsMap> methodWordsMapList = new ArrayList<WordsMap>();
        ArrayList<WordsMap> attributeWordsMapList = new ArrayList<WordsMap>();

        List<ArtefactSubElement> UMLAttributeElements = UMLArtefactElement
                .getArtefactSubElements();
        List<ArtefactSubElement> reqAttributeElements = reqArtefactElement
                .getArtefactSubElements();
        for (int i = 0; i < UMLAttributeElements.size(); i++) {
            ArtefactSubElement UMLSubElement = UMLAttributeElements
                    .get(i);
            for (int j = 0; j < reqAttributeElements.size(); j++) {
                ArtefactSubElement reqSubElement = reqAttributeElements
                        .get(j);
                WordsMap w2 = new WordsMap();
                String requirementArtName = reqSubElement.getName();
                String umlArtName = UMLSubElement.getName();

                w2 = SynonymWords.checkSymilarity(
                        UMLSubElement.getName(),
                        reqSubElement.getName(),
                        reqSubElement.getType(), UMLSubElement.getType(), requirementClasses);
                boolean isMatched = w2.isIsMatched();
                if (!isMatched) {
                    //wordNet dictionary does not have any matching word
                    //call our dictionary model.owl 
                    ModelCreator model = ModelCreator.getModelInstance();
                    isMatched = model.isMatchingWords(requirementArtName, umlArtName);
                    if (!isMatched) {
                        //if it is not match by our dictionary 
                        //call the check similarity algorithm or edit distance
                        //based on edit distance we find out the similarity
                        isMatched = MatchWords.compareStrings(requirementArtName, umlArtName);
                        if (isMatched) {
                            w2.setMapID(1000);
                        }
                    }
                    if (isMatched) {
                        w2.setMapID(1000);
                    }
                }
                if (UMLSubElement.getName().equalsIgnoreCase(
                        reqSubElement.getName())
                        | isMatched) {
                    /*relationNodes.add(reqSubElement
                            .getSubElementId().substring(
                                    reqSubElement
                                    .getSubElementId()
                                    .indexOf("RQ")));*/
                    relationNodes.add(reqSubElement
                            .getSubElementId());
                    relationNodes.add("Req " + reqSubElement.getType() + " To " + UMLSubElement.getType());
                    relationNodes.add(UMLSubElement
                            .getSubElementId());
                    if (WriteToXML.isTragging.equalsIgnoreCase("Tragging")) {
                        relationNodes.add(com.project.traceability.staticdata.StaticData.DEFAULT_STATUS);
                    }

                    // if(UMLAttribute.getName().equalsIgnoreCase(reqElement.getName())
                    // ||LevenshteinDistance.similarity(UMLAttribute.getName(),
                    // reqElement.getName())>.6){
                    if (CompareWindow.tree != null
                            && !CompareWindow.tree.isDisposed() && HomeGUI.isComaparing) {

                        if ((reqSubElement.getType())
                                .equalsIgnoreCase("Field")) {
                            UMLAttributesList.add(UMLSubElement);
                            reqAttributesList.add(reqSubElement);
                            attributeWordsMapList.add(w2);
                        } else if ((reqSubElement.getType())
                                .equalsIgnoreCase("Method")) {
                            UMLMethodsList.add(UMLSubElement);
                            reqMethodsList.add(reqSubElement);
                            methodWordsMapList.add(w2);
                        }

                        UMLAttributeElements
                                .remove(UMLSubElement);
                        reqAttributeElements.remove(reqSubElement);
                        i--;
                        j--;
                        break;
                    }
                }

            }
        }
        if ((CompareWindow.tree != null
                && !CompareWindow.tree.isDisposed()) && HomeGUI.isComaparing) {
            TreeItem subAttribute = new TreeItem(classItem, SWT.NONE);
            subAttribute.setText("Attributes");
            subAttribute.setForeground(Display.getDefault()
                    .getSystemColor(SWT.COLOR_GREEN));
            for (int k = 0; k < UMLAttributesList.size(); k++) {
                TreeItem subItem = new TreeItem(subAttribute, SWT.NONE);
                subItem.setText(1, UMLAttributesList.get(k).getName());
                subItem.setData("1", UMLAttributesList.get(k));
                subItem.setImage(1, ImageType.getImage(attributeWordsMapList.get(k)).getValue());
                subItem.setText(0, reqAttributesList.get(k).getName());
                subItem.setData("0", reqAttributesList.get(k));
                subItem.setImage(0, ImageType.getImage(attributeWordsMapList.get(k)).getValue());
            }

            TreeItem subMethod = new TreeItem(classItem, SWT.NONE);
            subMethod.setText("Methods");
            subMethod.setForeground(Display.getDefault()
                    .getSystemColor(SWT.COLOR_GREEN));
            for (int k = 0; k < UMLMethodsList.size(); k++) {
                TreeItem subItem = new TreeItem(subMethod, SWT.NONE);
                subItem.setText(1, UMLMethodsList.get(k).getName());
                subItem.setData("1", UMLMethodsList.get(k));
                subItem.setImage(1, ImageType.getImage(methodWordsMapList.get(k)).getValue());
                subItem.setText(0, reqMethodsList.get(k).getName());
                subItem.setData("0", reqMethodsList.get(k));
                subItem.setImage(0, ImageType.getImage(methodWordsMapList.get(k)).getValue());
            }
            if (reqAttributeElements.size() > 0) {
                for (ArtefactSubElement model : reqAttributeElements) {
                    if (model.getType().equalsIgnoreCase("Field")) {
                        TreeItem subItem = new TreeItem(subAttribute,
                                SWT.NONE);
                        subItem.setText(0, model.getName());
                        subItem.setData("0", model);
                        subItem.setImage(0, violateImage);
                        subItem.setForeground(Display
                                .getDefault().getSystemColor(
                                        SWT.COLOR_RED));
                    } else if (model.getType().equalsIgnoreCase("Method")) {
                        TreeItem subItem = new TreeItem(subMethod,
                                SWT.NONE);
                        subItem.setText(0, model.getName());
                        subItem.setData("0", model);
                        subItem.setImage(0, violateImage);
                        subItem.setForeground(Display
                                .getDefault().getSystemColor(
                                        SWT.COLOR_RED));
                    }
                }
            }
            if (UMLAttributeElements.size() > 0) {
                for (ArtefactSubElement model : UMLAttributeElements) {
                    if (model.getType().equalsIgnoreCase("UMLAttribute")) {
                        TreeItem subItem = new TreeItem(subAttribute,
                                SWT.NONE);
                        subItem.setText(1, model.getName());
                        subItem.setImage(1, violateImage);
                        subItem.setData("1", model);
                        subItem.setForeground(Display
                                .getDefault().getSystemColor(
                                        SWT.COLOR_RED));
                    } else if (model.getType().equalsIgnoreCase("UMLOperation")) {
                        TreeItem subItem = new TreeItem(subMethod,
                                SWT.NONE);
                        subItem.setText(1, model.getName());
                        subItem.setImage(1, violateImage);
                        subItem.setData("1", model);
                        subItem.setForeground(Display
                                .getDefault().getSystemColor(
                                        SWT.COLOR_RED));
                    }
                }

            }
        }
    }

}
