package signalscroll_app;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Trey
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Pane bkgrndPane;
    
    @FXML
    private TextField unField;
    
    @FXML
    private TextField pwField;
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label serverComboBoxLabel;
    
    @FXML
    private Label unLabel;
    
    @FXML
    private Label pwLabel;
    
    @FXML
    private Label signalScrollLabel;
        
    @FXML
    private Label errorMessageLabel;
        
    @FXML
    private ComboBox serverComboBox;
    
    @FXML
    private Button loginButton;
    
    //either "AMS" or "QR"
    private String CENTRAL_STATION = "AMS";
    //private String CENTRAL_STATION = "QR";
    
    private String IIS_SERVER = "";
    
    public ConnectionObject connObj = null;
    
    
    //create logger object
    private static Logger logger = signalscroll_app.SignalScroll_App.logger;
    
    @FXML
    public void onEnter(ActionEvent ae) throws IOException{
       handleLoginButtonAction(ae);
    }
    
    @FXML
    private void handleLoginButtonAction(ActionEvent event) throws IOException {
        
        String un = unField.getText();
        String pw = pwField.getText();
        SingleSelectionModel server = serverComboBox.getSelectionModel();
        
        String validSN = "";
        String validPW = "";
        
        String[] credentials = null;
        
        if (server.isEmpty()) {
            errorMessageLabel.setText("Please choose a server.");
            errorMessageLabel.setVisible(true);
            serverComboBox.requestFocus();
        }
        
        else if (un.isEmpty() || pw.isEmpty()) {
            errorMessageLabel.setText("Please enter a username and password.");
            errorMessageLabel.setVisible(true);
            unField.requestFocus();
        }
        
        
        else {
            
            errorMessageLabel.setVisible(false);
            
            connObj = new ConnectionObject(CENTRAL_STATION, IIS_SERVER);

            /**********************************/
            credentials = connObj.login(un, pw);

            //check if valid login
            if (credentials[0].compareTo("true") == 0) {

                validSN = credentials[1];
                validPW = credentials[2];

                System.out.println("" + validSN + "\n" + validPW);
                
                logger.info("Successful login. Session Num: " + validSN + "; Session Password: " + validPW + ".");

                //load new scene and pass validated info parameters to be used for future logged in methods
                try {
                    
                    
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("internal.fxml"));
                    Parent internalControllerParent = (Parent) fxmlLoader.load();
                    Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
                    window.setScene(new Scene(internalControllerParent));

                    InternalController controller = fxmlLoader.<InternalController>getController();
                    controller.acceptValidatedSessionInfo(validSN, validPW, CENTRAL_STATION, IIS_SERVER);
                    controller.loadTaskList();
                                        
                    window.setOnCloseRequest(e -> { controller.manualCloseWindow(); });

                    window.show();
                    
                    

                } catch(Exception e) {
                    e.printStackTrace();
                    logger.severe(e.toString());
                }

            }

            else {
                if (credentials[1].compareTo("This account is locked. Contact an administrator to unlock.") == 0){
                    
                    errorMessageLabel.setText("Your username is locked.");
                    logger.info("Unsuccesful login. Account is locked.");

                }

                else if (credentials[1].compareTo("Your password has expired. Change your password to log in.") == 0) {
                    
                    errorMessageLabel.setText("Your password has expired. Please update your password.");
                    logger.info("Unsuccesful login. Password is expired.");
                    
                }

                else {
                    
                    errorMessageLabel.setText("Invalid username or password.  Please try again.");
                    logger.info("Unsuccesful login. Invalid username or password.");
                    
                }

                unField.requestFocus();
                errorMessageLabel.setVisible(true);

            }
        }

    }
    
    @FXML
    private void handleServerComboBoxChange(ActionEvent event) throws IOException {
        
        int chosenServerIndex = serverComboBox.getSelectionModel().getSelectedIndex();
        IIS_SERVER = serverComboBox.getItems().get(chosenServerIndex).toString();
        logger.log(Level.INFO, "Chose server " + serverComboBox.getItems().get(chosenServerIndex).toString());

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        logger.log(Level.INFO, "Load login screen");
        
        errorMessageLabel.setVisible(false);
        
        String version = this.getClass().getPackage().getImplementationVersion();
        versionLabel.setText("v" + version);
        
        //adding server for combobox dropdowns
        if (CENTRAL_STATION.compareTo("AMS") == 0) {
            serverComboBox.getItems().add("192.0.0.6");
            serverComboBox.getItems().add("192.0.0.7");
        
        }
        else {
            serverComboBox.getItems().add("192.168.103.88");
            serverComboBox.getItems().add("192.168.103.89");
        }
        
    }    
    
}
