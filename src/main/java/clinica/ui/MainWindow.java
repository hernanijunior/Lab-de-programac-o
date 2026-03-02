package clinica.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class MainWindow {

    private final Stage stage;

    public MainWindow(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-font-size: 13; -fx-tab-min-width: 120;");

        tabPane.getTabs().addAll(
            makeTab("🏥 Ambulatórios", new AmbulatorioTab().build()),
            makeTab("👨‍⚕️ Médicos",     new MedicoTab().build()),
            makeTab("🧑‍🤝‍🧑 Pacientes",   new PacienteTab().build()),
            makeTab("👷 Funcionários",  new FuncionarioTab().build()),
            makeTab("📅 Consultas",     new ConsultaTab().build()),
            makeTab("📊 Relatórios",    new RelatoriosTab().build())
        );

        BorderPane root = new BorderPane();
        root.setTop(header());
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: " + UiHelper.COLOR_BG + ";");

        Scene scene = new Scene(root, 1100, 720);
        stage.setTitle("Sistema de Clínica Médica - UFMT");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private Tab makeTab(String title, javafx.scene.Node content) {
        Tab t = new Tab(title);
        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color: " + UiHelper.COLOR_BG + ";");
        t.setContent(sp);
        return t;
    }

    private HBox header() {
        Label title = new Label("Sistema de Clínica Médica");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        Label sub = new Label("Trabalho de Lab de banco de dados");
        sub.setTextFill(Color.web("#BBDEFB"));
        sub.setFont(Font.font("System", 12));

        VBox vb = new VBox(2, title, sub);

        HBox hb = new HBox(vb);
        hb.setPadding(new Insets(12, 20, 12, 20));
        hb.setStyle("-fx-background-color: " + UiHelper.COLOR_PRIMARY + ";");
        return hb;
    }
}
