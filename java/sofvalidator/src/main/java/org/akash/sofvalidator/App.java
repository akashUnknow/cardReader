package org.akash.sofvalidator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader=new FXMLLoader(
                getClass().getResource("/fxml/main.fxml")
        );
        Scene scene=new Scene(loader.load(),500,600);
        stage.setTitle("validator");
        stage.setScene(scene);
        stage.show();

    }
}
