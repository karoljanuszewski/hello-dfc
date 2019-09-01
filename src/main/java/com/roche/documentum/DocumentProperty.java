package main.java.com.roche.documentum;

/*
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/

import com.documentum.fc.client.*;
import com.documentum.fc.common.DfException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class DocumentProperty {


    public DocumentProperty(String exportFileLocation, String exportPropertiesFileName, ArrayList<String> rObjectId, IDfSession session) {


        List<JsonObject> jsonList;
        IDfQuery query = new DfQuery();

        //  JsonObject jsonObject = buildJsonFromDqlQuery(session,query);

        jsonList = buildJsonArrayFromDqlQuery(session, query, rObjectId);


        createWorkbook(exportFileLocation, exportPropertiesFileName, jsonList,session,rObjectId, query);


    }

    private void createWorkbook(String exportFileLocation, String exportPropertiesFileName, List<JsonObject> jsonList, IDfSession session, ArrayList<String> rObjectId, IDfQuery query) {
        // WritableWorkbook workbook = null;

        WritableWorkbook workbook = null;


        try {
            workbook = Workbook.createWorkbook(new File(exportFileLocation + exportPropertiesFileName));

            WritableSheet excelSheet = workbook.createSheet("Sheet1", 0);
//   createLabelsInSheet(jsonList.get(0));
            // add something into the Excel sheet


            List<String> attributeList = new ArrayList<>();
            attributeList = getLabelsFromAttributeNames(session,query,rObjectId);


         //   valueList = getValuesFromAttributesForOneObject(session,query,rObjectId);


            for (int i = 0; i <attributeList.size() ; i++) {
                Label label= new Label(i,0,attributeList.get(i));
                excelSheet.addCell(label);
            }

            rObjectId.get(0);


            List<String> valueList = new ArrayList<>();

            for (int j = 0; j <rObjectId.size() ; j++) {
                for (int i = 0; i <attributeList.size() ; i++) {
                    valueList = getValuesFromAttributesForOneObject(session,query,rObjectId.get(j));
                    Label label = new Label(i,j+1,valueList.get(i));
                    excelSheet.addCell(label);
                }
            }


 /*           for (String rObject:rObjectId
                 ) {
                valueList = getValuesFromAttributesForOneObject(session,query,rObject);
            }*/


/*
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
*/

            workbook.write();


        } catch (IOException | WriteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null) {
                    (workbook).close();
                }
            } catch (IOException | WriteException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getValuesFromAttributesForOneObject(IDfSession session, IDfQuery query, String oneRObjectId) {
        IDfCollection collection;
        List<String> attributeValueList = new ArrayList<>();

            try {
                String dql = "SELECT * FROM dm_document WHERE r_object_id = '" + oneRObjectId + "';";
                query.setDQL(dql);
                collection = query.execute(session, IDfQuery.DF_READ_QUERY);
                while (collection.next()) {
                    for (int i = 0; i < collection.getAttrCount(); i++) {
                        attributeValueList.add(String.valueOf(collection.getValueAt(i)));
                    }
                }
            } catch (DfException e) {
                e.printStackTrace();
            }

        System.out.println(attributeValueList);
        return attributeValueList;

    }



    private List<String> getLabelsFromAttributeNames(IDfSession session, IDfQuery query, ArrayList<String> rObjectId) {
        IDfCollection collection;
        List<String> attributeNameList = new ArrayList<>();

            try {
                String dql = "SELECT * FROM dm_document WHERE r_object_id = '" + rObjectId.get(0) + "';";
                query.setDQL(dql);
                collection = query.execute(session, IDfQuery.DF_READ_QUERY);
                while (collection.next()) {
                    for (int i = 0; i < collection.getAttrCount(); i++) {
                        attributeNameList.add(collection.getAttr(i).getName());
                    }
                }
            } catch (DfException e) {
                e.printStackTrace();
            }

        System.out.println(attributeNameList);
        return attributeNameList;

    }

    private String createStringFromJson(JsonObject jsonObject) {
        return ("[" + jsonObject + "]");
    }


    private List<JsonObject> buildJsonArrayFromDqlQuery(IDfSession session, IDfQuery query, ArrayList<String> rObjectId) {
        IDfCollection collection;

        List<JsonObject> jsonArray = new ArrayList<>();
        JsonObject jsonObject = new JsonObject();

        for (String s : rObjectId) {
            try {
                String dql = "SELECT * FROM dm_document WHERE r_object_id = '" + s + "';";
                query.setDQL(dql);
                collection = query.execute(session, IDfQuery.DF_READ_QUERY);
                while (collection.next()) {
                    for (int i = 0; i < collection.getAttrCount(); i++) {
                        String attributeName = collection.getAttr(i).getName();
                        String attributeValue = String.valueOf(collection.getValueAt(i));
                        jsonObject.addProperty(attributeName, attributeValue);

                    }

                    //  jsonArray.add(jsonObject);
                    System.out.println("JsonObject: " + jsonObject);

                }

            } catch (DfException e) {
                e.printStackTrace();
            }
            jsonArray.add(jsonObject);

        }

        return jsonArray;
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
                    jsonObject.addProperty(attributeName, attributeValue);

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


}
