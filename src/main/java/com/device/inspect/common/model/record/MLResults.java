package com.device.inspect.common.model.record;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by fgz on 2017/9/1.
 */
@Entity
@Table(name = "ml_results")
public class MLResults implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ResultsId id;

    private String trainingResult;

    public ResultsId getId() {
        return id;
    }

    public void setId(ResultsId id) {
        this.id = id;
    }

    @Column(name = "training_result")
    public String getTrainingResult() {
        return trainingResult;
    }

    public void setTrainingResult(String trainingResult) {
        this.trainingResult = trainingResult;
    }
}
