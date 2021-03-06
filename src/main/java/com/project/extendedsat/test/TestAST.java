package com.project.extendedsat.test;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.FileUtils;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import javax.management.AttributeList;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chathura on 8/24/2016.
 */
public class TestAST {
	
	public String TestPath;
	
    public TestAST(String testPath) {
		super();
		TestPath = testPath;
	}


    public  void TestingTraceability(String WorkFolder) throws IOException, TransformerException {
 WorkFolder=WorkFolder+File.separator;
        File mainFolder = new File(TestPath);
        List<File> files = JavaFileFinder.getJavaFiles(mainFolder);

        for (File file : files) {

            try {
                new TestAST(TestPath).printJava8(FileUtils.readFileToString(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Java8BaseListener.getTemp());


        Source xmlInput = new StreamSource(new StringReader("<Artefacts>" + Java8BaseListener.getTemp() + "</Artefacts>"));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //transformerFactory.setAttribute("indent-number", 2);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);

        FileUtils.copyFile(new File("UMLArtefactFile.xml"),new File(WorkFolder+"UMLArtefactFile.xml"));
        FileUtils.copyFile(new File("SourceCodeArtefactFile.xml"),new File(WorkFolder+"SourceCodeArtefactFile.xml"));
        FileUtils.write(new File(WorkFolder+"testIntermediate.xml"), xmlOutput.getWriter().toString());

        ArrayList<AttributeList> list=XmlToArray.parseXML(WorkFolder+"testIntermediate.xml");
        ArrayList<AttributeList> list1=XmlToArray.parseXML(WorkFolder+"UMLArtefactFile.xml");
        ArrayList<AttributeList> list2=XmlToArray.parseXML(WorkFolder+"SourceCodeArtefactFile.xml");
        ArrayList<AttributeList> l1= new ArrayList<AttributeList>();
        ArrayList<AttributeList> l2= new ArrayList<AttributeList>();
        Map<String,ArrayList<AttributeList>> mapList = CreateTraceability.compareList(list, list1,l1,l2);
        Map<String,ArrayList<AttributeList>> mapList1 = CreateTraceability.compareList(list, list2,l1,l2);
//        Map<String,ArrayList<AttributeList>> map3 = new HashMap<>();
//
//        map3.putAll(mapList);
//        map3.putAll(mapList1);
        CreateRelationFile relation = new CreateRelationFile(WorkFolder+"Relations.xml");
        relation.createRelations(mapList1);
        RelationFileToArray r = new RelationFileToArray();
        ArrayList<HashMap<String,String>> relationlist = r.toArray(WorkFolder+"Relations.xml");
        StoreData st = new StoreData();
        st.deleteData();
        st.createdb(list);
        st.createdb(list1);
        st.createdb(list2);
        st.createRelation(relationlist);
        Driver driver = GraphDatabase.driver( "bolt://localhost", AuthTokens.basic( "neo4j", "samitha" ) );
        Session session = driver.session();


        GenerateGexf gexf = new GenerateGexf();
        gexf.generateGraphFile(session);
        //FileUtils.copyFile(new File("UMLArtefactFile.xml"),new File("SourceToTest.gexf"));


//       TestGraph previewJFrame = new TestGraph();
//       previewJFrame.script();
       //SourceToTest
       
       
    }


    private void printJava8(String Java8Sentence) {
        // Get our lexer
        Java8Lexer lexer = new Java8Lexer(new ANTLRInputStream(Java8Sentence));

        // Get a list of matched tokens
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Pass the tokens to the parser
        Java8Parser parser = new Java8Parser(tokens);

        // Specify our entry point
        ParserRuleContext contextz = parser.compilationUnit();

        // Walk it and attach our listener
        ParseTreeWalker walker = new ParseTreeWalker();
        Java8BaseListener listener = new Java8BaseListener();
        walker.walk(listener, contextz);

    }
}
