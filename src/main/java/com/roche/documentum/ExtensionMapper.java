package main.java.com.roche.documentum;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

import java.util.HashMap;
import java.util.Map;

public class ExtensionMapper {

    private static final String dqlQuery = "SELECT DISTINCT a_content_type, dos_extension FROM dm_document,dm_format WHERE a_content_type = name;";
    private Map<String,String> map;


    public ExtensionMapper(IDfSession session) {
        this.map = new HashMap<>();
        IDfQuery query = new DfQuery();
        query.setDQL(dqlQuery);
        try {
            IDfCollection collection = query.execute(session,IDfQuery.DF_READ_QUERY);
            while (collection.next()){
                map.put(collection.getString("a_content_type"),collection.getString("dos_extension"));
            }
        } catch (DfException e) {
            e.printStackTrace();
        }
    }

    public String getExtension(String objectType){
        return map.get(objectType);
    }



}
