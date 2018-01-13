package ecg.ecg_collect.LiteSQL.list;

import java.util.Date;

/**
 * Created by 92198 on 2017/12/24.
 */

public class UIsignList {

    private Date time;
    private String name;

    public UIsignList(Date time, String name) {
        this.time = time;
        this.name = name;
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
