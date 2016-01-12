/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *..
 */
package com.project.NLP.Requirement;

import com.project.NLP.UMLToXML.xmlwriter.WriteToXML;
import com.project.traceability.model.Attribute;
import com.project.traceability.model.Dependencies;
import com.project.traceability.model.ModelData;
import com.project.traceability.model.Operation;
import com.project.traceability.model.Parameter;
import com.project.traceability.staticdata.StaticData;
import edu.stanford.nlp.trees.Tree;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MainClass {
    
    private static String requirementDocument="";
    private static HashMap requirementObjects = new HashMap();
    private static HashSet<ClassRelation> requirementObjectRelations=new HashSet<>();

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
        try {
            /*Reading requirement file */
            requirementDocument = readFromTextFile("OrderRequirement.txt");

            System.setProperty("wordnet.database.dir", "/usr/local/WordNet-2.1/dict");

            if ("".equals(requirementDocument)) {
                System.out.println("Error : There is no input document !!!");
            } else {
                AnaphoraAnalyzer analyzer=new AnaphoraAnalyzer(requirementDocument);
                requirementDocument=analyzer.doPronounResolving();
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
                        ArrayList attributesFromClass = np.getAttributeFromClass();
                        System.out.println("CLASS LIST:" + classList);

                        /*if classList is empty skip the rest of the extraction of artefacts*/
                        if (!classList.isEmpty()) {
                            /*attributes identification */
                            AttributeIdentification attr = new AttributeIdentification(tree, attributesFromClass, classList);
                            attrList = attr.getAttributes();
                            System.out.println("ATTRIBUTE LIST: " + attrList);

                            /*if the sentence is passive swipe the attributes and methods*/
                            // passiveVoiceHandling(parser, classList, attrList);
                            passiveMap = parser.getPassiveSentenceMap();
                            
                            if (passiveMap.containsKey(tree)) {
                                tempList = classList;
                                classList = attrList;
                                attrList = tempList;
                            }

                            /* methods identification */
                            MethodIdentifier mId = new MethodIdentifier(tree, classList);
                            methodList = mId.identifyCandidateMethods(tree);

                            /* relations identificaton */
                            //ClassRelationIdentifier crId = new ClassRelationIdentifier(classList, requirementObjects.keySet());
                            //relationList = crId.identifyGenaralizationByComparing(classList, requirementObjects.keySet());
                            //relationList.addAll(crId.identifyGenaralizationByHypernym(classList, requirementObjects.keySet()));

                            if (classList.size() > 1) {

                                multipleClassListHandlingDemo = new MultipleClassListHandlingDemo(classList, attrList, methodList);
                                multiClassWithAttribute = multipleClassListHandlingDemo.getClassWithAttribute();
                                multiClassWithMethod = multipleClassListHandlingDemo.getClassWithMethod();

                                /*loop to control opening multiple frames*/
                                while (MultipleClassListHandlingGUI.lock) {
                                    Thread.sleep(100);
                                }

                                Iterator classIterator = classList.iterator();
                                for (int countClass = 0; countClass < classList.size(); countClass++) {
                                    attributeMulti = new HashSet();
                                    methodMulti = new HashSet();
                                    if (classIterator.hasNext()) {
                                        String classNameMulti = classIterator.next().toString();
                                        HashSet classListMulti = new HashSet();
                                        if (multiClassWithAttribute.containsKey(classNameMulti)) {
                                            attributeMulti = multiClassWithAttribute.get(classNameMulti);
                                            classListMulti.add(classNameMulti);
                                            System.out.println("--------------------------------------------------------------------------------------attribute :" + attributeMulti);
                                        }
                                        if (multiClassWithMethod.containsKey(classNameMulti)) {
                                            methodMulti = multiClassWithMethod.get(classNameMulti);
                                            classListMulti.add(classNameMulti);
                                            System.out.println("--------------------------------------------------------------------------------------method :" + methodMulti);
                                        }
                                        //storeClassDetails(classNameMulti, attributeMulti, methodMulti, relationList);
                                        if (!attributeMulti.isEmpty() || !methodMulti.isEmpty()) {
                                            if (requirementObjects.containsKey(classNameMulti)) {
                                                StoringArtefacts storeArt=(StoringArtefacts) requirementObjects.get(classNameMulti);
                                                storeArt.addAttributes(attributeMulti);
                                                storeArt.addMethods(methodMulti);
                                                storeArt.addRelationships(relationList);
                                            }
                                            else {
                                                /*calling storingArtefacts class store the results inorder to find the class- attri - metho -relation */
                                                storingArtefacts = new StoringArtefacts();
                                                storingArtefacts.addClassName(classListMulti);
                                                storingArtefacts.addAttributes(attributeMulti);
                                                storingArtefacts.addMethods(methodMulti);
                                                storingArtefacts.addRelationships(relationList);
                                                requirementObjects.put(classNameMulti, storingArtefacts);
                                                //System.out.println("cl :"+ classNameMulti+"\nAttr :"+ attributes +"\nMethod :"+methods);

                                            }
                                        }
                                    }

                                }
                            } else if (classList.size() == 1) {

                                /*Storing Class details  */
                                Iterator iterator = classList.iterator();
                                if(iterator.hasNext()){
                                    className=(String) iterator.next();
                                    className=className.toLowerCase();
                                }
                                if(requirementObjects.containsKey(className)){
                                    StoringArtefacts storeArt=(StoringArtefacts) requirementObjects.get(className);
                                    storeArt.addAttributes(attrList);
                                    storeArt.addMethods(methodList);
                                    storeArt.addRelationships(relationList);

                                }
                                else{
                                    /*calling storingArtefacts class store the results inorder to find the class- attri - metho -relation */
                                    storingArtefacts = new StoringArtefacts();
                                    storingArtefacts.addClassName(classList);
                                    storingArtefacts.addAttributes(attrList);
                                    storingArtefacts.addMethods(methodList);
                                    storingArtefacts.addRelationships(relationList);
                                    requirementObjects.put(className,storingArtefacts);
                                }

                            }
                        }
                    }

                }
                /*After finding all classes in the document identifying relationships betweenm them.
                *
                */
                HashSet relationSet=new HashSet();
                ClassRelationIdentifier crid=new ClassRelationIdentifier();
                //relationSet=crid.identifyGeneralizationRelations(requirementObjects.keySet());
                requirementObjectRelations.addAll(crid.identifyGeneralizationRelations(requirementObjects.keySet()));
                //addingRelationsToIdentifiedClasses(relationSet);
                HashSet associationSet=new HashSet();
                 for(Object tree:trees){
                   // associationSet.addAll(crid.identifyAssociation((Tree)tree,requirementObjects.keySet()));
                   //relationSet.addAll(crid.identifyAssociation((Tree)tree,requirementObjects.keySet()));
                    requirementObjectRelations.addAll(crid.identifyAssociation((Tree)tree,requirementObjects.keySet()));
                 }
                 System.out.println("---Relation set : "+requirementObjectRelations);
                 //System.out.println("---Associaton set : "+relationSet);
                // addingRelationsToIdentifiedClasses(associationSet);
               // addingRelationsToIdentifiedClasses(relationSet);
                
                /*Writing the information extracted from Requirement to text file  */
                if (!requirementObjects.isEmpty()) {

                    //WriteRequirementToXML.writeToXMLFile(requirementObjects);
                    WriteRequirementToXML.writeToXMLFile(requirementObjects,requirementObjectRelations);

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

    public static void addingRelationsToIdentifiedClasses(HashSet relations){
            Iterator iter=relations.iterator();
            while(iter.hasNext()){
                ClassRelation classRelation=(ClassRelation)iter.next();
                String parent=classRelation.getParentElement();
                String child=classRelation.getChildElement();
                StoringArtefacts storingArtefacts=(StoringArtefacts)requirementObjects.get(parent);
                storingArtefacts.addRelationships(classRelation);
                storingArtefacts=(StoringArtefacts)requirementObjects.get(child);
                storingArtefacts.addRelationships(classRelation);
            }
        }
}
