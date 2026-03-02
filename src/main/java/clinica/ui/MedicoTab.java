package clinica.ui;

import clinica.dao.MedicoDAO;
import clinica.dao.ConsultaDAO;
import clinica.model.Medico;
import clinica.store.AppStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MedicoTab {

    private final MedicoDAO dao = new MedicoDAO();
    private TableView<Medico> table;
    private TextField tfCodm, tfNome, tfIdade, tfEsp, tfRg, tfCidade, tfNroa;

    public VBox build() {
        table = UiHelper.table();

        TableColumn<Medico, String> cCod = UiHelper.col("Cód");
        TableColumn<Medico, String> cNome = UiHelper.col("Nome");
        TableColumn<Medico, String> cIdade = UiHelper.col("Idade");
        TableColumn<Medico, String> cEsp = UiHelper.col("Especialidade");
        TableColumn<Medico, String> cRg = UiHelper.col("RG");
        TableColumn<Medico, String> cCid = UiHelper.col("Cidade");
        TableColumn<Medico, String> cAmb = UiHelper.col("Amb.");

        cCod.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCodm())));
        cNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));
        cIdade.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdade())));
        cEsp.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getEspecialidade() != null ? d.getValue().getEspecialidade() : ""));
        cRg.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRg() != null ? String.valueOf(d.getValue().getRg()) : ""));
        cCid.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getCidade() != null ? d.getValue().getCidade() : ""));
        cAmb.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getNroa() != null ? String.valueOf(d.getValue().getNroa()) : "-"));

        table.getColumns().addAll(cCod, cNome, cIdade, cEsp, cRg, cCid, cAmb);
        table.setPrefHeight(280);

        tfCodm = UiHelper.field("Código (vazio = auto)");
        tfNome = UiHelper.field("Nome completo");
        tfIdade = UiHelper.field("Idade");
        tfEsp = UiHelper.field("Especialidade");
        tfRg = UiHelper.field("RG (numérico)");
        tfCidade = UiHelper.field("Cidade");
        tfNroa = UiHelper.field("Nº Ambulatório (opcional)");

        GridPane form = UiHelper.formGrid();
        form.addRow(0, new Label("Código:"), tfCodm, new Label("Nome:"), tfNome);
        form.addRow(1, new Label("Idade:"), tfIdade, new Label("Especialidade:"), tfEsp);
        form.addRow(2, new Label("RG:"), tfRg, new Label("Cidade:"), tfCidade);
        form.addRow(3, new Label("Nº Ambulatório:"), tfNroa);

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
                UiHelper.card("Médicos — Cadastro", form,
                        UiHelper.buttonBar(btnNovo, btnSalvar, btnRemover, btnBuscar)),
                UiHelper.card("Lista de Médicos", table));
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
            Medico m = new Medico();
            String codStr = tfCodm.getText().trim();
            boolean isNovo = codStr.isEmpty();

            if (isNovo) {
                m.setCodm(dao.obterProximoCodigo());
            } else {
                m.setCodm(Integer.parseInt(codStr));
            }

            m.setNome(tfNome.getText().trim());
            m.setIdade(Integer.parseInt(tfIdade.getText().trim()));
            m.setEspecialidade(tfEsp.getText().trim().isEmpty() ? null : tfEsp.getText().trim());
            m.setRg(tfRg.getText().trim().isEmpty() ? null : Long.parseLong(tfRg.getText().trim()));
            m.setCidade(tfCidade.getText().trim().isEmpty() ? null : tfCidade.getText().trim());
            m.setNroa(tfNroa.getText().trim().isEmpty() ? null : Integer.parseInt(tfNroa.getText().trim()));

            if (isNovo || dao.buscarPorCodigo(m.getCodm()) == null)
                dao.inserir(m);
            else
                dao.atualizar(m);
            UiHelper.info("Médico salvo com sucesso!");
            carregar();
            limpar();
            AppStore.recarregarMedicos();
        } catch (NumberFormatException ex) {
            UiHelper.error("Verifique os campos numéricos (Código, Idade, RG, Nº Ambulatório).");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void remover() {
        Medico sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("Selecione um médico.");
            return;
        }

        int qtd = new ConsultaDAO().contarPorMedico(sel.getCodm());
        String msg = (qtd > 0)
                ? "O médico " + sel.getNome() + " possui " + qtd
                        + " consulta(s) agendada(s). Deseja remover o médico e cancelar todas as consultas?"
                : "Remover médico " + sel.getNome() + "?";

        if (UiHelper.confirm(msg)) {
            try {
                if (qtd > 0)
                    dao.removerComTransacao(sel.getCodm());
                else
                    dao.remover(sel.getCodm());
                UiHelper.info("Médico removido.");
                carregar();
                limpar();
                AppStore.recarregarMedicos();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void buscar() {
        if (tfCodm.getText().trim().isEmpty()) {
            carregar();
            return;
        }
        try {
            Medico m = dao.buscarPorCodigo(Integer.parseInt(tfCodm.getText().trim()));
            if (m != null) {
                table.setItems(FXCollections.observableArrayList(m));
                preencher(m);
            } else
                UiHelper.info("Médico não encontrado.");
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }

    private void preencher(Medico m) {
        tfCodm.setText(String.valueOf(m.getCodm()));
        tfNome.setText(m.getNome());
        tfIdade.setText(String.valueOf(m.getIdade()));
        tfEsp.setText(m.getEspecialidade() != null ? m.getEspecialidade() : "");
        tfRg.setText(m.getRg() != null ? String.valueOf(m.getRg()) : "");
        tfCidade.setText(m.getCidade() != null ? m.getCidade() : "");
        tfNroa.setText(m.getNroa() != null ? String.valueOf(m.getNroa()) : "");
    }

    private void limpar() {
        tfCodm.clear();
        tfNome.clear();
        tfIdade.clear();
        tfEsp.clear();
        tfRg.clear();
        tfCidade.clear();
        tfNroa.clear();
        table.getSelectionModel().clearSelection();
        carregar();
    }
}
