package src.main.java.com.device.inspect.controller.request;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by liliang on 2017/3/18.
 */
public class test1 {
    public static void main(String[] args) {
        String str = "20170318140523";
        //System.out.println(str);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            Date date = sdf.parse(str);
            //SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //String date2 = sdf2.format(date);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}