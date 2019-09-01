package main.java.com.roche.documentum;


import com.documentum.fc.client.*;
import com.documentum.fc.client.acs.IDfAcsClient;
import com.documentum.fc.common.DfException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DocumentProperty {


    public DocumentProperty(String exportFileLocation, String exportPropertiesFileName, ArrayList<String> rObjectId, IDfSession session) {
        IDfQuery query = new DfQuery();
        String dql = "SELECT * FROM dm_document WHERE r_object_id = '" + rObjectId.get(0) + "';";
        System.out.println(dql);
        query.setDQL(dql);
        createJsonFromDqlQuery(session, query);


    }

    private void createJsonFromDqlQuery(IDfSession session, IDfQuery query) {
        IDfCollection collection;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> map = new HashMap<>();
        String json;

        try {
            collection = query.execute(session, IDfQuery.DF_READ_QUERY);
            while (collection.next()) {
                for (int i = 0; i < collection.getAttrCount(); i++) {
                    String attributeName = collection.getAttr(i).getName();
                    String attributeValue = String.valueOf(collection.getValueAt(i));
                    map.put(attributeName, attributeValue);
                }

                json = gson.toJson(map);
                System.out.println(json);

            }

        } catch (DfException e) {
            e.printStackTrace();
        }
    }

    private void createWorkbookWithSheet(String exportFileLocation, String exportPropertiesFileName) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Properties");
        try {
            OutputStream fileOut = new FileOutputStream(exportFileLocation + exportPropertiesFileName);
            workbook.write(fileOut);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
