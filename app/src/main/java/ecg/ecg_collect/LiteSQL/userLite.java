package ecg.ecg_collect.LiteSQL;

import org.litepal.crud.DataSupport;

/**
 * Created by 92198 on 2017/12/10.
 */

public class userLite extends DataSupport {

    private String ObjectID;
    private String userName;
    private String userPicAddress;
    private String dataAddress;

    public String getObjectID() {
        return ObjectID;
    }

    public void setObjectID(String objectID) {
        ObjectID = objectID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPicAddress() {
        return userPicAddress;
    }

    public void setUserPicAddress(String userPicAddress) {
        this.userPicAddress = userPicAddress;
    }

    public String getDataAddress() {
        return dataAddress;
    }

    public void setDataAddress(String dataAddress) {
        this.dataAddress = dataAddress;
    }
}
