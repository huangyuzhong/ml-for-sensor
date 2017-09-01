package com.device.inspect.config.python;

import org.python.core.PyFunction;
import org.python.core.PyList;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

/**
 * Created by FGZ on 17/8/30.
 */
public class UseAML {
    public static Integer doTask(String device_id, String inspect_para, String val) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("import sys");
        interpreter.exec("sys.path.append(\"src/main/java/com/device/inspect/config/python\")");
        interpreter.execfile("src/main/java/com/device/inspect/config/python/Kmeans_use.py");
        PyFunction func = (PyFunction)interpreter.get("use",PyFunction.class);

        PyList retList=(PyList)func.__call__(new PyString(device_id),new PyString(inspect_para),new PyString(val));
        PyList tmp= (PyList)retList.get(0);
        Integer type=Integer.valueOf((String)tmp.get(0));
        System.out.print(type);
        return type;
    }

//    public static void main (String[] args){
//        doTask("477", "power", "5");
//    }
}
