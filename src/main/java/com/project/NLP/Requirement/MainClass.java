/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *..
 */
package com.project.NLP.Requirement;

import com.project.NLP.GUI.ArtefactFrameTestGUI;
import com.project.NLP.UMLToXML.xmlwriter.WriteToXML;
import com.project.traceability.model.Attribute;
import com.project.traceability.model.Dependencies;
import com.project.traceability.model.ModelData;
import com.project.traceability.model.Operation;
import com.project.traceability.model.Parameter;
import com.project.traceability.staticdata.StaticData;
import edu.stanford.nlp.trees.Tree;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MainClass {

    private static String requirementDocument = "";
    private static HashMap requirementObjects = new HashMap();
    private static HashSet<ClassRelation> requirementObjectRelations = new HashSet<>();

    public static void main(String args[]) {

        HashSet classList = new HashSet();
        HashSet attrList = new HashSet();
        HashSet methodList = new HashSet();
        HashSet relationList = new HashSet();
        StoringArtefacts storingArtefacts;
        String className = null;
        ArrayList trees = new ArrayList();
        HashMap<String, HashSet> multiClassWithAttribute;
        HashMap<String, HashSet> multiClassWithMethod;
        MultipleClassListHandlingDemo multipleClassListHandlingDemo;
        HashSet attributeMulti = new HashSet();
        HashSet methodMulti = new HashSet();
        boolean passiveCheck = false;
        HashMap passiveMap;
        HashSet tempList;
        HashMap classWithAttr;
        try {
            /*Reading requirement file */
            requirementDocument = readFromTextFile("io/BankRequirement1.txt");

            //System.setProperty("wordnet.database.dir", "/usr/local/WordNet-2.1/dict");
            System.setProperty("wordnet.database.dir", "C://Program Files (x86)/WordNet/2.1/dict");

            if ("".equals(requirementDocument)) {
                System.out.println("Error : There is no input document !!!");
            } else {
                AnaphoraAnalyzer analyzer = new AnaphoraAnalyzer(requirementDocument);
                requirementDocument = analyzer.doPronounResolving();
                ParserTreeGenerator parser = new ParserTreeGenerator(requirementDocument);
                trees = parser.getSentenceParseTree();
                // ParserTreeGenerator p = new ParserTreeGenerator();
                //passiveCheck = parser.isPassiveSentence();
                /*For individual sentence in the requirement Document */
                for (int countTree = 0; countTree < trees.size(); countTree++) {
                    Tree tree = (Tree) trees.get(countTree);
                    System.out.println("Tree: " + tree);
                    /*if sentence is not negative, then allowing the artefact extraction*/
                    NegativeSentenceDetection negativeSentence = new NegativeSentenceDetection(tree);
                    if (!negativeSentence.isNegativeSentence()) {

                        /*noun pharase identification */
                        ClassIdentification np = new ClassIdentification(tree);
                        classList = np.getClasses();
                        classWithAttr = np.getClassWithAttr();

                        ArrayList attributesFromClass = np.getAttributeFromClass();
                        System.out.println("CLASS LIST:" + classList);

                        /*if classList is empty skip the rest of the extraction of artefacts*/
                        if (!classList.isEmpty()) {
                            /*attributes identification */
                            AttributeIdentification attr = new AttributeIdentification(tree, attributesFromClass, classList);
                            attrList = attr.getAttributes();
                            System.out.println("ATTRIBUTE LIST: " + attrList);

                            /*if the sentence is passive swipe the attributes and methods*/
                            //passiveVoiceHandling(parser, classList, attrList);
                            passiveMap = parser.getPassiveSentenceMap();

                            if (passiveMap.containsKey(tree)) {
                                System.out.println("passive sentence detected");
                               // if (!attrList.isEmpty()) {
                                    tempList = classList;
                                    classList = attrList;
                                    attrList = tempList;
                               // }
                            }

                            /* methods identification */
                            MethodIdentifier mId = new MethodIdentifier(tree, classList);
                            methodList = mId.identifyCandidateMethods(tree);

                            /* relations identificaton */
                            //ClassRelationIdentifier crId = new ClassRelationIdentifier(classList, requirementObjects.keySet());
                            //relationList = crId.identifyGenaralizationByComparing(classList, requirementObjects.keySet());
                            //relationList.addAll(crId.identifyGenaralizationByHypernym(classList, requirementObjects.keySet()));
                            if (classList.size() > 1) {

                                /*Storing Class details  */
                                Iterator iterator = classList.iterator();
                                while (iterator.hasNext()) {
                                    className = (String) iterator.next();
                                    className = className.toLowerCase();
                                    if (requirementObjects.containsKey(className)) {
                                        StoringArtefacts storeArt = (StoringArtefacts) requirementObjects.get(className);
                                        storeArt.addAttributes(attrList);
                                        storeArt.addMethods(methodList);
                                        storeArt.addRelationships(relationList);

                                    } else {
                                        /*calling storingArtefacts class store the results inorder to find the class- attri - metho -relation */
                                        storingArtefacts = new StoringArtefacts();
                                        storingArtefacts.addClassName(classList);
                                        storingArtefacts.addAttributes(attrList);
                                        storingArtefacts.addMethods(methodList);
                                        storingArtefacts.addRelationships(relationList);
                                        requirementObjects.put(className, storingArtefacts);
                                    }

                                }

                            } else if (classList.size() == 1) {

                                /*Storing Class details  */
                                Iterator iterator = classList.iterator();
                                if (iterator.hasNext()) {
                                    className = (String) iterator.next();
                                    className = className.toLowerCase();
                                }
                                if (requirementObjects.containsKey(className)) {
                                    StoringArtefacts storeArt = (StoringArtefacts) requirementObjects.get(className);
                                    storeArt.addAttributes(attrList);
                                    storeArt.addMethods(methodList);
                                    storeArt.addRelationships(relationList);

                                } else {
                                    /*calling storingArtefacts class store the results inorder to find the class- attri - metho -relation */
                                    storingArtefacts = new StoringArtefacts();
                                    storingArtefacts.addClassName(classList);
                                    storingArtefacts.addAttributes(attrList);
                                    storingArtefacts.addMethods(methodList);
                                    storingArtefacts.addRelationships(relationList);
                                    requirementObjects.put(className, storingArtefacts);
                                }

                            }
                            /*to handle class with attribute map (noun + noun)*/
                            if (!classWithAttr.isEmpty()) {
                                HashSet classListWithAttr = new HashSet();
                                HashSet attributeList = new HashSet();
                                int sizeOfMap = classWithAttr.size();
                                Iterator classWithAttrIterator = classWithAttr.keySet().iterator();
                                while (classWithAttrIterator.hasNext()) {
                                    String classN = classWithAttrIterator.next().toString();
                                    classListWithAttr.add(classN);
                                    attributeList = (HashSet) classWithAttr.get(classN);
                                    System.out.println("CLASS from classWith attributes :" + classN);
                                    System.out.println("Attributes from classwith attributes: " + attributeList);

                                    if (requirementObjects.containsKey(classN)) {
                                        StoringArtefacts storeArt = (StoringArtefacts) requirementObjects.get(classN);
                                        storeArt.addAttributes(attributeList);

                                    } else {
                                        /*calling storingArtefacts class store the results inorder to find the class- attri - metho -relation */
                                        storingArtefacts = new StoringArtefacts();
                                        storingArtefacts.addClassName(classListWithAttr);
                                        storingArtefacts.addAttributes(attributeList);

                                        requirementObjects.put(classN, storingArtefacts);
                                    }

                                }

                            }
                        }
                    }

                }
                /*After finding all classes in the document identifying relationships betweenm them.
                 *
                 */
                HashSet relationSet = new HashSet();
                ClassRelationIdentifier crid = new ClassRelationIdentifier();
                //relationSet=crid.identifyGeneralizationRelations(requirementObjects.keySet());
                requirementObjectRelations.addAll(crid.identifyGeneralizationRelations(requirementObjects.keySet()));
                //addingRelationsToIdentifiedClasses(relationSet);
                HashSet associationSet = new HashSet();
                for (Object tree : trees) {
                    // associationSet.addAll(crid.identifyAssociation((Tree)tree,requirementObjects.keySet()));
                    //relationSet.addAll(crid.identifyAssociation((Tree)tree,requirementObjects.keySet()));
                    requirementObjectRelations.addAll(crid.identifyAssociation((Tree) tree, requirementObjects.keySet()));
                }
                System.out.println("---Relation set : " + requirementObjectRelations);
                //System.out.println("---Associaton set : "+relationSet);
                // addingRelationsToIdentifiedClasses(associationSet);
                // addingRelationsToIdentifiedClasses(relationSet);

                /*Writing the information extracted from Requirement to text file  */
                if (!requirementObjects.isEmpty()) {
                    /*eliminate the classes which are not having any attributes, methods and relationships*/
                    HashMap requirementObjectsModified = checkArtefactsExist(requirementObjects, requirementObjectRelations);
                    ArtefactFrameTestGUI t = new ArtefactFrameTestGUI(requirementObjectsModified, requirementObjectRelations);

                    while (t.getLock()) {
                        Thread.sleep(100);
                    }

                    //writeOutputToTxtFile(t.getRequirementobjects(), t.getRequirementRelationsObject());
                    WriteRequirementToXML.writeToXMLFile(t.getRequirementobjects(), t.getRequirementRelationsObject());

                }
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();

        }
    }


    /* Reading the input Natural Language Requirement File 
     *Input : text file
     *Output :String of the text file
     */
    private static String readFromTextFile(String file) {
        BufferedReader br = null;
        String req_Document = "";

        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println("Current Line: " + sCurrentLine);

                /*start a new sentence if the sentence contains but */
                if (sCurrentLine.contains("but")) {
                    sCurrentLine = sCurrentLine.replace("but", ". But");
                }
                /*if the sentence is having hyphen, then it is replaced by a space*/
                if (sCurrentLine.contains("-")) {
                    sCurrentLine = sCurrentLine.replace("-", " ");
                }
                /*if the sentence if having underscore, then it is replace by a space*/
                if (sCurrentLine.contains("_")) {
                    //sCurrentLine = sCurrentLine.replace("_", " ");
                }
                /*if the sentence is having 's, then it is replaced by space EX: employee's -> employee*/
                if (sCurrentLine.contains("'s")) {
                    sCurrentLine = sCurrentLine.replace("'s", " ");
                }

                req_Document += " " + sCurrentLine;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return req_Document;
    }

    public static void addingRelationsToIdentifiedClasses(HashSet relations) {
        Iterator iter = relations.iterator();
        while (iter.hasNext()) {
            ClassRelation classRelation = (ClassRelation) iter.next();
            String parent = classRelation.getParentElement();
            String child = classRelation.getChildElement();
            StoringArtefacts storingArtefacts = (StoringArtefacts) requirementObjects.get(parent);
            storingArtefacts.addRelationships(classRelation);
            storingArtefacts = (StoringArtefacts) requirementObjects.get(child);
            storingArtefacts.addRelationships(classRelation);
        }
    }

    public static void writeOutputToTxtFile(HashMap output, HashSet outputRelations) {
        //write to file : "Requirement Output"
        try {

            StringBuffer sbf = new StringBuffer();
            Iterator it = output.keySet().iterator();
            while (it.hasNext()) {

                String className = it.next().toString();
                StoringArtefacts store = (StoringArtefacts) output.get(className);
                HashSet attributes = store.getAttributes();
                HashSet methods = store.getMethods();
                HashSet relations = store.getRelationships();

                sbf.append("\nClass : " + className + "\n");
                sbf.append("\tAttributes : ");
                for (Object attribute : attributes) {
                    sbf.append(attribute.toString() + ",");
                }
                sbf.append("\tMethods : ");
                for (Object method : methods) {
                    sbf.append(method.toString() + ",");
                }
                sbf.append("\tRelations : ");
                Iterator relIterator = outputRelations.iterator();
                while (relIterator.hasNext()) {
                    Object clRelation = relIterator.next();
                    ClassRelation rel = (ClassRelation) clRelation;
                    System.out.println("Type - " + rel.getRelationType() + "-> Parent -" + rel.getParentElement());
                    sbf.append("Type - " + rel.getRelationType() + "-> Parent -" + rel.getParentElement());

                }

            }

            System.out.println(sbf.toString());

            BufferedWriter bwr = new BufferedWriter(new FileWriter("Requirement_Output.txt"));

            //write contents of StringBuffer to a file
            bwr.write(sbf.toString());

            //flush the stream
            bwr.flush();

            //close the stream
            bwr.close();

        } catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
        }
    }

    private static HashMap checkArtefactsExist(HashMap output, HashSet outputRelations) {
        boolean status = false;
        boolean statusRelation = false;
        Iterator outputIterator = output.keySet().iterator();
        while (outputIterator.hasNext()) {
            String className = outputIterator.next().toString();
            StoringArtefacts storeArt = (StoringArtefacts) output.get(className);

            Iterator relIterator = outputRelations.iterator();
            while (relIterator.hasNext()) {

                ClassRelation rel = (ClassRelation) relIterator.next();
                if (rel.getChildElement().equalsIgnoreCase(className) || rel.getParentElement().equalsIgnoreCase(className)) {
                    statusRelation = true;
                    break;
                }
            }

            if (storeArt.getAttributes().isEmpty() && storeArt.getMethods().isEmpty() && !statusRelation) {
                status = true;
                outputIterator.remove();
            }

        }

        return output;
    }
}
