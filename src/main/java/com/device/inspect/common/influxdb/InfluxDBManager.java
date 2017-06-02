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
                .retentionPolicy("autogen")
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
     * 查询指定设备指定参数最近一条信息
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @return
     */
    public List<Object> readLatestTelemetry(String inspectType, Integer deviceId, Integer deviceInspectId){

        String dbName = "intelab";

        String queryString = String.format("SELECT value FROM %s WHERE device_id='%d' and inspect_id='%d' ORDER BY time DESC LIMIT 1",
                inspectType, deviceId, deviceInspectId);

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
     * handle "SELECT count(X) xxx" query, expecting return single item, so the query cannot have group by
     * @param dbName
     * @param queryString
     * @return number of points meeting the query condition
     */
    private Integer countQuery(String dbName, String queryString){
        Query query = new Query(queryString, dbName);


        try {
            QueryResult result = influxDB.query(query);

            //since a query can contain multiple sub queries, the return value is a list
            List<QueryResult.Result> resultList = result.getResults();

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
     * 查询指定设备指定measurement指定参数指定时间内的报警信息。 注意， 同一设备的可能对同种measurement有多个参数。
     * 例如， 一台大型设备可能不同的部分会有不同的温度
     * @param inspectType
     * @param deviceId
     * @param deviceInspectId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<List<Object>> readTelemetryInTimeRange(String inspectType, Integer deviceId, Integer deviceInspectId, Date startTime, Date endTime){

        String dbName = "intelab";

        // timestamp in influxdb is in nano seconds
        long startNano = startTime.getTime() * 1000000;
        long endNano = endTime.getTime() * 1000000;

        if(startNano > endNano){
            logger.error(String.format("time range illegal, start %d > end %d", startNano, endNano));
            return null;
        }

        String queryString =
                String.format("SELECT value FROM %s WHERE device_id='%d' AND inspect_id='%d' AND time >= %d AND time <= %d ORDER BY time",
                inspectType, deviceId, deviceInspectId, startNano, endNano);

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
}
