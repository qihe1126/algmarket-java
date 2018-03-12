package com.algmarket.model;

import java.util.LinkedList;
import java.util.List;


public class ModelAclType {
    private final List<String> aclStrings;
    public ModelAclType(List<String> aclStrings) {
        this.aclStrings = aclStrings;
    }

    public static ModelAclType fromAclStrings(List<String> aclStrings) {
        if (aclStrings == null) {
            return null;
        } else if (aclStrings.size() == 0) {
            return PRIVATE;
        } else {
            String aclString = aclStrings.get(0);
            if (aclString.equals(PUBLIC_PERMISSIONS)) {
                return PUBLIC;
            } else if (aclString.equals(MY_ALGMS_PERMISSIONS)) {
                return MY_ALGMS;
            }
        }
        return null;
    }

    private static List<String> createList(String aclString) {
        List<String> aclList = new LinkedList<String>();
        aclList.add(aclString);
        return aclList;
    }

    public List<String> getAclStrings() {
        return aclStrings;
    }

    public static final String PUBLIC_PERMISSIONS = "user://*";
    public static final String MY_ALGMS_PERMISSIONS = "algm://.my/*";

    public static ModelAclType PRIVATE = new ModelAclType(new LinkedList<String>());
    public static ModelAclType PUBLIC = new ModelAclType(createList(PUBLIC_PERMISSIONS));
    public static ModelAclType MY_ALGMS = new ModelAclType(createList(MY_ALGMS_PERMISSIONS));
}
