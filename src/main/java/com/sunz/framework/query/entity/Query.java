//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.sunz.framework.query.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(
    name = "T_S_Query"
)
public class Query extends com.sunz.framework.core.Entity {
    private String resources;
    private String dicts;
    private String key;
    private String description;
    private String sqlid;
    private String setting;
    private String doubleHandler;
    private String catagory;
    SqlStatement sqlStatement;
    private List<ResultField> resultFields;
    private List<SearchField> searchFields;
    private List<Operation> operations;
    private boolean multiSelect;
    private String gridSetting;
    private boolean autoLoadData;
    private String dataUrl;
    private int SFRestrictMode;
    private int RFRestrictMode;
    private int operationRestrictMode;
    private String configs;

    public Query() {
    }

    public String getResources() {
        return this.resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getDicts() {
        return this.dicts;
    }

    public void setDicts(String dicts) {
        this.dicts = dicts;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSqlid() {
        return this.sqlid;
    }

    public void setSqlid(String sqlid) {
        this.sqlid = sqlid;
    }

    public String getSetting() {
        return this.setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getDoubleHandler() {
        return this.doubleHandler;
    }

    public void setDoubleHandler(String doubleHandler) {
        this.doubleHandler = doubleHandler;
    }

    public String getCatagory() {
        return this.catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    @OneToOne(
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "sqlid",
        insertable = false,
        updatable = false
    )
    public SqlStatement getSqlStatement() {
        return this.sqlStatement;
    }

    public void setSqlStatement(SqlStatement sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    @OneToMany(
        targetEntity = ResultField.class,
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        cascade = {CascadeType.REMOVE}
    )
    @JoinColumn(
        name = "queryid"
    )
    public List<ResultField> getResultFields() {
        return this.resultFields;
    }

    @OneToMany(
        targetEntity = SearchField.class,
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        cascade = {CascadeType.REMOVE}
    )
    @JoinColumn(
        name = "queryid"
    )
    public List<SearchField> getSearchFields() {
        return this.searchFields;
    }

    public void setResultFields(List<ResultField> resultFields) {
        this.resultFields = resultFields;
    }

    public void setSearchFields(List<SearchField> searchFields) {
        this.searchFields = searchFields;
    }

    @OneToMany(
        targetEntity = Operation.class,
        fetch = FetchType.LAZY,
        orphanRemoval = true,
        cascade = {CascadeType.REMOVE}
    )
    @JoinColumn(
        name = "queryid"
    )
    public List<Operation> getOperations() {
        return this.operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public boolean isMultiSelect() {
        return this.multiSelect;
    }

    public String getGridSetting() {
        return this.gridSetting;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setGridSetting(String gridSetting) {
        this.gridSetting = gridSetting;
    }

    public boolean isAutoLoadData() {
        return this.autoLoadData;
    }

    public void setAutoLoadData(boolean autoLoadData) {
        this.autoLoadData = autoLoadData;
    }

    public String getDataUrl() {
        return this.dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public int getSFRestrictMode() {
        return this.SFRestrictMode;
    }

    public void setSFRestrictMode(int sFRestrictMode) {
        this.SFRestrictMode = sFRestrictMode;
    }

    public int getRFRestrictMode() {
        return this.RFRestrictMode;
    }

    public void setRFRestrictMode(int rFRestrictMode) {
        this.RFRestrictMode = rFRestrictMode;
    }

    public int getOperationRestrictMode() {
        return this.operationRestrictMode;
    }

    public void setOperationRestrictMode(int operationRestrictMode) {
        this.operationRestrictMode = operationRestrictMode;
    }

    public String getConfigs() {
        return this.configs;
    }

    public void setConfigs(String configs) {
        this.configs = configs;
    }
}
