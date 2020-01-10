/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ecgviewer;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {
	@FXML public TextField jTextFieldFileName;
	public Parent root;
	@Override
	public void start(Stage stage) throws IOException {
		
		FXMLLoader loader =new FXMLLoader(getClass().getResource("Main.fxml"));
		root=(Parent)loader.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());
                
                stage.setTitle("ECG Graph Viewer");
                stage.setScene(scene);    
//                stage.setMaximized(true);
//                stage.resizableProperty().setValue(Boolean.FALSE);
                stage.show();
                stage.sizeToScene();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
}
