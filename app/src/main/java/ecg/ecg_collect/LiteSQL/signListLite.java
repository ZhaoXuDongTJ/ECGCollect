package ecg.ecg_collect.LiteSQL;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by 92198 on 2017/12/10.
 */

public class signListLite extends DataSupport {

    private String ObjectID;
    private String dataAddress;
    private Date time;
    private String name;

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public String getDataAddress() {
        return dataAddress;
    }

    public void setDataAddress(String dataAddress) {
        this.dataAddress = dataAddress;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
