package main.java.com.roche.documentum;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import java.util.ArrayList;

public class PermissionSet {

    private String aclName;
    private IDfSession session;

    public PermissionSet(String aclName, IDfSession session) {

        this.session=session;
        this.aclName = aclName;

    }


    public void assignPermissionSetToFiles(ArrayList<String> files) throws DfException {

        IDfQuery query = new DfQuery();
        String dql;
        String aclOwner = findOwnerNameForAcl(session);

        for (String file : files
        ) {
            dql = "UPDATE dm_document OBJECTS SET acl_domain = '" + aclOwner + "' SET acl_name = '" + this.aclName + "'" + "WHERE r_object_id = '" + file + "';";
            System.out.println("UPDATE " + dql);
            query.setDQL(dql);
            query.execute(session, IDfQuery.EXEC_QUERY);
        }
    }

    private String findOwnerNameForAcl(IDfSession session) throws DfException {
        String ownerDqlQuery = "SELECT owner_name FROM dm_acl WHERE object_name = '" + this.aclName + "'";
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

        return ownerName;
    }

}
