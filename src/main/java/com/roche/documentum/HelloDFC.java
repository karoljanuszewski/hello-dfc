package main.java.com.roche.documentum;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfLoginInfo;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class HelloDFC {
    private static final Logger logger = Logger.getLogger(HelloDFC.class);
    private static IDfSession session = null;
    private static IDfSessionManager sessionManager = null;
    private static Properties appProperties = null;
    private static ExtensionMapper extensionMapper;
    private static ArrayList<String> rObjectIds;

    public static void main(String[] args) throws IOException, DfException {
        connect();
        readAppProperties();


        switch (appProperties.getProperty("application.mode")) {
            case "ASSIGN_PERMISSION_SET": {
                String aclName = appProperties.getProperty("permission.set.name");
                PermissionSet acl = new PermissionSet(aclName, session, appProperties);
                acl.assignPermissionSetToFiles(getRObjectIds());

                break;

            }

            case "EXPORT_DOCUMENT": {
                contentExport(getRObjectIds(), appProperties.getProperty("export.file.location"));
                extensionMapper = new ExtensionMapper(session);

                break;


            }

            case "EXPORT_PROPERTIES": {
                String exportFileLocation = appProperties.getProperty("export.file.location");
                String exportPropertiesFileName = appProperties.getProperty("export.properties.file.name");

                DocumentProperty documentProperty = new DocumentProperty(exportFileLocation, exportPropertiesFileName, getRObjectIds(), session);
                documentProperty.createWorkbook();

                break;

            }
            case "CREATE_PERMISSION_SET": {
                PermissionSet acl = new PermissionSet(appProperties);
                acl.readLines();

                break;
            }

            default: {
                logger.error("application.mode: " + appProperties.getProperty("application.mode") + " not found in app.properties file");
            }
        }


        disconnect();
    }


    private static ArrayList<String> getRObjectIds() throws FileNotFoundException {
        rObjectIds = new ArrayList<>();
        String fileName = appProperties.getProperty("rObjectId.list.file.location");

        InputStream inputStream = new FileInputStream(fileName);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                rObjectIds.add(line);
                logger.info("Reading r_object_id value: " + line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
            logger.info("Buffered Reader close()");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Returning a list of r_object_id 's: " + rObjectIds);
        return rObjectIds;
    }

    private static void contentExport(ArrayList<String> rObjectIds, String exportPath) {
        IDfSysObject sysObject;
        ByteArrayInputStream inputStream;
        String objectName = null;
        String contentType;
        OutputStream outputStream;
        extensionMapper = new ExtensionMapper(session);

        for (String rObject : rObjectIds
        ) {
            try {
                sysObject = (IDfSysObject) session.getObject(new DfId(rObject));
                contentType = sysObject.getContentType();
                objectName = sysObject.getObjectName();

                inputStream = sysObject.getContent();
                outputStream = new FileOutputStream(exportPath + objectName + "." + extensionMapper.getExtension(contentType));

                outputStream.write(new byte[1024], 0, 1024);
                inputStream.close();
                outputStream.close();

                String fullFileName = exportPath + objectName + "." + extensionMapper.getExtension(contentType);
                logger.info("Successfully saved file: " + fullFileName);
            } catch (DfException | IOException e) {
                e.printStackTrace();
                logger.error("Saving a file : " + objectName + "unsuccessful");
            }
        }
    }


    private static void connect() { //new class autoClosable
        IDfClientX clientx = new DfClientX();
        IDfClient client;
        sessionManager = null;

        try {

            Properties connectionProperties = readProperties();

            String username = connectionProperties.getProperty("login.username");
            String password = connectionProperties.getProperty("login.password");

            IDfLoginInfo loginInfo = clientx.getLoginInfo();
            loginInfo.setUser(username);
            loginInfo.setPassword(password);

            String repository = connectionProperties.getProperty("login.repository");

            client = clientx.getLocalClient();

            sessionManager = client.newSessionManager();
            sessionManager.setIdentity(repository, loginInfo);

            session = sessionManager.getSession(repository);

            logger.info("Successfully logged to: " + repository + " as: " + username);
        } catch (Exception ex) {
            logger.error("Unsuccessful logging");
            ex.printStackTrace();
        }
    }


    private static Properties readProperties() throws IOException {
        Properties loginProperties = new Properties();

        try (InputStream inputStream = HelloDFC.class.getClassLoader().getResourceAsStream("main/resources/config.properties")) {
            loginProperties.load(inputStream);
            logger.info("Successfully reading config.properties file");
        }

        return loginProperties;
    }


    private static void readAppProperties() throws IOException {
        appProperties = new Properties();

        try (InputStream inputStream = HelloDFC.class.getClassLoader().getResourceAsStream("main/resources/app.properties")) {
            appProperties.load(inputStream);
            logger.info("Successfully reading app.config file");
        }
    }


    public static void disconnect() {
        logger.info("Releasing session");
        sessionManager.release(session);
    }


}
