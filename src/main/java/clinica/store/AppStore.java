package clinica.store;

import clinica.dao.AmbulatorioDAO;
import clinica.dao.FuncionarioDAO;
import clinica.dao.MedicoDAO;
import clinica.dao.PacienteDAO;
import clinica.model.Ambulatorio;
import clinica.model.Funcionario;
import clinica.model.Medico;
import clinica.model.Paciente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppStore {

    public static final ObservableList<Medico> medicos = FXCollections.observableArrayList();
    public static final ObservableList<Paciente> pacientes = FXCollections.observableArrayList();
    public static final ObservableList<Ambulatorio> ambulatorios = FXCollections.observableArrayList();
    public static final ObservableList<Funcionario> funcionarios = FXCollections.observableArrayList();
    public static final ObservableList<String> departamentos = FXCollections.observableArrayList();
    public static final ObservableList<String> departamentosComTodos = FXCollections.observableArrayList();

    private static final MedicoDAO medicoDAO = new MedicoDAO();
    private static final PacienteDAO pacienteDAO = new PacienteDAO();
    private static final AmbulatorioDAO ambulatorioDAO = new AmbulatorioDAO();
    private static final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public static void recarregarMedicos() {
        medicos.setAll(medicoDAO.listarTodos());
    }

    public static void recarregarPacientes() {
        pacientes.setAll(pacienteDAO.listarTodos());
    }

    public static void recarregarAmbulatorios() {
        ambulatorios.setAll(ambulatorioDAO.listarTodos());
    }

    public static void recarregarFuncionarios() {
        funcionarios.setAll(funcionarioDAO.listarTodos());
        java.util.List<String> deptos = funcionarioDAO.listarDepartamentos();
        departamentos.setAll(deptos);

        java.util.List<String> comTodos = new java.util.ArrayList<>();
        comTodos.add("— Todos os Departamentos —");
        comTodos.addAll(deptos);
        departamentosComTodos.setAll(comTodos);
    }

    public static void recarregarTudo() {
        recarregarMedicos();
        recarregarPacientes();
        recarregarAmbulatorios();
        recarregarFuncionarios();
    }
}
