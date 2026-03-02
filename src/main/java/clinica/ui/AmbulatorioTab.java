package clinica.ui;

import clinica.dao.AmbulatorioDAO;
import clinica.dao.MedicoDAO;
import clinica.model.Ambulatorio;
import clinica.store.AppStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class AmbulatorioTab {

    private final AmbulatorioDAO dao = new AmbulatorioDAO();
    private TableView<Ambulatorio> table;
    private TextField tfNroa, tfAndar, tfCapacidade;

    public VBox build() {
        table = UiHelper.table();

        TableColumn<Ambulatorio, String> cNroa = UiHelper.col("Nº Ambulatório");
        cNroa.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getNroa())));

        TableColumn<Ambulatorio, String> cAndar = UiHelper.col("Andar");
        cAndar.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getAndar())));

        TableColumn<Ambulatorio, String> cCap = UiHelper.col("Capacidade");
        cCap.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCapacidade() != null ? String.valueOf(d.getValue().getCapacidade()) : "-"));

        table.getColumns().addAll(cNroa, cAndar, cCap);
        table.setPrefHeight(280);

        // Form
        tfNroa = UiHelper.field("Nº Ambulatório (vazio = auto)");
        tfAndar = UiHelper.field("Andar");
        tfCapacidade = UiHelper.field("Capacidade");

        GridPane form = UiHelper.formGrid();
        form.addRow(0, new Label("Nº Ambulatório:"), tfNroa);
        form.addRow(1, new Label("Andar:"), tfAndar);
        form.addRow(2, new Label("Capacidade:"), tfCapacidade);

        Button btnSalvar = UiHelper.btnPrimary("💾 Salvar");
        Button btnNovo = UiHelper.btnSecondary("✚ Novo");
        Button btnRemover = UiHelper.btnDanger("🗑 Remover");
        Button btnBuscar = UiHelper.btnSuccess("🔍 Buscar por Nº");

        btnNovo.setOnAction(e -> limpar());
        btnSalvar.setOnAction(e -> salvar());
        btnRemover.setOnAction(e -> remover());
        btnBuscar.setOnAction(e -> buscar());

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null)
                preencher(sel);
        });

        VBox layout = new VBox(12,
                UiHelper.card("Ambulatórios — Cadastro", form,
                        UiHelper.buttonBar(btnNovo, btnSalvar, btnRemover, btnBuscar)),
                UiHelper.card("Lista de Ambulatórios", table));
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
            Ambulatorio a = new Ambulatorio();
            String nroaStr = tfNroa.getText().trim();
            boolean isNovo = nroaStr.isEmpty();

            if (isNovo) {
                a.setNroa(dao.obterProximoCodigo());
            } else {
                a.setNroa(Integer.parseInt(nroaStr));
            }

            a.setAndar(Integer.parseInt(tfAndar.getText().trim()));
            String cap = tfCapacidade.getText().trim();
            a.setCapacidade(cap.isEmpty() ? null : Integer.parseInt(cap));

            Ambulatorio existe = dao.buscarPorCodigo(a.getNroa());
            if (isNovo || existe == null)
                dao.inserir(a);
            else
                dao.atualizar(a);

            UiHelper.info("Ambulatório salvo com sucesso!");
            carregar();
            limpar();
            AppStore.recarregarAmbulatorios();
        } catch (NumberFormatException ex) {
            UiHelper.error("Preencha corretamente os campos numéricos.");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void remover() {
        Ambulatorio sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("Selecione um ambulatório na tabela.");
            return;
        }

        int qtd = new MedicoDAO().contarPorAmbulatorio(sel.getNroa());
        String msg = (qtd > 0)
                ? "O ambulatório " + sel.getNroa() + " possui " + qtd
                        + " médico(s) vinculado(s). Deseja remover o ambulatório e desvincular os médicos (nroa ficará nulo)?"
                : "Remover ambulatório " + sel.getNroa() + "?";

        if (UiHelper.confirm(msg)) {
            try {
                if (qtd > 0)
                    dao.removerComTransacao(sel.getNroa());
                else
                    dao.remover(sel.getNroa());
                UiHelper.info("Ambulatório removido.");
                carregar();
                limpar();
                AppStore.recarregarAmbulatorios();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void buscar() {
        String nroaStr = tfNroa.getText().trim();
        if (nroaStr.isEmpty()) {
            carregar();
            return;
        }
        try {
            Ambulatorio a = dao.buscarPorCodigo(Integer.parseInt(nroaStr));
            if (a != null) {
                table.setItems(FXCollections.observableArrayList(a));
                preencher(a);
            } else
                UiHelper.info("Ambulatório não encontrado.");
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }

    private void preencher(Ambulatorio a) {
        tfNroa.setText(String.valueOf(a.getNroa()));
        tfAndar.setText(String.valueOf(a.getAndar()));
        tfCapacidade.setText(a.getCapacidade() != null ? String.valueOf(a.getCapacidade()) : "");
    }

    private void limpar() {
        tfNroa.clear();
        tfAndar.clear();
        tfCapacidade.clear();
        table.getSelectionModel().clearSelection();
        carregar();
    }
}
