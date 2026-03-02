package clinica.ui;

import clinica.dao.ConsultaDAO;
import clinica.model.Consulta;
import clinica.model.Medico;
import clinica.model.Paciente;
import clinica.store.AppStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class ConsultaTab {

    private final ConsultaDAO dao = new ConsultaDAO();

    private TableView<Consulta> table;
    private ComboBox<Medico> cbMedico, cbMedicoRem, cbMedicoRemar;
    private ComboBox<Paciente> cbPaciente, cbPacienteRemar;
    private DatePicker dpData, dpDataRemar, dpNovaData;
    private TextField tfHora, tfNovaHora, tfHoraRem;

    public VBox build() {
        table = UiHelper.table();

        TableColumn<Consulta, String> cMed = UiHelper.col("Médico");
        TableColumn<Consulta, String> cPac = UiHelper.col("Paciente");
        TableColumn<Consulta, String> cData = UiHelper.col("Data");
        TableColumn<Consulta, String> cHora = UiHelper.col("Hora");

        cMed.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNomeMedico()));
        cPac.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNomePaciente()));
        cData.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getDataConsulta())));
        cHora.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getHora())));

        table.getColumns().addAll(cMed, cPac, cData, cHora);
        table.setPrefHeight(240);

        // ── CADASTRO ──
        cbMedico = new ComboBox<>(AppStore.medicos);
        cbPaciente = new ComboBox<>(AppStore.pacientes);
        dpData = new DatePicker();
        tfHora = UiHelper.field("HH:MM (ex: 14:00)");

        cbMedico.setPrefWidth(200);
        cbPaciente.setPrefWidth(200);
        dpData.setPrefWidth(160);

        GridPane formCad = UiHelper.formGrid();
        formCad.addRow(0, new Label("Médico:"), cbMedico, new Label("Paciente:"), cbPaciente);
        formCad.addRow(1, new Label("Data:"), dpData, new Label("Hora:"), tfHora);

        Button btnAgendar = UiHelper.btnPrimary("📅 Agendar Consulta");
        btnAgendar.setOnAction(e -> agendar());

        Button btnVerMedico = UiHelper.btnSuccess("Ver por Médico");
        btnVerMedico.setOnAction(e -> {
            Medico m = cbMedico.getValue();
            if (m == null) {
                carregar();
                return;
            }
            table.setItems(FXCollections.observableArrayList(dao.listarPorMedico(m.getCodm())));
        });
        Button btnVerPac = UiHelper.btnSuccess("Ver por Paciente");
        btnVerPac.setOnAction(e -> {
            Paciente p = cbPaciente.getValue();
            if (p == null) {
                carregar();
                return;
            }
            table.setItems(FXCollections.observableArrayList(dao.listarPorPaciente(p.getCodp())));
        });
        Button btnTodas = UiHelper.btnSecondary("Ver Todas");
        btnTodas.setOnAction(e -> carregar());

        // ── REMARCAÇÃO ──
        cbMedicoRemar = new ComboBox<>(AppStore.medicos);
        cbPacienteRemar = new ComboBox<>(AppStore.pacientes);
        dpDataRemar = new DatePicker();
        tfNovaHora = UiHelper.field("Nova hora HH:MM");
        dpNovaData = new DatePicker();
        TextField tfHoraOriginal = UiHelper.field("Hora original HH:MM");

        cbMedicoRemar.setPrefWidth(200);
        cbPacienteRemar.setPrefWidth(200);

        GridPane formRemar = UiHelper.formGrid();
        formRemar.addRow(0, new Label("Médico:"), cbMedicoRemar, new Label("Paciente:"), cbPacienteRemar);
        formRemar.addRow(1, new Label("Data original:"), dpDataRemar, new Label("Hora original:"), tfHoraOriginal);
        formRemar.addRow(2, new Label("Nova data:"), dpNovaData, new Label("Nova hora:"), tfNovaHora);

        Button btnRemarcar = UiHelper.btnPrimary("🔄 Remarcar");
        btnRemarcar.setOnAction(e -> remarcar(tfHoraOriginal));

        // ── REMOÇÃO ──
        cbMedicoRem = new ComboBox<>(AppStore.medicos);
        tfHoraRem = UiHelper.field("Horário HH:MM (para remover por hora)");
        cbMedicoRem.setPrefWidth(200);

        GridPane formRem = UiHelper.formGrid();
        formRem.addRow(0, new Label("Remover por Médico:"), cbMedicoRem);
        formRem.addRow(1, new Label("Remover por Horário:"), tfHoraRem);

        Button btnRemPorMedico = UiHelper.btnDanger("🗑 Remover por Médico");
        Button btnRemPorHora = UiHelper.btnDanger("🗑 Remover por Horário");
        Button btnRemSelecionada = UiHelper.btnDanger("🗑 Remover Selecionada");

        btnRemPorMedico.setOnAction(e -> removerPorMedico());
        btnRemPorHora.setOnAction(e -> removerPorHora());
        btnRemSelecionada.setOnAction(e -> removerSelecionada());

        VBox layout = new VBox(12,
                UiHelper.card("Agendar Consulta", formCad,
                        UiHelper.buttonBar(btnAgendar, btnVerMedico, btnVerPac, btnTodas)),
                UiHelper.card("Remarcar Consulta", formRemar, UiHelper.buttonBar(btnRemarcar)),
                UiHelper.card("Remover Consultas", formRem,
                        UiHelper.buttonBar(btnRemPorMedico, btnRemPorHora, btnRemSelecionada)),
                UiHelper.card("Consultas Agendadas", table));
        layout.setPadding(new Insets(16));
        carregar();
        return layout;
    }

    private void agendar() {
        try {
            Medico m = cbMedico.getValue();
            Paciente p = cbPaciente.getValue();
            if (m == null || p == null || dpData.getValue() == null || tfHora.getText().trim().isEmpty()) {
                UiHelper.error("Preencha todos os campos.");
                return;
            }
            Consulta c = new Consulta(m.getCodm(), p.getCodp(),
                    dpData.getValue(), LocalTime.parse(tfHora.getText().trim()));
            dao.inserir(c);
            UiHelper.info("Consulta agendada!");
            carregar();
        } catch (DateTimeParseException ex) {
            UiHelper.error("Formato de hora inválido. Use HH:MM (ex: 14:00)");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void remarcar(TextField tfHoraOriginal) {
        try {
            Medico m = cbMedicoRemar.getValue();
            Paciente p = cbPacienteRemar.getValue();
            if (m == null || p == null || dpDataRemar.getValue() == null ||
                    tfHoraOriginal.getText().trim().isEmpty() ||
                    dpNovaData.getValue() == null || tfNovaHora.getText().trim().isEmpty()) {
                UiHelper.error("Preencha todos os campos de remarcação.");
                return;
            }
            dao.remarcar(m.getCodm(), p.getCodp(),
                    dpDataRemar.getValue(), LocalTime.parse(tfHoraOriginal.getText().trim()),
                    dpNovaData.getValue(), LocalTime.parse(tfNovaHora.getText().trim()));
            UiHelper.info("Consulta remarcada com sucesso!");
            carregar();
        } catch (DateTimeParseException ex) {
            UiHelper.error("Formato de hora inválido. Use HH:MM.");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void removerPorMedico() {
        Medico m = cbMedicoRem.getValue();
        if (m == null) {
            UiHelper.error("Selecione um médico.");
            return;
        }
        if (UiHelper.confirm("Remover TODAS as consultas do médico " + m.getNome() + "?")) {
            try {
                int n = dao.removerPorMedico(m.getCodm());
                UiHelper.info(n + " consulta(s) removida(s).");
                carregar();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void removerPorHora() {
        String h = tfHoraRem.getText().trim();
        if (h.isEmpty()) {
            UiHelper.error("Informe o horário.");
            return;
        }
        try {
            LocalTime hora = LocalTime.parse(h);
            if (UiHelper.confirm("Remover TODAS as consultas no horário " + h + "?")) {
                int n = dao.removerPorHorario(hora);
                UiHelper.info(n + " consulta(s) removida(s).");
                carregar();
            }
        } catch (DateTimeParseException ex) {
            UiHelper.error("Formato de hora inválido. Use HH:MM.");
        }
    }

    private void removerSelecionada() {
        Consulta sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("Selecione uma consulta na tabela.");
            return;
        }
        if (UiHelper.confirm("Remover consulta selecionada?")) {
            try {
                dao.remover(sel.getCodm(), sel.getCodp(), sel.getDataConsulta(), sel.getHora());
                UiHelper.info("Consulta removida.");
                carregar();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void carregar() {
        try {
            table.setItems(FXCollections.observableArrayList(dao.listarTodas()));
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }
}
