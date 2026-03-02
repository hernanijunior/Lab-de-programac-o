package clinica.ui;

import clinica.dao.PacienteDAO;
import clinica.dao.ConsultaDAO;
import clinica.model.Paciente;
import clinica.store.AppStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PacienteTab {

    private final PacienteDAO dao = new PacienteDAO();
    private TableView<Paciente> table;
    private TextField tfCodp, tfNome, tfIdade, tfCidade, tfRg, tfProblema;

    public VBox build() {
        table = UiHelper.table();

        TableColumn<Paciente, String> cCod = UiHelper.col("Cód");
        TableColumn<Paciente, String> cNome = UiHelper.col("Nome");
        TableColumn<Paciente, String> cIdade = UiHelper.col("Idade");
        TableColumn<Paciente, String> cCidade = UiHelper.col("Cidade");
        TableColumn<Paciente, String> cRg = UiHelper.col("RG");
        TableColumn<Paciente, String> cProblema = UiHelper.col("Problema");

        cCod.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCodp())));
        cNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));
        cIdade.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdade())));
        cCidade.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getCidade() != null ? d.getValue().getCidade() : ""));
        cRg.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRg() != null ? String.valueOf(d.getValue().getRg()) : ""));
        cProblema.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProblema()));

        table.getColumns().addAll(cCod, cNome, cIdade, cCidade, cRg, cProblema);
        table.setPrefHeight(280);

        tfCodp = UiHelper.field("Código (vazio = auto)");
        tfNome = UiHelper.field("Nome");
        tfIdade = UiHelper.field("Idade");
        tfCidade = UiHelper.field("Cidade");
        tfRg = UiHelper.field("RG (numérico)");
        tfProblema = UiHelper.field("Problema");

        GridPane form = UiHelper.formGrid();
        form.addRow(0, new Label("Código:"), tfCodp, new Label("Nome:"), tfNome);
        form.addRow(1, new Label("Idade:"), tfIdade, new Label("Cidade:"), tfCidade);
        form.addRow(2, new Label("RG:"), tfRg, new Label("Problema:"), tfProblema);

        Button btnNovo = UiHelper.btnSecondary("✚ Novo");
        Button btnSalvar = UiHelper.btnPrimary("💾 Salvar");
        Button btnRemover = UiHelper.btnDanger("🗑 Remover");
        Button btnBuscar = UiHelper.btnSuccess("🔍 Buscar por Cód.");

        btnNovo.setOnAction(e -> limpar());
        btnSalvar.setOnAction(e -> salvar());
        btnRemover.setOnAction(e -> remover());
        btnBuscar.setOnAction(e -> buscar());

        table.getSelectionModel().selectedItemProperty().addListener((o, old, sel) -> {
            if (sel != null)
                preencher(sel);
        });

        VBox layout = new VBox(12,
                UiHelper.card("Pacientes — Cadastro", form,
                        UiHelper.buttonBar(btnNovo, btnSalvar, btnRemover, btnBuscar)),
                UiHelper.card("Lista de Pacientes", table));
        layout.setPadding(new Insets(16));
        carregar();
        return layout;
    }

    private void carregar() {
        try {
            table.setItems(FXCollections.observableArrayList(dao.listarTodos()));
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }

    private void salvar() {
        try {
            Paciente p = new Paciente();
            String codStr = tfCodp.getText().trim();
            boolean isNovo = codStr.isEmpty();

            if (isNovo) {
                p.setCodp(dao.obterProximoCodigo());
            } else {
                p.setCodp(Integer.parseInt(codStr));
            }

            p.setNome(tfNome.getText().trim());
            p.setIdade(Integer.parseInt(tfIdade.getText().trim()));
            p.setCidade(tfCidade.getText().trim().isEmpty() ? null : tfCidade.getText().trim());
            p.setRg(tfRg.getText().trim().isEmpty() ? null : Long.parseLong(tfRg.getText().trim()));
            p.setProblema(tfProblema.getText().trim());

            if (p.getNome().isEmpty() || p.getProblema().isEmpty()) {
                UiHelper.error("Nome e Problema são obrigatórios.");
                return;
            }

            if (isNovo || dao.buscarPorCodigo(p.getCodp()) == null)
                dao.inserir(p);
            else
                dao.atualizar(p);
            UiHelper.info("Paciente salvo!");
            carregar();
            limpar();
            AppStore.recarregarPacientes();
        } catch (NumberFormatException ex) {
            UiHelper.error("Verifique os campos numéricos.");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void remover() {
        Paciente sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("Selecione um paciente.");
            return;
        }

        int qtd = new ConsultaDAO().contarPorPaciente(sel.getCodp());
        String msg = (qtd > 0)
                ? "O paciente " + sel.getNome() + " possui " + qtd
                        + " consulta(s) agendada(s). Deseja remover o paciente e cancelar todas as consultas?"
                : "Remover paciente " + sel.getNome() + "?";

        if (UiHelper.confirm(msg)) {
            try {
                if (qtd > 0)
                    dao.removerComTransacao(sel.getCodp());
                else
                    dao.remover(sel.getCodp());
                UiHelper.info("Paciente removido.");
                carregar();
                limpar();
                AppStore.recarregarPacientes();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void buscar() {
        if (tfCodp.getText().trim().isEmpty()) {
            carregar();
            return;
        }
        try {
            Paciente p = dao.buscarPorCodigo(Integer.parseInt(tfCodp.getText().trim()));
            if (p != null) {
                table.setItems(FXCollections.observableArrayList(p));
                preencher(p);
            } else
                UiHelper.info("Paciente não encontrado.");
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }

    private void preencher(Paciente p) {
        tfCodp.setText(String.valueOf(p.getCodp()));
        tfNome.setText(p.getNome());
        tfIdade.setText(String.valueOf(p.getIdade()));
        tfCidade.setText(p.getCidade() != null ? p.getCidade() : "");
        tfRg.setText(p.getRg() != null ? String.valueOf(p.getRg()) : "");
        tfProblema.setText(p.getProblema());
    }

    private void limpar() {
        tfCodp.clear();
        tfNome.clear();
        tfIdade.clear();
        tfCidade.clear();
        tfRg.clear();
        tfProblema.clear();
        table.getSelectionModel().clearSelection();
        carregar();
    }
}
