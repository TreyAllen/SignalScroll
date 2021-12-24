package signalscroll_app;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * FXML Controller class
 *
 * @author Trey
 */
public class InternalController implements Initializable {
    
    @FXML
    private Button logoutButton;
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button stopButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Label taskPickerLabel;
    
    @FXML
    private Label searchLabel;
    
    @FXML
    private TextField searchTextField;
    
    @FXML
    private ComboBox taskPickerComboBox;
    
    @FXML
    private TableView signalLogTable = new TableView();
    
    @FXML
    private TableColumn colTask = new TableColumn("Task");
    
    @FXML
    private TableColumn colDate = new TableColumn("Date");
    
    @FXML
    private TableColumn colXmit = new TableColumn("Xmit");
    
    @FXML
    private TableColumn colEventCode = new TableColumn("Event Code");
    
    @FXML
    private TableColumn colSignalCode = new TableColumn("Signal Code");
    
    @FXML
    private TableColumn colPoint = new TableColumn("Point");
    
    @FXML
    private TableColumn colPhone = new TableColumn("Caller ID");
    
    @FXML
    private TableColumn colSiteName = new TableColumn("Site Name");
    
    @FXML
    private TableColumn colRawMessage = new TableColumn("Raw Message");
    
    @FXML
    private GridPane grid = new GridPane();
    
    @FXML
    private Pane pane = new Pane();
    
    @FXML
    private AnchorPane anchorPane = new AnchorPane();

    private String CENTRAL_STATION = "";
    private String IIS_SERVER = "";
    private String VALID_SN = "";
    private String VALID_PW = "";
    private int i = 0;
    private int highestSeqNum = 0;
    public ConnectionObject connObjInt = null;
    public DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
    
    
    //quit is if manually pressing stop button
    //forceQuit is if invalid session
    Boolean quit = false;
    Boolean forceQuit = false;
    
    //create logger object
    private static Logger logger = signalscroll_app.SignalScroll_App.logger;

    
    @FXML
    public void logoutButtonHandleAction(ActionEvent event) throws IOException{
        quit = true;
        startButton.setVisible(true);   
        stopButton.setVisible(false);   
        clearButton.setVisible(false);   
        taskPickerComboBox.setDisable(false);
        searchTextField.setDisable(false);
        connObjInt.logout(VALID_SN, VALID_PW);        
        
        logger.info("Logout button pressed; Signal Scroll process stopped and user logged out.");
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent mainControllerParent = (Parent) fxmlLoader.load();
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(new Scene(mainControllerParent));

            FXMLDocumentController mainController = fxmlLoader.<FXMLDocumentController>getController();

            window.show();
            
        } catch (Exception ex) {
            logger.severe(ex.toString());
        }
    }
    
    @FXML
    public void taskPickerComboBoxHandleAction(ActionEvent event) throws IOException{
        logger.info("Task List Changed - selected " + taskPickerComboBox.getSelectionModel().getSelectedItem());
    }
    
    @FXML
    public void invalidSessionLoadLogin() {
        quit = true;
        forceQuit = true;
        startButton.setVisible(true);       
        stopButton.setVisible(false);  
        clearButton.setVisible(false);  
        taskPickerComboBox.setDisable(false);
        searchTextField.setDisable(false);
        
        logger.info("Invalid session; signal Scroll process stopped and load login screen.");
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent mainControllerParent = (Parent) fxmlLoader.load();
            Stage window = (Stage)anchorPane.getScene().getWindow();
            window.setScene(new Scene(mainControllerParent));

            FXMLDocumentController mainController = fxmlLoader.<FXMLDocumentController>getController();

            window.show();
            
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        
    }
    
    @FXML
    public void startButtonHandleAction(ActionEvent event) throws IOException{
        
        quit = false;
        forceQuit = false;
        startButton.setVisible(false);
        stopButton.setVisible(true);    
        clearButton.setVisible(false);    
        taskPickerComboBox.setDisable(true);
        searchTextField.setDisable(true);
        
        logger.info("Start Scroll button pressed.");
        try {logger.info("Passed Task Parameter: " + taskPickerComboBox.getValue().toString());} 
        catch (Exception ex){logger.info("Passed Task Parameter: All");}
        
        logger.info("Passed Search Parameter: " + searchTextField.getText());
        logger.info("Signal log started");
        signalLogTable.refresh();
        callSignalLog();
    }
    
    @FXML
    public void stopButtonHandleAction(ActionEvent event) throws IOException{
        
        quit = true;
        forceQuit = false;
        startButton.setVisible(true);
        stopButton.setVisible(false);    
        clearButton.setVisible(true);    
        taskPickerComboBox.setDisable(false);
        searchTextField.setDisable(false);
        logger.info("Stop Scroll button pressed; signal log stopped");
 
    }
    
    @FXML
    public void clearButtonHandleAction(ActionEvent event) throws IOException{
        
        signalLogTable.getItems().clear();
        signalLogTable.refresh();
        logger.info("Clear Scroll button pressed; signal log contents reset");
        
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colTask.setCellValueFactory(new PropertyValueFactory<>("taskDesc"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("convertedDate"));
        colXmit.setCellValueFactory(new PropertyValueFactory<>("xmit"));
        colEventCode.setCellValueFactory(new PropertyValueFactory<>("eventDesc"));
        colSignalCode.setCellValueFactory(new PropertyValueFactory<>("signalCode"));
        colPoint.setCellValueFactory(new PropertyValueFactory<>("point"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colSiteName.setCellValueFactory(new PropertyValueFactory<>("siteName"));
        colRawMessage.setCellValueFactory(new PropertyValueFactory<>("rawMessage"));
        
        clearButton.setVisible(false);
        
    }   
        
    public void loadTaskList() {
        
        logger.info("Loading task list from stages procedure call");
        
        taskPickerComboBox.getItems().add(0, "All");
        
        JSONObject getReq = connObjInt.taskList(VALID_SN, VALID_PW);
        
        //check for successful return
        JSONArray resultSet = getReq.getJSONArray("ResultSet");    
        int resultSetLen = resultSet.length();

        if (resultSetLen > 0) {
            
            //if CS is AMS do this list
            if (CENTRAL_STATION.compareTo("AMS") == 0) {
                for (int i = 0; i < resultSetLen; i++) {

                    int size = taskPickerComboBox.getItems().size();

                    JSONObject newTask = resultSet.getJSONObject(i);
                    Integer taskNum = newTask.getInt("TaskNum");
                    String taskDesc = newTask.getString("TaskDescription"); 

                    if (taskNum == 22) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 

                    }
                    else if (taskNum >= 300 && taskNum <= 9200) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 
                    }
                    else {}

                }
            }
            
            //if CS is QR do this list
            else {
                for (int i = 0; i < resultSetLen; i++) {

                    int size = taskPickerComboBox.getItems().size();

                    JSONObject newTask = resultSet.getJSONObject(i);
                    Integer taskNum = newTask.getInt("TaskNum");
                    String taskDesc = newTask.getString("TaskDescription"); 

                    if (taskNum == 18) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 

                    }
                    else if (taskNum == 21) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 

                    }
                    else if (taskNum == 28) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 

                    }
                    else if (taskNum >= 190 && taskNum <= 9200) {
                        taskPickerComboBox.getItems().add(size, "" + taskNum + " - " + taskDesc); 
                    }
                    else {}

                }
            }
            
        }
        
        logger.info("Task list loaded and populated");
    
    }
    
    
    public void callSignalLog(){
        
        new Thread(() -> {
            
            while (!quit) {
                
                String search = "";
                String task = "";
                
                try {search = searchTextField.getText();} catch (Exception ex){}
                try {task = taskPickerComboBox.getValue().toString();} catch (Exception ex){}
                
                DateFormat dfHHmm = new SimpleDateFormat("HHmm");
                DateFormat dfStartDay = new SimpleDateFormat("yyyy/MM/dd 00:00:00");
                Date currentDateTime = new Date(System.currentTimeMillis());
                String outputDateStartDay = dfStartDay.format(currentDateTime);
                String passedStartHHMM = "";

                Date date2 = new Date();
                long newMilli = 0;
                long milliTimeZoneOffset = 18000000; //offset 5 hours

                try {
                    date2 = dfStartDay.parse(outputDateStartDay);
                    newMilli = date2.getTime() - milliTimeZoneOffset;
                    passedStartHHMM = dfHHmm.format(currentDateTime);
                } catch (ParseException ex) {
                    logger.severe("Parse Exception: " + ex.toString());
                } 
                
                
                /********** debug mode *********/
                //date2 = dfStartDay.parse("2021/05/14 08:00:00");
                //newMilli = 1620950400000L;
                //passedStartHHMM = "0255";
                /*******************************/
                
                JSONObject getReq = connObjInt.signalLog(VALID_SN, VALID_PW, newMilli, passedStartHHMM, search, task);
                            
                //get ErrorTypeNum value - 123 is 0 rows returned, 124 is X rows returned
                Integer errorCode = getReq.getInt("ErrorTypeNum");
                
                //checks for a valid session.. if valid, it continues
                if (errorCode == 123 || errorCode == 124) {
                    
                    //check for successful return
                    JSONArray resultSet = getReq.getJSONArray("ResultSet");    
                    int resultSetLen = resultSet.length();

                    if (resultSetLen > 0) {

                        for (int i = resultSetLen-1; i >= 0 ; i--) {


                            JSONObject newSite = resultSet.getJSONObject(i);
                            Integer seqNum = newSite.getInt("SeqNumBig");

                            if (seqNum > highestSeqNum) {

                                //if signal lob table is over 500 items, remove item to keep list small
                                if (signalLogTable.getItems().size() > 500) {
                                    signalLogTable.getItems().remove(0);
                                }

                                highestSeqNum = seqNum;

                                signalLogTable.setRowFactory(tv -> new TableRow<Signal>(){

                                    @Override
                                    protected void updateItem(Signal item, boolean empty) {

                                        try {
                                            super.updateItem(item,empty);

                                            String eventNum = item.getEventNum();
                                            
                                            //if chosen CS is AMS do this styling
                                            if (CENTRAL_STATION.compareTo("AMS") == 0) {
                                                switch (eventNum) {
                                                    case "001": case "020":
                                                        getStyleClass().clear();
                                                        getStyleClass().add("alarm-type");
                                                        getStyleClass().add("fire");
                                                        break;
                                                    case "002": case "016":  case "017": case "034": 
                                                        getStyleClass().clear();
                                                        getStyleClass().add("panic");
                                                        getStyleClass().add("alarm-type");
                                                        break;
                                                    case "003": case "019":  case "061": 
                                                        getStyleClass().clear();
                                                        getStyleClass().add("burg");
                                                        getStyleClass().add("alarm-type");
                                                        break;
                                                    case "004": 
                                                        getStyleClass().clear();
                                                        getStyleClass().add("medic");
                                                        getStyleClass().add("alarm-type");
                                                        break;

                                                    default:
                                                        getStyleClass().clear();
                                                        getStyleClass().add("cell");
                                                        getStyleClass().add("indexed-cell");
                                                        getStyleClass().add("table-row-cell");

                                                }
                                            }
                                            
                                            //if chosen CS is QR do this styling
                                            else {
                                                switch (eventNum) {
                                                    case "001": case "024":  case "030":  case "110":  case "111": 
                                                    case "112": case "113":  case "114":  case "115": 
                                                    case "116": case "117":  case "024N":  case "024Pro": 
                                                    case "FA": case "Fire":  case "GA":  
                                                        getStyleClass().clear();
                                                        getStyleClass().add("alarm-type");
                                                        getStyleClass().add("fireQR");
                                                        break;
                                                    case "002": case "016":  case "017":
                                                        getStyleClass().clear();
                                                        getStyleClass().add("panicQR");
                                                        getStyleClass().add("alarm-type");
                                                        break;
                                                    case "003": case "130":  case "131": 
                                                    case "132": case "133":  case "134": 
                                                    case "003D": case "003V":  case "BA":
                                                        getStyleClass().clear();
                                                        getStyleClass().add("burgQR");
                                                        getStyleClass().add("alarm-type");
                                                        break;
                                                    case "004": case "031": case "004D": 
                                                        getStyleClass().clear();
                                                        getStyleClass().add("medicQR");
                                                        getStyleClass().add("alarm-type");
                                                        break;
                                                    case "019": case "026": case "027": 
                                                    case "028": case "029": case "026G": 
                                                        getStyleClass().clear();
                                                        getStyleClass().add("criticalQR");
                                                        getStyleClass().add("alarm-type");
                                                        break;

                                                    default:
                                                        getStyleClass().clear();
                                                        getStyleClass().add("cell");
                                                        getStyleClass().add("indexed-cell");
                                                        getStyleClass().add("table-row-cell");

                                                }
                                            }
                                        }
                                        catch (NullPointerException ex) {
                                            //nothing to do here.. null is to be expected sometimes
                                        }

                                    }
                                });

                                signalLogTable.getItems().add(new Signal(newSite, CENTRAL_STATION));

                                int lastRow = signalLogTable.getItems().size();
                                
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        TableViewSelectionModel selectionModel = signalLogTable.getSelectionModel();
                                        signalLogTable.scrollTo(lastRow);
                                        signalLogTable.scrollToColumnIndex(0);
                                        selectionModel.select(lastRow, colTask);
                                    }
                                });

                            }
                        }
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        logger.severe("Interrupted Exception while sleeping: " + ex.toString());
                    }
                    
                }
                
                //invalid session - exit the loop and thread.
                else {
                    logger.info("ErrorTypeNum:" + errorCode);
                    logger.info("UserErrorMessage: " + getReq.get("UserErrorMessage").toString());
                    logger.info("Invalid session. Exit Signal Scroll loop");
                    quit = true;
                    forceQuit = true;
                }
                
                
                
          
            }
            
            //only reach this step IF the loop ended
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    
                    //if forceQuit true means there was an invalid session and load login screen
                    //otherwise it was a manual quit by user and login screen doesn't need to 
                    //load but stay internal
                    if (forceQuit) {
                        invalidSessionLoadLogin();
                    }
                    
                }
            });
            
        }).start();
        
        
        
    }
   
    public void acceptValidatedSessionInfo (String vSN, String vPW, String cs, String iis) {
        VALID_SN = vSN;
        VALID_PW = vPW;
        CENTRAL_STATION = cs;
        IIS_SERVER = iis;
        connObjInt = new ConnectionObject(cs, iis);
    }
    
    public void manualCloseWindow() {
        quit = true;
        startButton.setVisible(true);
        stopButton.setVisible(false);
        clearButton.setVisible(false);
        connObjInt.logout(VALID_SN, VALID_PW);
        logger.info("Window closed; Signal Scroll process stopped and user logged out.");
        Platform.exit();
    }
    
    private String checkIfNull (JSONObject json, String key) {
        return json.isNull(key) ? "" : json.optString(key);
    }
    
    
}
