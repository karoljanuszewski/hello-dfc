package main.java.com.roche.documentum;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DocumentProperty {

    private String exportFileLocation;
    private String exportPropertiesFileName;
    private ArrayList<String> rObjectId;
    private IDfSession session;

    public DocumentProperty(String exportFileLocation, String exportPropertiesFileName, ArrayList<String> rObjectId, IDfSession session) {

        this.exportFileLocation=exportFileLocation;
        this.exportPropertiesFileName=exportPropertiesFileName;
        this.rObjectId=rObjectId;
        this.session=session;

    }

    public void createWorkbook() {
        WritableWorkbook workbook = null;
        IDfQuery query = new DfQuery();

        try {
            workbook = Workbook.createWorkbook(new File(exportFileLocation + exportPropertiesFileName));

            WritableSheet excelSheet = workbook.createSheet("Sheet1", 0);

            List<String> attributeList;
            attributeList = getLabelsFromAttributeNames();


            for (int i = 0; i < attributeList.size(); i++) {
                Label label = new Label(i, 0, attributeList.get(i));
                excelSheet.addCell(label);
            }

            rObjectId.get(0);


            List<String> valueList;

            for (int j = 0; j < rObjectId.size(); j++) {
                for (int i = 0; i < attributeList.size(); i++) {
                    valueList = getValuesFromAttributesForOneObject(rObjectId.get(j));
                    Label label = new Label(i, j + 1, valueList.get(i));
                    excelSheet.addCell(label);
                }
            }

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

    private List<String> getValuesFromAttributesForOneObject(String oneRObjectId) {
        IDfCollection collection;
        IDfQuery query = new DfQuery();
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


    private List<String> getLabelsFromAttributeNames() {
        IDfCollection collection;
        IDfQuery query = new DfQuery();
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


}
