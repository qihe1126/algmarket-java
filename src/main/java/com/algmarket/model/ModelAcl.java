package com.algmarket.model;

import java.util.List;
import java.util.Map;

public class ModelAcl {
    protected List<String> read;

    public ModelAcl(ModelAclType readAcl) {
        this.read = readAcl.getAclStrings();
    }

    public ModelAclType getReadPermissions() {
        return ModelAclType.fromAclStrings(read);
    }

    public static ModelAcl fromAclResponse(Map<String, List<String>> aclResponse) {
        if (aclResponse == null) {
            return null;
        }
        return new ModelAcl(ModelAclType.fromAclStrings(aclResponse.get("read")));
    }

    public static final ModelAcl PUBLIC = new ModelAcl(ModelAclType.PUBLIC);
    public static final ModelAcl PRIVATE = new ModelAcl(ModelAclType.PRIVATE);
    public static final ModelAcl MY_ALGOS = new ModelAcl(ModelAclType.MY_ALGMS);
}
