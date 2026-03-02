package clinica.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

public class UiHelper {

    public static final String COLOR_PRIMARY   = "#1565C0";
    public static final String COLOR_SECONDARY = "#E3F2FD";
    public static final String COLOR_SUCCESS   = "#2E7D32";
    public static final String COLOR_DANGER    = "#C62828";
    public static final String COLOR_BG        = "#F5F5F5";

    public static Label title(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 16));
        l.setTextFill(Color.web(COLOR_PRIMARY));
        l.setPadding(new Insets(0, 0, 8, 0));
        return l;
    }

    public static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#333333"));
        return l;
    }

    public static Button btnPrimary(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: #0D47A1; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;"));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + COLOR_PRIMARY + "; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;"));
        return b;
    }

    public static Button btnDanger(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + COLOR_DANGER + "; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;");
        return b;
    }

    public static Button btnSuccess(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + COLOR_SUCCESS + "; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;");
        return b;
    }

    public static Button btnSecondary(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #757575; -fx-text-fill: white; " +
                   "-fx-font-weight: bold; -fx-padding: 6 16; -fx-cursor: hand; -fx-background-radius: 4;");
        return b;
    }

    public static TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(180);
        return tf;
    }

    public static GridPane formGrid() {
        GridPane g = new GridPane();
        g.setHgap(10); g.setVgap(8);
        g.setPadding(new Insets(10, 0, 10, 0));
        return g;
    }

    public static HBox buttonBar(Button... buttons) {
        HBox hb = new HBox(8, buttons);
        hb.setPadding(new Insets(4, 0, 4, 0));
        return hb;
    }

    public static <T> TableView<T> table() {
        TableView<T> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setStyle("-fx-font-size: 13;");
        return table(tv);
    }

    private static <T> TableView<T> table(TableView<T> tv) {
        return tv;
    }

    public static <T> TableColumn<T, String> col(String title) {
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setStyle("-fx-alignment: CENTER-LEFT;");
        return c;
    }

    public static void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    public static void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Erro"); a.setContentText(msg); a.showAndWait();
    }

    public static boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Confirmação"); a.setContentText(msg);
        return a.showAndWait().filter(r -> r == ButtonType.OK).isPresent();
    }

    public static VBox card(String title, javafx.scene.Node... content) {
        VBox box = new VBox(6);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color: white; -fx-border-color: #DDDDDD; " +
                     "-fx-border-radius: 4; -fx-background-radius: 4;");
        box.getChildren().add(UiHelper.title(title));
        box.getChildren().addAll(content);
        return box;
    }
}
