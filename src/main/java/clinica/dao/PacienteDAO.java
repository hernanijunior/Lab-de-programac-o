package clinica.dao;

import clinica.database.Database;
import clinica.model.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Pacientes ORDER BY codp")) {
            while (rs.next())
                lista.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public Paciente buscarPorCodigo(int codp) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM Pacientes WHERE codp=?")) {
            ps.setInt(1, codp);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void inserir(Paciente p) {
        String sql = "INSERT INTO Pacientes (codp,nome,idade,cidade,RG,problema) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            setAll(ps, p, true);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Paciente p) {
        String sql = "UPDATE Pacientes SET nome=?,idade=?,cidade=?,RG=?,problema=? WHERE codp=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setInt(2, p.getIdade());
            ps.setString(3, p.getCidade());
            if (p.getRg() != null)
                ps.setLong(4, p.getRg());
            else
                ps.setNull(4, Types.NUMERIC);
            ps.setString(5, p.getProblema());
            ps.setInt(6, p.getCodp());
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void remover(int codp) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Pacientes WHERE codp=?")) {
            ps.setInt(1, codp);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void removerComTransacao(int codp) {
        String sql1 = "DELETE FROM Consultas WHERE codp=?";
        String sql2 = "DELETE FROM Pacientes WHERE codp=?";
        try (PreparedStatement p1 = Database.getConnection().prepareStatement(sql1);
                PreparedStatement p2 = Database.getConnection().prepareStatement(sql2)) {
            p1.setInt(1, codp);
            p1.executeUpdate();
            p2.setInt(1, codp);
            p2.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    // Relatório i: pacientes com consultas antes de todos os horários de uma data
    public List<String[]> pacientesConsultasAnteriores(String data) {
        String sql = """
                SELECT DISTINCT p.* FROM Pacientes p
                INNER JOIN Consultas c ON p.codp = c.codp
                WHERE c.hora < ALL (
                    SELECT hora FROM Consultas WHERE data_consulta = ?
                )
                ORDER BY p.nome
                """;
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, data);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                res.add(new String[] { rs.getString("codp"), rs.getString("nome"),
                        rs.getString("idade"), rs.getString("cidade"), rs.getString("RG"), rs.getString("problema") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private void setAll(PreparedStatement ps, Paciente p, boolean withCod) throws SQLException {
        int i = 1;
        if (withCod)
            ps.setInt(i++, p.getCodp());
        ps.setString(i++, p.getNome());
        ps.setInt(i++, p.getIdade());
        ps.setString(i++, p.getCidade());
        if (p.getRg() != null)
            ps.setLong(i++, p.getRg());
        else
            ps.setNull(i++, Types.NUMERIC);
        ps.setString(i, p.getProblema());
    }

    private Paciente map(ResultSet rs) throws SQLException {
        Object rg = rs.getObject("RG");
        return new Paciente(
                rs.getInt("codp"), rs.getString("nome"), rs.getInt("idade"),
                rs.getString("cidade"), rg != null ? ((Number) rg).longValue() : null,
                rs.getString("problema"));
    }

    public int obterProximoCodigo() {
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(codp), 0) + 1 FROM Pacientes")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
