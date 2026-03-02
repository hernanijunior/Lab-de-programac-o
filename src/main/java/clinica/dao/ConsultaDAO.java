package clinica.dao;

import clinica.database.Database;
import clinica.model.Consulta;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    public List<Consulta> listarTodas() {
        String sql = """
                SELECT c.*, m.nome AS nomeMedico, p.nome AS nomePaciente
                FROM Consultas c
                INNER JOIN Medicos m ON c.codm = m.codm
                INNER JOIN Pacientes p ON c.codp = p.codp
                ORDER BY c.data_consulta, c.hora
                """;
        List<Consulta> lista = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public List<Consulta> listarPorMedico(int codm) {
        String sql = """
                SELECT c.*, m.nome AS nomeMedico, p.nome AS nomePaciente
                FROM Consultas c
                INNER JOIN Medicos m ON c.codm = m.codm
                INNER JOIN Pacientes p ON c.codp = p.codp
                WHERE c.codm = ? ORDER BY c.data_consulta, c.hora
                """;
        return listarComParametro(sql, codm);
    }

    public List<Consulta> listarPorPaciente(int codp) {
        String sql = """
                SELECT c.*, m.nome AS nomeMedico, p.nome AS nomePaciente
                FROM Consultas c
                INNER JOIN Medicos m ON c.codm = m.codm
                INNER JOIN Pacientes p ON c.codp = p.codp
                WHERE c.codp = ? ORDER BY c.data_consulta, c.hora
                """;
        return listarComParametro(sql, codp);
    }

    private List<Consulta> listarComParametro(String sql, int param) {
        List<Consulta> lista = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public void inserir(Consulta c) {
        String sql = "INSERT INTO Consultas (codm, codp, data_consulta, hora) VALUES (?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, c.getCodm());
            ps.setInt(2, c.getCodp());
            ps.setDate(3, Date.valueOf(c.getDataConsulta()));
            ps.setTime(4, Time.valueOf(c.getHora()));
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    /**
     * Remarcação: atualiza data e hora de uma consulta identificada pelo
     * médico/paciente/data/hora original
     */
    public void remarcar(int codm, int codp, LocalDate dataAntiga, LocalTime horaAntiga,
            LocalDate novaData, LocalTime novaHora) {
        String sql = "UPDATE Consultas SET data_consulta=?, hora=? WHERE codm=? AND codp=? AND data_consulta=? AND hora=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(novaData));
            ps.setTime(2, Time.valueOf(novaHora));
            ps.setInt(3, codm);
            ps.setInt(4, codp);
            ps.setDate(5, Date.valueOf(dataAntiga));
            ps.setTime(6, Time.valueOf(horaAntiga));
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    /** Remove todas as consultas de um médico */
    public int removerPorMedico(int codm) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Consultas WHERE codm=?")) {
            ps.setInt(1, codm);
            int rows = ps.executeUpdate();
            Database.commit();
            return rows;
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    /** Remove todas as consultas em um determinado horário */
    public int removerPorHorario(LocalTime hora) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Consultas WHERE hora=?")) {
            ps.setTime(1, Time.valueOf(hora));
            int rows = ps.executeUpdate();
            Database.commit();
            return rows;
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    /** Remove uma consulta específica */
    public void remover(int codm, int codp, LocalDate data, LocalTime hora) {
        String sql = "DELETE FROM Consultas WHERE codm=? AND codp=? AND data_consulta=? AND hora=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, codm);
            ps.setInt(2, codp);
            ps.setDate(3, Date.valueOf(data));
            ps.setTime(4, Time.valueOf(hora));
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public int contarPorMedico(int codm) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM Consultas WHERE codm=?")) {
            ps.setInt(1, codm);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int contarPorPaciente(int codp) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM Consultas WHERE codp=?")) {
            ps.setInt(1, codp);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void removerPorPaciente(int codp) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Consultas WHERE codp=?")) {
            ps.setInt(1, codp);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    private Consulta map(ResultSet rs) throws SQLException {
        Consulta c = new Consulta(
                rs.getInt("codm"), rs.getInt("codp"),
                rs.getDate("data_consulta").toLocalDate(),
                rs.getTime("hora").toLocalTime());
        try {
            c.setNomeMedico(rs.getString("nomeMedico"));
        } catch (SQLException ignored) {
        }
        try {
            c.setNomePaciente(rs.getString("nomePaciente"));
        } catch (SQLException ignored) {
        }
        return c;
    }
}
