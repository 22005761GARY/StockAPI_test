package com.example.stockAPI.model.entity;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Symbol {
    @XmlAttribute
    String id;
    @XmlAttribute(name = "dealprice")
    String dealPrice;
    @XmlAttribute(name = "shortname")
    String shortName;
    @XmlAttribute(name = "mtype")
    String mType;
}
