package com.device.inspect.common.model.record;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by fgz on 2017/9/3.
 */
@Embeddable
public class ResultsId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deviceId;

    private String inspectPara;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInspectPara() {
        return inspectPara;
    }

    public void setInspectPara(String inspectPara) {
        this.inspectPara = inspectPara;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResultsId other = (ResultsId) obj;
        if ((this.deviceId == null) ? (other.deviceId != null) : !this.deviceId.equals(other.deviceId)) {
            return false;
        }
        if ((this.inspectPara == null) ? (other.inspectPara != null) : !this.inspectPara.equals(
                other.inspectPara)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.deviceId != null ? this.deviceId.hashCode() : 0);
        hash = 41 * hash + (this.inspectPara != null ? this.inspectPara.hashCode() : 0);
        return hash;
    }

}
