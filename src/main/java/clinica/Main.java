package clinica;

import clinica.database.Database;
import clinica.ui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        clinica.store.AppStore.recarregarTudo();
        MainWindow window = new MainWindow(primaryStage);
        window.show();
    }

    @Override
    public void stop() {
        Database.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
