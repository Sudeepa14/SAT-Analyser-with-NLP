/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.project.NLP.Requirement;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.WordStemmer;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Vinojan
 *
 * Identifying methods related to a particular class.
 */
public class MethodIdentifier {

    ArrayList tree;
    String document;
    Tree[] treeAn;
    Tree sTree;
    HashSet classList;
    //private ArrayList commonVerbs=new ArrayList();
    private HashSet commonVerbs = new HashSet();
    WordStemmer wordStemmer = new WordStemmer();
    private DesignElementClass designEleClass = new DesignElementClass();

    public MethodIdentifier() {
        createCommonVerbs();
    }

    public MethodIdentifier(ArrayList list) {
        this.tree = list;
        createCommonVerbs();
    }

    public MethodIdentifier(String document) {
        this.document = document;
        createCommonVerbs();
    }

    public MethodIdentifier(Tree[] tree, HashSet classList) {
        this.classList = classList;
        this.treeAn = tree;
        createCommonVerbs();

    }
    /*For single Tree */

    public MethodIdentifier(Tree tree, HashSet classList) {
        this.classList = classList;
        this.sTree = tree;
        createCommonVerbs();

    }

    void createCommonVerbs() {
        commonVerbs.add("be");
        commonVerbs.add("have");
    }

    void addVerbToCommonVerbs(String verb) {
        commonVerbs.add(verb);
    }

    void removeVerbFromCommonVerbs(String verb) {
        commonVerbs.remove(verb);
    }

    ArrayList identifyCandidateMethods(String document) {
        ArrayList sentenceTree = new ArrayList();
        ArrayList initialVerbs = new ArrayList();
        ParserTreeGenerator treeGen = new ParserTreeGenerator(document);
        sentenceTree = treeGen.getSentenceParseTree();
        System.out.println("--------Identified verb pharases are:---------");
        initialVerbs = getPhrase(sentenceTree);
        initialVerbs.removeAll(commonVerbs);
        System.out.println("--------Filtered  verb phrases are:---------");
        System.out.println(initialVerbs);

        return null;
    }

    ArrayList identifyCandidateMethods(ArrayList<Tree> sentenceTree) {
        ArrayList intialVerbs = new ArrayList();
        intialVerbs = getPhrase(sentenceTree);

        return null;
    }

    ArrayList getPhrase(ArrayList<Tree> sentenceTree) {
        /*ref : patterns -http://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/trees/tregex/TregexPattern.html  */
        String phraseNotation = "VB|VBN>VP";//@VB>VP" ; //& VBN >VP";//"VP<(VB $++NP)";//"VP:VB";//"@"+"VP"+"! << @"+"VP";
        ArrayList vpList = new ArrayList();
        for (Tree tree : sentenceTree) {
            System.out.print("\n---tree_sen----" + tree + "----\n");
            /* Stemming the sentence */
            wordStemmer.visitTree(tree);
            TregexPattern VBpattern = TregexPattern.compile(phraseNotation);
            TregexMatcher matcher = VBpattern.matcher((Tree) tree);
            while (matcher.findNextMatchingNode()) {
                Tree match = matcher.getMatch();
                String verb = Sentence.listToString(match.yield());

                /* Filter to unique verbs  */
                //List<String> newList = new ArrayList<String>(new HashSet<String>(oldList));
                if (!vpList.contains(verb)) {
                    vpList.add(verb);
                }
                System.out.print("\n---phrase match----" + match + "----\n");

            }
        }
        System.out.print("\n---VPList----" + vpList + "----\n");
        return vpList;
    }

    HashSet identifyCandidateMethods(Tree[] tree) {

        String phraseNotation = "VB|VBN>VP";//@VB>VP" ; //& VBN >VP";//"VP<(VB $++NP)";//"VP:VB";//"@"+"VP"+"! << @"+"VP";
        HashSet vpList = new HashSet();
        for (Tree childTree : tree) {
            System.out.print("\n---tree_sen----" + childTree + "----\n");
            /* Stemming the sentence */
            wordStemmer.visitTree(childTree);
            TregexPattern VBpattern = TregexPattern.compile(phraseNotation);
            TregexMatcher matcher = VBpattern.matcher((Tree) childTree);
            while (matcher.findNextMatchingNode()) {
                Tree match = matcher.getMatch();
                String verb = Sentence.listToString(match.yield());

                /* Filter to unique verbs  */
                //List<String> newList = new ArrayList<String>(new HashSet<String>(oldList));
                //if(!vpList.contains(verb)){
                vpList.add(verb);
                //}
                System.out.print("\n---phrase match----" + match + "----\n");

            }
        }
        vpList.removeAll(commonVerbs);
        System.out.print("\n---VPList----" + vpList + "----\n");
        return vpList;
    }

    /*For single Tree */
    HashSet identifyCandidateMethods(Tree tree) {

        String phraseNotation = "VB|VBN>VP";//@VB>VP" ; //& VBN >VP";//"VP<(VB $++NP)";//"VP:VB";//"@"+"VP"+"! << @"+"VP";
        HashSet vpList = new HashSet();

        /* Stemming the sentence */
        wordStemmer.visitTree(tree);
        TregexPattern VBpattern = TregexPattern.compile(phraseNotation);
        TregexMatcher matcher = VBpattern.matcher(tree);
        while (matcher.findNextMatchingNode()) {
            Tree match = matcher.getMatch();
            String verb = Sentence.listToString(match.yield());

            /* Filter to unique verbs  */
                //List<String> newList = new ArrayList<String>(new HashSet<String>(oldList));
            //if(!vpList.contains(verb)){
            vpList.add(verb);
            //}
            System.out.print("\n---phrase match----" + match + "----\n");

        }

        vpList.removeAll(commonVerbs);
        System.out.print("\n------VPList----" + vpList + "----\n");

        vpList = removeDesignElements(vpList);
        return vpList;
    }

    private HashSet removeDesignElements(HashSet vpList) {
        //HashSet newVPList = new HashSet();

        ArrayList designElements = designEleClass.getDesignElementsList();
        vpList.removeAll(designElements);
        

        return vpList;
    }
    /*
            
     Tree[] children = tree.children() ;
            
     System.out.print("\n---Tree----"+tree+"----\n");
     System.out.print("\n----Children---"+children+"----\n");
            
     for (Tree child: children){
     System.out.print("\n----Child---"+child+"----\n");
     TregexPattern VBpattern = TregexPattern.compile(phraseNotation);
     TregexMatcher matcher = VBpattern.matcher((Tree) child);

     while (matcher.findNextMatchingNode()) {
     Tree match = matcher.getMatch();
     System.out.println("\n--Matching Tree  "+match+"-------\n");
     Tree[] innerChild= match.children();
     for (Tree inChild: innerChild){
     System.out.println("\n--innerChild  "+inChild+"-------\n");
     if (inChild.value().equals("VB")){
     List<Tree> leaves =inChild.getLeaves(); //leaves correspond to the tokens
     for (Tree leaf : leaves){ 
     List<Word> words = leaf.yieldWords();
     for (Word word: words){
     System.out.println("\n----Words------\n");
     System.out.println(String.format("(%s - VB),",word.word()));
     }
                                
     }
     }

     }
     }

     }
     }
     
       
     }
     */

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

}
