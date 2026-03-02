package clinica.ui;

import clinica.dao.*;
import clinica.model.*;
import clinica.store.AppStore;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class RelatoriosTab {

    private final ConsultaDAO cDao = new ConsultaDAO();
    private final AmbulatorioDAO aDao = new AmbulatorioDAO();
    private final MedicoDAO mDao = new MedicoDAO();
    private final PacienteDAO pDao = new PacienteDAO();
    private final FuncionarioDAO fDao = new FuncionarioDAO();

    public VBox build() {
        VBox layout = new VBox(14);
        layout.setPadding(new Insets(16));

        layout.getChildren().addAll(
                buildRelA(),
                buildRelB(),
                buildRelC(),
                buildRelD(),
                buildRelE(),
                buildRelF(),
                buildRelG(),
                buildRelH(),
                buildRelI());

        return layout;
    }

    // ─── a) Todas as consultas ───
    private VBox buildRelA() {
        TableView<Consulta> tv = UiHelper.table();
        TableColumn<Consulta, String> cMed = UiHelper.col("Médico");
        TableColumn<Consulta, String> cPac = UiHelper.col("Paciente");
        TableColumn<Consulta, String> cData = UiHelper.col("Data");
        TableColumn<Consulta, String> cHora = UiHelper.col("Hora");
        cMed.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomeMedico()));
        cPac.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomePaciente()));
        cData.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getDataConsulta())));
        cHora.setCellValueFactory(
                d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getHora())));
        tv.getColumns().addAll(cMed, cPac, cData, cHora);
        tv.setPrefHeight(180);

        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            try {
                tv.setItems(FXCollections.observableArrayList(cDao.listarTodas()));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        return UiHelper.card("a) Todas as Consultas Médicas", btn, tv);
    }

    // ─── b) Ambulatório com maior capacidade sem médico ───
    private VBox buildRelB() {
        TableView<String[]> tv = tableStrArr(new String[] { "Nº Ambulatório", "Capacidade" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            try {
                List<String[]> res = aDao.relatorioMaiorCapSemMedico();
                tv.setItems(FXCollections.observableArrayList(res));
                if (res.isEmpty())
                    UiHelper.info("Todos os ambulatórios têm médicos atendendo.");
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        return UiHelper.card("b) Ambulatório com Maior Capacidade sem Médico", btn, tv);
    }

    // ─── c) Funcionários com salário > todos de um departamento ───
    private VBox buildRelC() {
        ComboBox<String> cbDepto = new ComboBox<>(AppStore.departamentos);
        cbDepto.setPromptText("Selecione o departamento");
        cbDepto.setPrefWidth(220);

        TableView<String[]> tv = tableStrArr(new String[] { "Nome", "Salário", "Departamento" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            String d = cbDepto.getValue();
            if (d == null) {
                UiHelper.error("Selecione um departamento.");
                return;
            }
            try {
                tv.setItems(FXCollections.observableArrayList(fDao.funcionariosSalarioSuperior(d)));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        HBox hb = new HBox(8, cbDepto, btn);
        return UiHelper.card("c) Funcionários com Salário Superior ao Departamento", hb, tv);
    }

    // ─── d) Salários de todos ou por departamento ───
    private VBox buildRelD() {
        ComboBox<String> cbDepto = new ComboBox<>(AppStore.departamentosComTodos);
        cbDepto.setValue("— Todos os Departamentos —");
        cbDepto.setPrefWidth(220);

        TableView<String[]> tv = tableStrArr(new String[] { "Nome", "Salário", "Departamento" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            String d = cbDepto.getValue();
            String depto = (d == null || d.startsWith("—")) ? null : d;
            try {
                tv.setItems(FXCollections.observableArrayList(fDao.salariosPorDepartamento(depto)));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        HBox hb = new HBox(8, cbDepto, btn);
        return UiHelper.card("d) Salários dos Funcionários", hb, tv);
    }

    // ─── e) Médicos com consultas em uma data ───
    private VBox buildRelE() {
        DatePicker dp = new DatePicker();
        dp.setPromptText("Selecione a data");
        TableView<String[]> tv = tableStrArr(new String[] { "Cód. Médico", "Nome" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            if (dp.getValue() == null) {
                UiHelper.error("Selecione uma data.");
                return;
            }
            try {
                tv.setItems(FXCollections.observableArrayList(mDao.medicosPorData(dp.getValue().toString())));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        HBox hb = new HBox(8, dp, btn);
        return UiHelper.card("e) Médicos com Consultas em uma Data", hb, tv);
    }

    // ─── f) Médicos com consulta com determinado paciente ───
    private VBox buildRelF() {
        ComboBox<Paciente> cbPac = new ComboBox<>(AppStore.pacientes);
        cbPac.setPromptText("Selecione o paciente");
        cbPac.setPrefWidth(220);
        TableView<String[]> tv = tableStrArr(new String[] { "Cód. Médico", "Nome" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            Paciente p = cbPac.getValue();
            if (p == null) {
                UiHelper.error("Selecione um paciente.");
                return;
            }
            try {
                tv.setItems(FXCollections.observableArrayList(mDao.medicosPorPaciente(p.getCodp())));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        HBox hb = new HBox(8, cbPac, btn);
        return UiHelper.card("f) Médicos com Consulta com Determinado Paciente", hb, tv);
    }

    // ─── g) Idades dos médicos e total com mesma idade ───
    private VBox buildRelG() {
        TableView<String[]> tv = tableStrArr(new String[] { "Idade", "Total de Médicos" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            try {
                tv.setItems(FXCollections.observableArrayList(mDao.idadesETotalMedicos()));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        return UiHelper.card("g) Idades dos Médicos e Total por Idade", btn, tv);
    }

    // ─── h) Andares com ambulatórios e média de capacidade ───
    private VBox buildRelH() {
        TableView<String[]> tv = tableStrArr(new String[] { "Andar", "Média de Capacidade" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            try {
                tv.setItems(FXCollections.observableArrayList(aDao.relatorioAndaresMédiaCapacidade()));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        return UiHelper.card("h) Andares e Média de Capacidade dos Ambulatórios", btn, tv);
    }

    // ─── i) Pacientes com consultas antes de todos os horários de uma data ───
    private VBox buildRelI() {
        DatePicker dp = new DatePicker();
        dp.setPromptText("Selecione a data de referência");
        TableView<String[]> tv = tableStrArr(new String[] { "Cód", "Nome", "Idade", "Cidade", "RG", "Problema" });
        Button btn = UiHelper.btnPrimary("▶ Executar");
        btn.setOnAction(e -> {
            if (dp.getValue() == null) {
                UiHelper.error("Selecione uma data.");
                return;
            }
            try {
                tv.setItems(
                        FXCollections.observableArrayList(pDao.pacientesConsultasAnteriores(dp.getValue().toString())));
            } catch (Exception ex) {
                UiHelper.error(ex.getMessage());
            }
        });
        HBox hb = new HBox(8, dp, btn);
        return UiHelper.card("i) Pacientes com Consultas Antes de Todos os Horários da Data", hb, tv);
    }

    /**
     * Cria uma TableView<String[]> com colunas dinâmicas pelos cabeçalhos
     * fornecidos
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TableView<String[]> tableStrArr(String[] headers) {
        TableView<String[]> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tv.setPrefHeight(160);
        for (int i = 0; i < headers.length; i++) {
            final int idx = i;
            TableColumn<String[], String> col = new TableColumn<>(headers[i]);
            col.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                    d.getValue().length > idx ? d.getValue()[idx] : ""));
            tv.getColumns().add(col);
        }
        return tv;
    }
}
