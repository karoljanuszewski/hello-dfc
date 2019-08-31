package main.java.com.roche.documentum;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfAttr;
import com.documentum.fc.common.IDfLoginInfo;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class PermissionSet {

    private String aclName;

    public PermissionSet(String aclName) {

        this.aclName = aclName;

    }


    public void assignPermissionSetToFiles(String files, IDfSession session) throws DfException {

        String ownerDqlQuery = "SELECT owner_name FROM dm_acl WHERE object_name = '" + this.aclName+"'";
        IDfQuery query = new DfQuery();
        query.setDQL(ownerDqlQuery);
        String ownerName = "";
        IDfCollection collection = query.execute(session, IDfQuery.DF_READ_QUERY);

        if (collection.next()) {
            ownerName = collection.getString("owner_name");
        }
        collection.close();


        System.out.println("ownerDqlQuery " + ownerDqlQuery);
        System.out.println("ownerName " + ownerName);

    }

    public String getAclName() {
        return aclName;
    }
}
