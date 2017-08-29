package com.device.inspect.config.python;
/**
 * Created by jill on 17/8/25.
 */
import org.python.core.PyFunction;
import org.python.core.PyString;
import org.python.core.PyList;
import org.python.util.PythonInterpreter;

public class run_python
{
//    public static void main(String args[]){
//        demoTest();
//    }

    public static void demoTest()
    {
        //parameter:
        String kmeans_url="https://asiasoutheast.services.azureml.net/workspaces/222a1ed3b642406b801b76a4a586169c/services/09b92ba52ecc4134b308d9b722fd253a/execute?api-version=2.0&details=true";
        String kmeans_api="riWU/LpQfyBNvcH2HJTXEEBDycUO0NCv80BdjCBcL0Tck64uYQei28x4l5vxsWTWUqUabFvaY2IQlO06B499OA==";

        String host_influx_test ="139.219.198.192";
        String user_influx_test="admin";
        String pwd_influx_test="XEmpLb7YrBRanIW2";
        String dbName_influx="intelab";
        String table_influx="power";
        String selcol_influx="value";
        Integer device_id=477;
        String filter_influx=" where \"device_id\"= \'" + (device_id).toString() + "\' and time>\'2017-08-22 05:30:00\' and time<\'2017-08-22 09:00:00\'";
        String n_clustering="7";

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("print sys.path");
        interpreter.exec("sys.path.append(\"src/main/java/com/device/inspect/config/python\")");
        interpreter.execfile("src/main/java/com/device/inspect/config/python/test.py");
        PyFunction func = (PyFunction)interpreter.get("Kmeans_ws",PyFunction.class);
        PyList list_host=new PyList();          list_host.add(new PyString(host_influx_test));
        PyList list_user=new PyList();          list_user.add(new PyString(user_influx_test));
        PyList list_pwd=new PyList();           list_pwd.add(new PyString(pwd_influx_test));
        PyList list_dbName=new PyList();        list_dbName.add(new PyString(dbName_influx));
        PyList list_table=new PyList();         list_table.add(new PyString(table_influx));
        PyList list_selcol=new PyList();        list_selcol.add(new PyString(selcol_influx));
        PyList list_filter=new PyList();        list_filter.add(new PyString(filter_influx));
        PyList list_n_clustering=new PyList();  list_n_clustering.add(new PyString(n_clustering));
        PyList list=new PyList();
        list.add(list_host);
        list.add(list_user);
        list.add(list_pwd);
        list.add(list_dbName);
        list.add(list_table);
        list.add(list_selcol);
        list.add(list_filter);
        list.add(list_n_clustering);
        PyList retList = (PyList)func.__call__(new PyString(kmeans_url),new PyString(kmeans_api),list);
        for (int i=0;i<retList.size();i++)
        {
            PyList tmp= (PyList)retList.get(i);
            String center=(String)tmp.get(0);
            String delta=(String)tmp.get(1);
            System.out.println(i+" center: "+center+" delta: "+delta);
        }

        //System.out.println("anwser = " + retList.toString());

    }
}