/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation;

import BurningFX.BurningFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import navigation.Main.MainCommandProvider;
import navigation.Main.MainView;

/**
 *
 * @author s_stanis
 */
public class Navigation extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        BurningFX.setUp(new MainCommandProvider());
        Scene scene = new Scene(new MainView().getView());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        BurningFX.stopAll();
    }

}
