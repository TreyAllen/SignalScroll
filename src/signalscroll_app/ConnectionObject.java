package signalscroll_app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.json.*;

/**
 *
 * @author Trey
 */
public class ConnectionObject {
    
    StringBuilder sb = new StringBuilder();
    DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    Integer highestSeqNum = 0;
    private String CENTRAL_STATION = "";
    private String IIS_SERVER = "";
    
    
    public ConnectionObject(String cs, String iis){
        this.CENTRAL_STATION = cs;
        this.IIS_SERVER = iis;
    }
    
    
    private JSONObject getRequest(String jsonInput, String action) {
        String wsURL = "";
        
        if (CENTRAL_STATION.compareTo("AMS") == 0) {
            wsURL = "http://" + IIS_SERVER + "/stagesactiveams/Services/AppService.asmx/" + action;
            //wsURL = "http://" + IIS_SERVER + "/stagesamstest/Services/AppService.asmx/" + action;
        }
        else {
            wsURL = "http://" + IIS_SERVER + "/stagesACTIVE/Services/AppService.asmx/" + action;
        }
        
        URL url = null;
        URLConnection connection = null;
        HttpURLConnection httpConn = null;
        String responseString = null;
        String outputString = "";
        OutputStream out = null;
        InputStreamReader isr = null;
        BufferedReader in = null;
        JSONObject jsonObj = null;
       
        try {
            url = new URL(wsURL);
            connection = url.openConnection();
            httpConn = (HttpURLConnection) connection;
            
            byte[] buffer = new byte[jsonInput.length()];
            
            buffer = jsonInput.getBytes();
            
            //set appropriate http parameters
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            httpConn.setRequestProperty("Accept-Language", "en-US,en;q=0.9,da;q=0.8");
            httpConn.setRequestProperty("Connection", "keep-alive");
            httpConn.setRequestProperty("Content-Length", String.valueOf(buffer.length));
            httpConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            
            httpConn.setRequestProperty("Host", IIS_SERVER);
            httpConn.setRequestProperty("Origin", "http://" + IIS_SERVER);
            httpConn.setRequestProperty("Referer", "http://" + IIS_SERVER + "/");
            
            httpConn.setRequestMethod("POST");
            
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            
            out = httpConn.getOutputStream();
           
            out.write(buffer);
            out.close();
            
            //read the response and write it to standard out
            isr = new InputStreamReader(httpConn.getInputStream());
            in = new BufferedReader(isr);

            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
            
            //get response from the web service call
            jsonObj = new JSONObject(outputString);
            
        } catch (Exception e) { e.printStackTrace(); }
        
        //return document;
        return jsonObj;
        
    }
    
    public JSONObject taskList(String sn, String pw) {

        String jsonInput = 
                "{\"procedure\":\"pTaskList\","
                + "\"parameterDictionary\":[],"
                + "\"sessionNum\":" + sn + ","
                + "\"sessionPassword\":" + pw + ","
                + "\"applicationCode\":null}";


        String action = "GetProcedureResult";

        //call the generic request method to return a response request document using passed parameters
        JSONObject getReq = getRequest(jsonInput, action);

        return getReq;

    }
    
    public JSONObject signalLog(String sn, String pw, long dayFull, String startHHmm, String search, String taskNum) {
    
        String jsonInput = 
                "{\"procedure\":\"pSignalLog\","
                + "\"parameterDictionary\":"
                    + "[{\"Name\":\"EndDate\",\"Value\":{\"Value\":\"\\/Date(" + dayFull + ")\\/\"},\"IsOutput\":false},"
                    + "{\"Name\":\"EndTime\",\"Value\":{\"Value\":\"2359\"},\"IsOutput\":false},"
                    + getSearchString(search)
                    + "{\"Name\":\"SignalQueue\",\"Value\":{\"Value\":\"Primary\"},\"IsOutput\":false},"
                    + "{\"Name\":\"StartDate\",\"Value\":{\"Value\":\"\\/Date(" + dayFull + ")\\/\"},\"IsOutput\":false},"
                    + "{\"Name\":\"StartTime\",\"Value\":{\"Value\":\"" + startHHmm + "\"},\"IsOutput\":false},"
                    + getTaskNumString(taskNum)
                    + "{\"Name\":\"TaskType\",\"Value\":{\"IsNull\":true,\"Value\":\"\"},\"IsOutput\":false}],"
                + "\"sessionNum\":" + sn + ","
                + "\"sessionPassword\":" + pw + ","
                + "\"applicationCode\":null}";


        String action = "GetProcedureResult";

        //call the generic request method to return a response request document using passed parameters
        JSONObject getReq = getRequest(jsonInput, action);

        return getReq;

    }
    
    public String getTaskNumString (String passedTask){
        
        if (passedTask.isEmpty() || passedTask.compareTo("All") == 0) {
            return "{\"Name\":\"TaskNum\",\"Value\":{\"IsNull\":true,\"Value\":0},\"IsOutput\":false},";
        }
        
        else {
            
            String output = "";
            try { output = passedTask.substring(0, passedTask.indexOf("-")-1);} catch (Exception ex) {}
            
            return "{Name: \"TaskNum\", Value: {Value: " + output + "}, IsOutput: false},";
        }
        
    }
    
    public String getSearchString (String passedSearch){
        
        if (passedSearch.isEmpty()) {
            return "{\"Name\":\"Search\",\"Value\":{\"IsNull\":true,\"Value\":\"\"},\"IsOutput\":false},";
        }
        
        else {
            return "{Name: \"Search\", Value: {Value: \"" + passedSearch + "\"}, IsOutput: false},";
        }
        
    }
    
    
    public String[] login(String un, String pw) {
        String[] creds = new String[3];
        
        String jsonInput = 
                "{\"userName\":\"" + un + "\","
                + "\"password\":\"" + pw + "\","
                + "\"newPassword\":null,"
                + "\"loginToken\":null,"
                + "\"wrapperFlag\":\"N\","
                + "\"applicationName\":\"SGS Scripting Engine\","
                + "\"applicationVersion\":\"2.0.139\","
                + "\"clientPlatform\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36\","
                + "\"captchaResponse\":null,"
                + "\"loginProcessId\":null,"
                + "\"securityCode\":null}";
        
        String action = "Login";
        
        //call the generic request method to return a response request document using passed parameters
        JSONObject getReq = getRequest(jsonInput, action);
        
        //check for successful return
        String success = getReq.get("Success").toString();

        if (success.compareTo("true") == 0) {
            
            JSONObject results = getReq.getJSONObject("OutputParameter");

            String sessionNum = results.get("SessionNum").toString();
            String sessionPassword = results.get("SessionPassword").toString();
            
            creds[0] = "true";
            creds[1] = sessionNum;
            creds[2] = sessionPassword;
            
            
        }
        
        else {
            String error = getReq.get("UserErrorMessage").toString();
            creds[0] = "false";
            creds[1] = error;
        }

        return creds;
        
    }

    public void logout(String sn, String pw) {
        
        //when passing sn and pw they do not need to be surrounded by quotes
        String jsonInput = 
                "{\"isForced\":false,"
                + "\"logContext\":null,"
                + "\"sessionNum\":" + sn + ","
                + "\"sessionPassword\":" + pw + ","
                + "\"applicationCode\":null}";
        
        String action = "Logout";
        
        //call the request method using passed parameters
        getRequest(jsonInput, action);
          
    }

    private String checkIfNull (JSONObject json, String key) {
        
        return json.isNull(key) ? "" : json.optString(key);
    
    }
    
}
