package main.java.com.roche.documentum;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class PermissionSet {

    private static final Logger logger = Logger.getLogger(PermissionSet.class);
    private String aclName;
    private IDfSession session;

    public PermissionSet(String aclName, IDfSession session) {

        this.session = session;
        this.aclName = aclName;
    }

    public PermissionSet() {
    }


    public void assignPermissionSetToFiles(ArrayList<String> files) throws DfException {

        IDfQuery query = new DfQuery();
        String dql;
        String aclOwner = findOwnerNameForAcl(session);

        for (String file : files
        ) {
            dql = "UPDATE dm_document OBJECTS SET acl_domain = '" + aclOwner + "' SET acl_name = '" + this.aclName + "'" + "WHERE r_object_id = '" + file + "';";
            query.setDQL(dql);
            query.execute(session, IDfQuery.EXEC_QUERY);

            logger.debug("Executing DQL query: " + dql);
            logger.info("Successfully assigned acl: " + this.aclName + " to the file: " + file);
        }
    }

    private String findOwnerNameForAcl(IDfSession session) throws DfException {
        String ownerDqlQuery = "SELECT owner_name FROM dm_acl WHERE object_name = '" + this.aclName + "'";
        String ownerName = "";

        IDfQuery query = new DfQuery();
        query.setDQL(ownerDqlQuery);

        IDfCollection collection = query.execute(session, IDfQuery.DF_READ_QUERY);

        if (collection.next()) {
            ownerName = collection.getString("owner_name");
        }
        collection.close();

        logger.info("Owner for acl: " + this.aclName + " is: " + ownerName);
        return ownerName;
    }


    public void readLines() {

        String fileName = "C:/sources//hello-dfc//workspace/permission_set_source/source.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
