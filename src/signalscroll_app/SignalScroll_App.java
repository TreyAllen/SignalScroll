package signalscroll_app;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * 
 * @author Trey
 * 
 */
public class SignalScroll_App extends Application {
    
    //create logger object
    public static Logger logger = Logger.getLogger(SignalScroll_App.class.getName());
    
    @Override
    public void start(Stage stage) throws Exception {
        
        stage.setTitle("Signal Scroll");
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.getIcons().add(new Image(getClass().getResourceAsStream("images/signalscroll_maroon_stroke.png")));

        stage.setScene(scene);
        
        stage.show();
    }

    public static void main(String[] args) {    
        
        final InputStream inputStream = SignalScroll_App.class.getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
            Logger.getAnonymousLogger().severe(e.getMessage());
        }
        
        logger.setLevel(Level.ALL);

        launch(args);
    }
    
    
}
