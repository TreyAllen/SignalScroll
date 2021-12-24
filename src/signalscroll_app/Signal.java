package signalscroll_app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

/**
 *
 * @author Trey
 */
public class Signal {
    
    DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    
    private int seqNumBig = 0;
    private int taskNum = 0;
    private String taskDesc = "";
    private String convertedDate = "";
    private String xmit = "";
    private String eventCode = "";
    private String eventDesc = "";
    private String eventNum = "";
    private String phone = "";
    private String signalCode = ""; 
    private String point = "";
    private String rawMessage = "";
    private String siteName = "";
    private String CENTRAL_STATION = "";
    
    public Signal(JSONObject result, String cs){

        this.CENTRAL_STATION = cs;
        this.seqNumBig = result.getInt("SeqNumBig");
        this.taskNum = result.getInt("TaskNum");
        
        if (CENTRAL_STATION.compareTo("AMS") == 0) {
            this.taskDesc = findTaskDesc(result.getInt("TaskNum"));
        }
        else {
            this.taskDesc = findTaskDescQR(result.getInt("TaskNum"));
        }
        
        this.xmit = checkIfNull(result, "TransmitterCode");
        this.eventCode = checkIfNull(result, "EventCode");
        this.eventDesc = justEventDesc(checkIfNull(result, "EventCode"));
        this.eventNum = justEventNum(checkIfNull(result, "EventCode"));
        this.phone = checkIfNull(result, "Phone");
        this.signalCode = checkIfNull(result, "SignalCode");
        this.point = checkIfNull(result, "Point");
        this.siteName = checkIfNull(result, "SiteName");
        this.rawMessage = checkIfNull(result, "RawMessage");
        
        String UTCDate = result.getString("UTCDate");
        String receiveDateMilli = UTCDate.substring(UTCDate.indexOf("(")+1, UTCDate.lastIndexOf(")"));
        long milli = Long.parseLong(receiveDateMilli);
        Date date = new Date(milli);
        
        this.convertedDate = df.format(date);
        
    }

    public String getCentralStation() {
        return CENTRAL_STATION;
    }

    public String getEventCode() {
        return eventCode;
    }
    
    public String getEventDesc() {
        return eventDesc;
    }
    
    public String getEventNum() {
        return eventNum;
    }

    public String getPhone() {
        return phone;
    }

    public String getPoint() {
        return point;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public int getSeqNumBig() {
        return seqNumBig;
    }

    public String getSignalCode() {
        return signalCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public String getXmit() {
        return xmit;
    }
    
    public String getConvertedDate() {
        return convertedDate;
    }
    
    private String checkIfNull (JSONObject json, String key) {
        return json.isNull(key) ? " " : json.optString(key);
    }
    
    private String justEventDesc(String event) {
        String output = "";
        try { output = event.substring(event.indexOf("-")+1);} catch (Exception ex) {}
        return output;   
    } 
    
    private String justEventNum(String event) {
        String output = "";
        try { output = event.substring(0, event.indexOf("-"));} catch (Exception ex) {}
        return output;
    } 
    
    //task description for AMS
    private String findTaskDesc(Integer taskNum){
        String output = "";
        switch (taskNum) {
            case 22:  
                output = "Twilio";
                break;
            case 300: case 301: case 302: case 303:
                output = "SG SysIII";
                break;
            case 306: case 307: case 308: case 309: 
                output = "SG SysIV";
                break;
            case 312:  
                output = "AES 1";
                break;
            case 313:  
                output = "AES 2";
                break;
            case 314:  
                output = "DMP 1";
                break;
            case 315:  
                output = "DMP 2";
                break;
            case 318:  
                output = "NAPCO 1";
                break;
            case 319:  
                output = "NAPCO 2";
                break;
            case 320:  
                output = "Uplink 1";
                break;
            case 321:  
                output = "Uplink 2";
                break;
            case 322:  
                output = "AlarmNet 1";
                break;
            case 323:  
                output = "AlarmNet 2";
                break;
            case 324:  
                output = "Alarm.Com";
                break;
            case 326:  
                output = "SecureNet";
                break;
            case 328:  
                output = "SecuraTrac";
                break;
            case 329:  
                output = "Alula";
                break;
            case 330:  
                output = "M2M 1";
                break;
            case 331:  
                output = "M2M 2";
                break;
            case 333:  
                output = "Email";
                break;
            default:
                output = "Task " + taskNum.toString();
        }
        
        return output;
    }
    
    //Task Descriptions for QuickResponse
    private String findTaskDescQR(Integer taskNum){
        String output = "";
        switch (taskNum) {
            case 18:  
                output = "Telephony";
                break;
            case 21:  
                output = "Twilio";
                break;
            case 28:  
                output = "ASAP";
                break;
            case 197:  
                output = "CheckVideo";
                break;
            case 198:  
                output = "Alarm.Com";
                break;
            case 200:  
                output = "XML";
                break;
            case 201: 
                output = "SysIII Pri";
                break;
            case 202:
                output = "SysIII Sec";
                break;
            case 203: 
                output = "D6600 Pri";
                break;
            case 204: 
                output = "D6600 Sec";
                break;
            case 205:  
                output = "AlarmNet-I";
                break;
            case 206:  
                output = "AlarmNet-XB";
                break;
            case 208:  
                output = "ITI 1";
                break;
            case 209:  
                output = "NetLink";
                break;
            case 210:  
                output = "OSB 2000 1";
                break;
            case 211:  
                output = "OH2000 2";
                break;
            case 212:  
                output = "OSB2000 3";
                break;
            case 214:  
                output = "Videofied Pri";
                break;
            case 216:  
                output = "Interlogix Pri";
                break;
            case 217:  
                output = "Interlogix Sec";
                break;
            case 218:  
                output = "Visor 1";
                break;
            case 219:  
                output = "Visor 2";
                break;
            case 220:  
                output = "SysV Pri";
                break;
            case 221:  
                output = "SysV Sec";
                break;
            case 222:  
                output = "SysII Pri";
                break;
            case 223:  
                output = "SysII Sec";
                break;
            case 224:  
                output = "DMP Pri";
                break;
            case 225:  
                output = "DMP Sec";
                break;
            case 226:  
                output = "M2M Pri";
                break;
            case 227:  
                output = "M2M Sec";
                break;
            case 228:  
                output = "AES 1 Pri";
                break;
            case 229:  
                output = "AES 1 ";
                break;
            case 230:  
                output = "Bosch Cloud";
                break;
            case 231:  
                output = "SysV Video Pri";
                break;
            case 233:  
                output = "SysIII Serial Pri";
                break;
            case 234:  
                output = "SysIII Serial Sec";
                break;
            case 235:  
                output = "SecureNet Pri";
                break;
            case 236:  
                output = "Uplink Pri";
                break;
            case 237:  
                output = "Uplink Sec";
                break;
            case 238:  
                output = "M2M TCP-IP Pri";
                break;
            case 239:  
                output = "M2M TCP-IP Sec";
                break;
            default:
                output = "Task " + taskNum.toString();
        }
        
        return output;
    }
   
}
