package com.device.inspect.common.influxdb;

/**
 * Created by gxu on 5/29/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.TimeUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InfluxDBManager {
    String serverIp;
    Integer port = 8086;

    InfluxDB influxDB = null;
    protected static Logger logger = LogManager.getLogger(InfluxDBManager.class);

    public InfluxDBManager(String serverIp, Integer port){
        this.serverIp = serverIp;
        this.port = port;
        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port));

    }

    public InfluxDBManager(String serverIp){
        this.serverIp = serverIp;

        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port));

    }

    public InfluxDBManager(String serverIp, String username, String password){
        this.serverIp = serverIp;
        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port), username, password);
    }

    public InfluxDBManager(String serverIp, Integer port, String username, String password){
        this.serverIp = serverIp;
        this.port = port;

        influxDB = InfluxDBFactory.connect(String.format("http://%s:%d", serverIp, port), username, password);
    }

    /**
     * 写入设备指定参数的监控信息
     * @param samplingTime
     * @param deviceId
     * @param deviceName
     * @param deviceType
     * @param inspectStatus
     * @param deviceInspectId
     * @param inspectType
     * @param inspectValue
     * @param inspectRawData
     * @return
     */
    public boolean writeTelemetry(Date samplingTime, Integer deviceId, String deviceName,
                                  String deviceType, String inspectStatus,
                               Integer deviceInspectId, String inspectType,
                               float inspectValue, float inspectRawData){

        String dbName = "intelab";

        BatchPoints batchPoints = BatchPoints.database(dbName)
                .tag("device_id", deviceId.toString())
                .tag("device_name", deviceName)
                .tag("inspect_id", deviceInspectId.toString())
                .tag("device_type", deviceType)
                .tag("inspect_status", inspectStatus)
                .retentionPolicy("original_telemetry")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point = Point.measurement(inspectType)
                .time(samplingTime.getTime(), TimeUnit.MILLISECONDS)
                .addField("value", inspectValue)
                .addField("raw_data", inspectRawData)
                .build();

        batchPoints.point(point);
        try {
            influxDB.write(batchPoints);
            return true;
        }catch (Exception e){
            e.printStackTrace();

            logger.error(String.format("Failed to write inspect data to influxdb. Error: %s", e.toString()));
            return false;
        }

    }

    /**
     * 写入设备在某时间的运行状态
     * @param samplingTime
     * @param deviceId
     * @param deviceName
     * @param deviceType
     * @param status
     * @return
     */
    public boolean writeDeviceOperatingStatus(Date samplingTime, Integer deviceId, String deviceName, String deviceType, int status){
        String dbName = "intelab";

        BatchPoints batchPoints = BatchPoints.database(dbName)
                .tag("device_id", deviceId.toString())
                .tag("device_name", deviceName)
                .tag("device_type", deviceType)
                .retentionPolicy("utilizations")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point = Point.measurement("operating_status")
                .time(samplingTime.getTime(), TimeUnit.MILLISECONDS)
                .addField("value", status)
                .build();

        batchPoints.point(point);
        try {
            influxDB.write(batchPoints);
            return true;
        }catch (Exception e){
            e.printStackTrace();

            logger.error(String.format("Failed to write operating status to influxdb. Error: %s", e.toString()));
            return false;
        }

    }

    /**
     * 写入指定设备指定时间的每小时利用率数据
     * @param samplingTime
     * @param deviceId
     * @param deviceName
     * @param deviceType
     * @param running_time
     * @param idle_time
     * @param power_lower_bound
     * @param power_upper_bound
     * @param consumed_energy
     * @param overwrite
     * @return
     */
    public boolean writeHourlyUtilization(Date samplingTime, Integer deviceId, String deviceName, String deviceType,
                                          Integer running_time, Integer idle_time,
                                          float power_lower_bound, float power_upper_bound,
                                          float consumed_energy, boolean overwrite){

        List<List<Object>> existingUtilization = readDeviceUtilizationInTimeRange(deviceId, samplingTime, DateUtils.addMinutes(samplingTime, 10));

        if (existingUtilization != null && existingUtilization.size() > 0){
            if (overwrite){
                logger.info(String.format("utilization data of device %d at hour %s already exist, since overwrite is true, deleting", deviceId, samplingTime));

                if(!deleteDeviceUtilizationInTimeRange(deviceId, samplingTime, DateUtils.addMinutes(samplingTime, 10))){
                    logger.error(String.format("FAILED to delete utilization data of device %d at hour %s , do not add new one", deviceId, samplingTime));
                    return false;
                }
            }else{
                logger.info(String.format("utilization data of device %d at hour %s already exist, since overwrite is False, skipping", deviceId, samplingTime));
                return true;
            }

        }

        String dbName = "intelab";

        BatchPoints batchPoints = BatchPoints.database(dbName)
                .tag("device_id", deviceId.toString())
                .tag("device_name", deviceName)
                .tag("device_type", deviceType)
                .retentionPolicy("utilizations")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();

        Point point = Point.measurement("utilization_rate")
                .time(samplingTime.getTime(), TimeUnit.MILLISECONDS)
                .addField("running_seconds", running_time)
                .addField("idle_seconds", idle_time)
                .addField("power_lower_bound", power_lower_bound)
                .addField("power_upper_bound", power_upper_bound)
                .addField("consumed_energy", consumed_energy)
                .build();

        batchPoints.point(point);
        try {
            influxDB.write(batchPoints);
            logger.info(String.format("Successfully write hourly utilization data to influxdb. device %d, time %s, running seconds %s", deviceId, samplingTime, running_time));
            return true;
        }catch (Exception e){
            e.printStackTrace();

            logger.error(String.format("Failed to write utilization data to influxdb. Error: %s", e.toString()));
            return false;
        }

    }

    /**
     * 写入调用API的TS数据, 包括用户, 链接, 返回值, 消耗时间
     * @param startTime
     * @param userName
     * @param url
     * @param responseCode
     * @param duration
     * @return
     */
    public boolean writeAPIOperation(Long startTime, String userName, String url, String httpMethod, String parameters, Integer responseCode, long duration){
        String dbName = "intelab";


        BatchPoints batchPoints;

        if(parameters == null || parameters=="") {
            batchPoints = BatchPoints.database(dbName)
                    .tag("url", url)
                    .tag("method", httpMethod)
                    .tag("response", responseCode.toString())
                    .tag("username", userName)
                    .retentionPolicy("operations")
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .build();
        }else{
            batchPoints = BatchPoints.database(dbName)
                    .tag("url", url)
                    .tag("method", httpMethod)
                    .tag("parameters", parameters)
                    .tag("response", responseCode.toString())
                    .tag("username", userName)
                    .retentionPolicy("operations")
                    .consistency(InfluxDB.ConsistencyLevel.ALL)
                    .build();
        }


        Point point = Point.measurement("operation")
                .time(startTime, TimeUnit.MILLISECONDS)
                .addField("duration", duration)
                .build();

        batchPoints.point(point);
        try {
            influxDB.write(batchPoints);
            return true;
        }catch (Exception e){
            e.printStackTrace();

            logger.error(String.format("Failed to write API operation data to influxdb. Error: %s", e.toString()));
            return false;
        }
    }

    public List<Object> readLatestOperation(String url, String userName){
        String dbName = "intelab";

        Date startTime = new Date();

        String queryString = String.format("SELECT url, method, parameters, \"duration\" FROM operations.operation WHERE url='%s' and username='%s' and response='200' ORDER BY time DESC LIMIT 1",
                url, userName);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            Date endTime = new Date();

            long timeCost = endTime.getTime() - startTime.getTime();
            logger.debug(String.format("Select query [%s] takes %d ms", queryString, timeCost));

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'value']

                    if(!columes.contains("time")){
                        logger.error("The series in influxdb query result is incorrect, no time");
                        return null;
                    }

                    List<List<Object>> tsDataEntries = series.get(0).getValues();
                    if(tsDataEntries != null && tsDataEntries.size() > 0){

                        return tsDataEntries.get(0);
                    }

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    public Date getLatestPasswordUpdateTime(String userName){
        List<Object> timeEntry = readLatestOperation("/api/rest/operate/modify/password", userName);

        if (timeEntry == null || timeEntry.size() == 0){
            return new GregorianCalendar(1970, 1, 1).getTime();
        }
        return new Date(TimeUtil.fromInfluxDBTimeFormat((String)timeEntry.get(0)));
    }

    private List<List<Object>> executeQuery(String queryString, String dbName){
        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', value]

                    if( !columes.contains("time") || !columes.contains("url")){
                        logger.error("The series in query result is incorrect, no time or url");
                        return null;
                    }

                    return series.get(0).getValues();

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    public List<List<Object>> readAPIByMethodUsernameInTimeRange(String method, String userName, Date startTime, Date endTime){
        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String queryString =
                String.format("SELECT url, method, parameters, username, \"duration\" FROM operations.operation WHERE username='%s' AND method='%s' AND time >= %d AND time < %d ORDER BY time DESC LIMIT 1000",
                        userName, method, startNano, endNano);


        List<List<Object>> result =  executeQuery(queryString, dbName);
        return result;


    }

    public List<List<Object>> readAPIByMethondCompanyInTimeRange(String method, String companyName, Date startTime, Date endTime){
        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String queryString =
                String.format("SELECT url, method, parameters, username, \"duration\" FROM operations.operation WHERE username =~ /@%s$/ AND method='%s' AND time >= %d AND time < %d ORDER BY time DESC LIMIT 1000",
                        companyName, method, startNano, endNano);


        List<List<Object>> result =  executeQuery(queryString, dbName);
        return result;

    }

    /**
     * 查询指定设备指定参数最近一条信息
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @return
     */
    public List<Object> readLatestTelemetry(String inspectType, Integer deviceId, Integer deviceInspectId){

        String dbName = "intelab";

        Date startTime = new Date();

        String queryString = String.format("SELECT value,inspect_status FROM %s WHERE device_id='%d' and inspect_id='%d' ORDER BY time DESC LIMIT 1",
                inspectType, deviceId, deviceInspectId);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            Date endTime = new Date();

            long timeCost = endTime.getTime() - startTime.getTime();
            logger.info(String.format("Select query [%s] takes %d ms", queryString, timeCost));

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    String measurementName = series.get(0).getName();
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'value']

                    if(columes.size() != 3 || !columes.contains("value") || !columes.contains("time") || !columes.contains("inspect_status")){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    List<List<Object>> tsDataEntries = series.get(0).getValues();
                    if(tsDataEntries != null && tsDataEntries.size() > 0){

                        return tsDataEntries.get(0);
                    }

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    /**
     * 读取某设备在某时间点之前最近的运行状态
     * @param deviceId
     * @param referenceTime
     * @return
     */
    public List<Object> readLatestDeviceOperatingStatus(Integer deviceId, Date referenceTime){
        String dbName = "intelab";


        Date refTime = new Date();

        if (referenceTime != null){
            refTime = referenceTime;
        }

        long refTimeNano = refTime.getTime() * 1000000;

        String queryString = String.format("SELECT value FROM utilizations.operating_status WHERE device_id='%d' and time <= %d ORDER BY time DESC LIMIT 1",
                deviceId, refTimeNano);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    String measurementName = series.get(0).getName();
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'value']

                    if(columes.size() != 2 || !columes.contains("value") || !columes.contains("time") ){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    List<List<Object>> tsDataEntries = series.get(0).getValues();
                    if(tsDataEntries != null && tsDataEntries.size() > 0){

                        return tsDataEntries.get(0);
                    }

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    /**
     * 读取某设备在某时间范围内的状态变化序列
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<List<Object>> readDeviceOperatingStatusInTimeRange(Integer deviceId, Date startTime, Date endTime){
        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String queryString =
                String.format("SELECT value FROM utilizations.operating_status WHERE device_id='%d' AND time >= %d AND time < %d ORDER BY time",
                        deviceId, startNano, endNano);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', value]

                    if(columes.size() != 2 || !columes.contains("time")){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    return series.get(0).getValues();

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query utilization from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    /**
     * handle "SELECT count(X) xxx" query, expecting return single item, so the query cannot have group by
     * @param dbName
     * @param queryString
     * @return number of points meeting the query condition
     */
    private Integer countQuery(String dbName, String queryString){

        Date startTime = new Date();
        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            Date endTime = new Date();

            long timeCost = endTime.getTime() - startTime.getTime();
            logger.info(String.format("Count Query [%s] takes %d ms", queryString, timeCost));

            if(resultList != null && resultList.size() > 0) {
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if (series != null && series.size() > 0) {
                    String measurementName = series.get(0).getName();
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'count']

                    if (columes.size() != 2 || !columes.contains("count") || !columes.contains("time")) {
                        logger.error("The series in query result is incorrect, no time or value");
                        return -1;
                    }

                    List<List<Object>> tsDataEntries = series.get(0).getValues();
                    if (tsDataEntries != null && tsDataEntries.size() > 0) {

                        List<Object> countTuple = tsDataEntries.get(0);

                        if (countTuple != null && countTuple.size() > 0) {
                            return ((Double) countTuple.get(1)).intValue();
                        } else {
                            return 0;
                        }
                    }
                }
            }

            return 0;



        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return -1;
        }
    }

    /**
     * 查询指定设备， 指定参数指定时间内 监控信息条数
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceTelemetryByInspectTypeAndTime(String inspectType, Integer deviceId, Integer deviceInspectId, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }


        String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d",
                inspectType, deviceId, deviceInspectId, startNano, endNano);

        return countQuery(dbName, queryString);
    }

    /**
     * 查询指定设备指定时间内 指定measurement 监控信息条数
     * @param inspectType
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceTelemetryByTime(String inspectType, Integer deviceId, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }

        String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND time >= %d AND time <= %d",
                inspectType, deviceId, startNano, endNano);

        return countQuery(dbName, queryString);
    }

    /**
     * 查询指定设备指定时间所有measurement的监控信息条数
     * @param inspectTypes
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceTotalTelemetryByTime(List<String> inspectTypes, Integer deviceId, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }

        Integer countTotal = 0;
        for(String measurement: inspectTypes) {

            String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND time >= %d AND time <= %d",
                    measurement, deviceId, startNano, endNano);

            Integer count = countQuery(dbName, queryString);
            if(count > 0){
                countTotal += count;
            }
        }

        // for debug
        //logger.info(String.format("Device %d has %d inspect data from %s", deviceId, countTotal, startTime.toString()));

        return countTotal;
    }


    /**
     * 查询指定设备指定时间内所有measurement指定报警类型的条数
     * @param inspectTypes
     * @param deviceId
     * @param inspectStatus
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceTotalAlertByTime(List<String> inspectTypes, Integer deviceId, String inspectStatus, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }

        Integer countTotal = 0;
        for(String measurement: inspectTypes) {
            String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND inspect_status='%s' AND time >= %d AND time <= %d",
                    measurement, deviceId, inspectStatus, startNano, endNano);

            Integer count = countQuery(dbName, queryString);
            if (count > 0){
                countTotal += count;
            }
        }

        //for debug
        //logger.info(String.format("Device %d has %d %s alerts from time %s", deviceId, countTotal, inspectStatus, startTime.toString()));
        return countTotal;

    }

    /**
     * 查询指定设备指定时间内指定measurement指定报警信息条数
     * @param inspectType
     * @param inspectStatus
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceAlertByTime(String inspectType, Integer deviceId, String inspectStatus, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }

        String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND inspect_status='%s' AND time >= %d AND time <= %d",
                inspectType, deviceId, inspectStatus, startNano, endNano);

        return countQuery(dbName, queryString);
    }

    /**
     * 查询指定设备指定时间内指定measurement 非指定信息类型的条数
     * @param inspectType
     * @param inspectStatus
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public Integer countDeviceNotCertainStatusByTime(String inspectType, Integer deviceId, String inspectStatus, Date startTime, Date endTime){

        String dbName = "intelab";
        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return -1;
        }

        String queryString = String.format("SELECT count(value) FROM %s WHERE device_id='%d' AND inspect_status<>'%s' AND time >= %d AND time <= %d",
                inspectType, deviceId, inspectStatus, startNano, endNano);

        return countQuery(dbName, queryString);
    }



    /**
     * 查询指定设备指定measurement指定参数指定时间内的报警信息。 注意， 同一设备的可能对同种measurement有多个参数。
     * 例如， 一台大型设备可能不同的部分会有不同的温度
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<List<Object>> readTelemetryInTimeRange(String inspectType, Integer deviceId, Integer deviceInspectId, Date startTime, Date endTime, int granularity){

        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String retentionPolicy;

        switch(granularity){
            case Calendar.MINUTE: retentionPolicy = "ten_min"; break;
            case Calendar.HOUR: retentionPolicy = "hourly"; break;
            case Calendar.DATE: retentionPolicy = "daily"; break;
            default: retentionPolicy="original_telemetry";
        }

        String queryString = null;

        if(retentionPolicy == "original_telemetry"){
            queryString = String.format("SELECT value FROM %s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d ORDER BY time",
                    inspectType, deviceId, deviceInspectId, startNano, endNano);
        }else{
            queryString = String.format("SELECT mean_value as value FROM %s.%s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d ORDER BY time",
                    retentionPolicy, inspectType, deviceId, deviceInspectId, startNano, endNano);
        }


        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    String measurementName = series.get(0).getName();
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', 'value']

                    if(columes.size() != 2 || !columes.contains("value") || !columes.contains("time")){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    return series.get(0).getValues();

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    /**
     * 查询指定设备指定时间内的利用率。
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<List<Object>> readDeviceUtilizationInTimeRange(Integer deviceId, Date startTime, Date endTime){

        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String queryString =
                String.format("SELECT running_seconds,idle_seconds,power_lower_bound,power_upper_bound,consumed_energy FROM utilizations.utilization_rate WHERE device_id='%d' AND time >= %d AND time < %d ORDER BY time",
                         deviceId, startNano, endNano);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

            if(resultList != null && resultList.size() > 0){
                QueryResult.Result tsData = resultList.get(0);

                List<QueryResult.Series> series = tsData.getSeries();

                if(series != null && series.size() > 0){
                    List<String> columes = series.get(0).getColumns();

                    // columes should be ['time', running_seconds,idle_seconds,power_lower_bound,power_upper_bound,consumed_energy]

                    if(columes.size() != 6 || !columes.contains("time")){
                        logger.error("The series in query result is incorrect, no time or value");
                        return null;
                    }

                    return series.get(0).getValues();

                }

            }

            return null;


        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query utilization from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return null;
        }
    }

    /**
     * 删除指定设备指定时间内的利用率。
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public boolean deleteDeviceUtilizationInTimeRange(Integer deviceId, Date startTime, Date endTime){

        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));

        }

        String queryString =
                String.format("DELETE FROM utilizations.utilization_rate WHERE device_id='%d' AND time >= %d AND time < %d ORDER BY time",
                        deviceId, startNano, endNano);

        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            return true;

        }catch (Exception e){
            e.printStackTrace();
            logger.error(String.format("Failed to query utilization from influxDB. query -- %s, Err: %s", queryString, e.toString()));

            return false;
        }
    }
}
