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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HelloDFC {
    public static IDfSession session = null;
    public static IDfSessionManager sessionManager = null;

    public static void main(String[] args) {
        connect();
        try {
            typeDemo("dm_acl");
            //typeDemo("dm_document");
            //typeAttributesDemo("dm_acl");
            //typeDumpDemo("dm_acl");
            //selectDocumentsForStudyDemo("ML00780");
            //documentModificationDemo("090f42df8025afbc");
            //apiDemo();
        } catch (DfException ex) {
            Logger.getLogger(HelloDFC.class.getName()).log(Level.INFO, null, ex);
        }

        disconnect();
    }

    public static void typeDemo(String typeName) throws DfException {
        IDfType type = session.getType(typeName);
        System.out.println("Info about " + typeName + " type:");
        System.out.println("Name: " + type.getName());
        System.out.println("Description: " + type.getDescription());
        System.out.println("Super Name: " + type.getSuperName());
    }



    public static void connect() {
        IDfClientX clientx = new DfClientX();
        IDfClient client;
        sessionManager = null;

        try {

            Properties properties = readProperties();

            String username = properties.getProperty("login.username");
            String password = properties.getProperty("login.password");

            IDfLoginInfo loginInfo = clientx.getLoginInfo();
            loginInfo.setUser(username);
            loginInfo.setPassword(password);

            String repository = properties.getProperty("login.repository");

            client = clientx.getLocalClient();

            sessionManager = client.newSessionManager();
            sessionManager.setIdentity(repository, loginInfo);

            session = sessionManager.getSession(repository);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    private static Properties readProperties() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = HelloDFC.class.getClassLoader().getResourceAsStream("main/resources/config.properties")) {
            properties.load(inputStream);
        }

        return properties;
    }

    public static void disconnect() {
        sessionManager.release(session);
    }







    private static String readProperty(String propertyKey) {
        String propertyValue = "";
        try (InputStream inputStream = HelloDFC.class.getClassLoader().getResourceAsStream("main/resources/config.properties")) {

            Properties properties = new Properties();
            properties.load(inputStream);


            propertyValue = properties.getProperty(propertyKey);

            System.out.println(propertyValue);


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return propertyValue;
    }

    private static void documentModificationDemo(String objectId) throws DfException {
        IDfSysObject sysObj = (IDfSysObject) session.getObject(new DfId(objectId));
        sysObj.setString("rog_comments", "test sample application");
        sysObj.save();
    }

    public static void apiDemo() throws DfException {
        IDfPersistentObject object = (IDfPersistentObject) session.newObject("dm_acl");
        object.apiSet("set", "owner_name", "etmfdev");
        object.apiSet("set", "object_name", "test_acl2");

        object.apiExec("grant", "dm_owner,1");
        object.apiExec("grant", "etmfdev,7,execute_proc,change_permit");

        object.save();
    }


    public static void typeAttributesDemo(String typeName) throws DfException {
        IDfType type = session.getType(typeName);
        IDfAttr attr;
        for (int i = 0; i < type.getTypeAttrCount(); i++) {
            attr = type.getAttr(i);
            System.out.println("Name: " + attr.getName() + " Length: " + attr.getLength());
        }

    }

    public static void typeDumpDemo(String typeName) throws DfException {
        IDfType type = session.getType(typeName);

        System.out.println("Dump of " + typeName + ": ");
        System.out.println(type.dump());
    }

    private static void selectDocumentsForStudyDemo(String studyNumber) throws DfException {
        String dql = "select * from cd_clinical_tmf_doc where clinical_trial_id = '" + studyNumber + "'";
        IDfQuery query = new DfQuery();
        query.setDQL(dql);
        IDfCollection coll = null;
        coll = query.execute(session, IDfQuery.DF_READ_QUERY);

        while (coll.next()) {
            System.out.println(coll.getString("r_object_id") + " " + coll.getString("object_name") + " " + coll.getString("acl_name"));
        }

        if (coll != null) {
            coll.close();
        }

    }


}
