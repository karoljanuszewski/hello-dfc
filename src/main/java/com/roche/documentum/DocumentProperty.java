package main.java.com.roche.documentum;

/*
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/

import com.documentum.fc.client.*;
import com.documentum.fc.client.acs.IDfAcsClient;
import com.documentum.fc.common.DfException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sun.istack.Nullable;
import jxl.Workbook;
import jxl.write.Number;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import netscape.javascript.JSObject;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class DocumentProperty {


    public DocumentProperty(String exportFileLocation, String exportPropertiesFileName, ArrayList<String> rObjectId, IDfSession session) {
        IDfQuery query = new DfQuery();
        String dql = "SELECT * FROM dm_document WHERE r_object_id = '" + rObjectId.get(0) + "';";
        System.out.println(dql);
        query.setDQL(dql);
       // createJsonFromDqlQuery(session, query);
        JsonObject jsonObject = buildJsonFromDqlQuery(session,query);

        String stringFromJson = createStringFromJson(jsonObject);
        System.out.println(stringFromJson);

        createWorkbook(exportFileLocation, exportPropertiesFileName);


    }

    private void createWorkbook(String exportFileLocation, String exportPropertiesFileName) {
       // WritableWorkbook workbook = null;

        WritableWorkbook myFirstWbook = null;

        try {
            myFirstWbook = Workbook.createWorkbook(new File(exportFileLocation+exportPropertiesFileName));

            WritableSheet excelSheet = myFirstWbook.createSheet("Sheet1", 0);

            // add something into the Excel sheet
            Label label = new Label(0, 0, "Test Count");
            excelSheet.addCell(label);

            Number number = new Number(0, 1, 1);
            excelSheet.addCell(number);

            label = new Label(1, 0, "Result");
            excelSheet.addCell(label);

            label = new Label(1, 1, "Passed");
            excelSheet.addCell(label);

            number = new Number(0, 2, 2);
            excelSheet.addCell(number);

            label = new Label(1, 2, "Passed 2");
            excelSheet.addCell(label);

            myFirstWbook.write();


/*
            workbook= Workbook.createWorkbook(new File(exportFileLocation+exportPropertiesFileName));
            WritableSheet sheet = workbook.createSheet("Sheet 1",0);

            Number number = new Number(0,0,1);
            sheet.addCell(number);*/
        } catch (IOException | WriteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (myFirstWbook != null){
                    (myFirstWbook).close();
                }
            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }
        }
    }

    private String createStringFromJson(JsonObject jsonObject) {
        return ("["+jsonObject+"]");
    }

    private JsonObject buildJsonFromDqlQuery(IDfSession session, IDfQuery query) {
        IDfCollection collection;

        JsonObject jsonObject = new JsonObject();

        try {
            collection = query.execute(session, IDfQuery.DF_READ_QUERY);
            while (collection.next()) {
                for (int i = 0; i < collection.getAttrCount(); i++) {
                    String attributeName = collection.getAttr(i).getName();
                    String attributeValue = String.valueOf(collection.getValueAt(i));
                    jsonObject.addProperty(attributeName,attributeValue);

                }

                System.out.println(jsonObject);

            }

        } catch (DfException e) {
            e.printStackTrace();
        }
        return jsonObject;
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



/*
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

    }*/


}
