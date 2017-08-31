package com.device.inspect.config.python;

import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 * Created by FGZ on 17/8/30.
 */
public class UseAML {
    public static Integer doTask(String kmeans_use_url, String kmeans_use_api, String device_id, String inspect_para, String val)
    {
        //parameter:
//        String kmeans_use_url="https://asiasoutheast.services.azureml.net/workspaces/222a1ed3b642406b801b76a4a586169c/services/c4b910034b2b498e84ddd3b88b9ffd22/execute?api-version=2.0&details=true";
//        String kmeans_use_api="etsdKfmQZ/PtzwR1qvCKOCCKuzJePhbAS3KYQqNB0/H5234fg6AshrOWOqsaSFRBWXygxAmBv60ECdMA7ta4OQ==";

        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append(\"src/main/java/com/device/inspect/config/python\")");

        interpreter.execfile("src/main/java/com/device/inspect/config/python/ModelDemo.py");
        PyFunction func = (PyFunction)interpreter.get("ws_",PyFunction.class);
//        String device_id="477";
//        String inspect_para="power";//检测对象参数
//        String val="5";
        PyList list_device_id=new PyList();          list_device_id.add(new PyString(device_id));
        PyList list_inspect_para=new PyList();          list_inspect_para.add(new PyString(inspect_para));
        PyList list_val=new PyList();          list_val.add(new PyString(val));
        PyList list=new PyList();
        list.add(list_device_id);
        list.add(list_inspect_para);
        list.add(list_val);
        PyList retList=(PyList)func.__call__(new PyString(kmeans_use_url),new PyString(kmeans_use_api),list);//[['0']]
        PyList tmp= (PyList)retList.get(0);
        Integer type=Integer.valueOf((String)tmp.get(0));
        System.out.print(type);
        return type;
    }
}
