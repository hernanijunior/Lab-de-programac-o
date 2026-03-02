package clinica.ui;

import clinica.dao.FuncionarioDAO;
import clinica.model.Funcionario;
import clinica.store.AppStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FuncionarioTab {

    private final FuncionarioDAO dao = new FuncionarioDAO();
    private TableView<Funcionario> table;
    private TextField tfCodf, tfNome, tfIdade, tfRg, tfSalario, tfDepto, tfTempo;

    public VBox build() {
        table = UiHelper.table();

        TableColumn<Funcionario, String> cCod = UiHelper.col("Cód");
        TableColumn<Funcionario, String> cNome = UiHelper.col("Nome");
        TableColumn<Funcionario, String> cIdade = UiHelper.col("Idade");
        TableColumn<Funcionario, String> cRg = UiHelper.col("RG");
        TableColumn<Funcionario, String> cSal = UiHelper.col("Salário");
        TableColumn<Funcionario, String> cDepto = UiHelper.col("Departamento");
        TableColumn<Funcionario, String> cTempo = UiHelper.col("Tempo (anos)");

        cCod.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCodf())));
        cNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));
        cIdade.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdade())));
        cRg.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRg() != null ? String.valueOf(d.getValue().getRg()) : ""));
        cSal.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getSalario() != null ? String.format("R$ %.2f", d.getValue().getSalario()) : ""));
        cDepto.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDepartamento() != null ? d.getValue().getDepartamento() : ""));
        cTempo.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTempoServico() != null ? String.valueOf(d.getValue().getTempoServico()) : ""));

        table.getColumns().addAll(cCod, cNome, cIdade, cRg, cSal, cDepto, cTempo);
        table.setPrefHeight(280);

        tfCodf = UiHelper.field("Código (vazio = auto)");
        tfNome = UiHelper.field("Nome");
        tfIdade = UiHelper.field("Idade");
        tfRg = UiHelper.field("RG (numérico)");
        tfSalario = UiHelper.field("Salário (ex: 1500.00)");
        tfDepto = UiHelper.field("Departamento");
        tfTempo = UiHelper.field("Tempo serviço (anos)");

        GridPane form = UiHelper.formGrid();
        form.addRow(0, new Label("Código:"), tfCodf, new Label("Nome:"), tfNome);
        form.addRow(1, new Label("Idade:"), tfIdade, new Label("RG:"), tfRg);
        form.addRow(2, new Label("Salário:"), tfSalario, new Label("Departamento:"), tfDepto);
        form.addRow(3, new Label("Tempo Serv.:"), tfTempo);

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
                UiHelper.card("Funcionários — Cadastro", form,
                        UiHelper.buttonBar(btnNovo, btnSalvar, btnRemover, btnBuscar)),
                UiHelper.card("Lista de Funcionários", table));
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
            Funcionario f = new Funcionario();
            String codStr = tfCodf.getText().trim();
            boolean isNovo = codStr.isEmpty();

            if (isNovo) {
                f.setCodf(dao.obterProximoCodigo());
            } else {
                f.setCodf(Integer.parseInt(codStr));
            }

            f.setNome(tfNome.getText().trim());
            f.setIdade(Integer.parseInt(tfIdade.getText().trim()));
            f.setRg(tfRg.getText().trim().isEmpty() ? null : Long.parseLong(tfRg.getText().trim()));
            f.setSalario(tfSalario.getText().trim().isEmpty() ? null : Double.parseDouble(tfSalario.getText().trim()));
            f.setDepartamento(tfDepto.getText().trim().isEmpty() ? null : tfDepto.getText().trim());
            f.setTempoServico(tfTempo.getText().trim().isEmpty() ? null : Integer.parseInt(tfTempo.getText().trim()));

            if (f.getNome().isEmpty()) {
                UiHelper.error("Nome é obrigatório.");
                return;
            }

            if (isNovo || dao.buscarPorCodigo(f.getCodf()) == null)
                dao.inserir(f);
            else
                dao.atualizar(f);
            UiHelper.info("Funcionário salvo!");
            carregar();
            limpar();
            AppStore.recarregarFuncionarios();
        } catch (NumberFormatException ex) {
            UiHelper.error("Verifique os campos numéricos.");
        } catch (Exception ex) {
            UiHelper.error("Erro: " + ex.getMessage());
        }
    }

    private void remover() {
        Funcionario sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("Selecione um funcionário.");
            return;
        }
        if (UiHelper.confirm("Remover funcionário " + sel.getNome() + "?")) {
            try {
                dao.remover(sel.getCodf());
                UiHelper.info("Funcionário removido.");
                carregar();
                limpar();
                AppStore.recarregarFuncionarios();
            } catch (Exception e) {
                UiHelper.error("Erro: " + e.getMessage());
            }
        }
    }

    private void buscar() {
        if (tfCodf.getText().trim().isEmpty()) {
            carregar();
            return;
        }
        try {
            Funcionario f = dao.buscarPorCodigo(Integer.parseInt(tfCodf.getText().trim()));
            if (f != null) {
                table.setItems(FXCollections.observableArrayList(f));
                preencher(f);
            } else
                UiHelper.info("Funcionário não encontrado.");
        } catch (Exception e) {
            UiHelper.error(e.getMessage());
        }
    }

    private void preencher(Funcionario f) {
        tfCodf.setText(String.valueOf(f.getCodf()));
        tfNome.setText(f.getNome());
        tfIdade.setText(String.valueOf(f.getIdade()));
        tfRg.setText(f.getRg() != null ? String.valueOf(f.getRg()) : "");
        tfSalario.setText(f.getSalario() != null ? String.format("%.2f", f.getSalario()) : "");
        tfDepto.setText(f.getDepartamento() != null ? f.getDepartamento() : "");
        tfTempo.setText(f.getTempoServico() != null ? String.valueOf(f.getTempoServico()) : "");
    }

    private void limpar() {
        tfCodf.clear();
        tfNome.clear();
        tfIdade.clear();
        tfRg.clear();
        tfSalario.clear();
        tfDepto.clear();
        tfTempo.clear();
        table.getSelectionModel().clearSelection();
        carregar();
    }
}
