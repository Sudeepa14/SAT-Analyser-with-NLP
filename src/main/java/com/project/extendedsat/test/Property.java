package com.project.extendedsat.test;

import org.w3c.dom.Element;

import javax.management.AttributeList;

public class Property {

    private String id;
    private String name;
    private String type;
    private String visibility;
    private String aType;
    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }



    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name=name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id=id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type=type;
    }
    public String getVisibility() {
        return visibility;
    }
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public static AttributeList getAttributes(Element eElement,String type) {


        AttributeList l = new AttributeList();
        //Element element = (Element) node;
        l.add(eElement.getAttribute("name"));
        l.add(eElement.getAttribute("id"));
        l.add(eElement.getAttribute("type"));
        l.add(eElement.getAttribute("visibility"));
        l.add(type);


        return l;
    }
}
