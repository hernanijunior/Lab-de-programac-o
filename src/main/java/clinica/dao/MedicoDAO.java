package clinica.dao;

import clinica.database.Database;
import clinica.model.Medico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    public List<Medico> listarTodos() {
        List<Medico> lista = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM Medicos ORDER BY codm")) {
            while (rs.next())
                lista.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public Medico buscarPorCodigo(int codm) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM Medicos WHERE codm=?")) {
            ps.setInt(1, codm);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void inserir(Medico m) {
        String sql = "INSERT INTO Medicos (codm,nome,idade,especialidade,RG,cidade,nroa) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            set(ps, m, false);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Medico m) {
        String sql = "UPDATE Medicos SET nome=?,idade=?,especialidade=?,RG=?,cidade=?,nroa=? WHERE codm=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, m.getNome());
            ps.setInt(2, m.getIdade());
            ps.setString(3, m.getEspecialidade());
            if (m.getRg() != null)
                ps.setLong(4, m.getRg());
            else
                ps.setNull(4, Types.NUMERIC);
            ps.setString(5, m.getCidade());
            if (m.getNroa() != null)
                ps.setInt(6, m.getNroa());
            else
                ps.setNull(6, Types.INTEGER);
            ps.setInt(7, m.getCodm());
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void remover(int codm) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM Medicos WHERE codm=?")) {
            ps.setInt(1, codm);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    // Relatório e: médicos com consultas em determinada data
    public List<String[]> medicosPorData(String data) {
        String sql = """
                SELECT DISTINCT m.codm, m.nome FROM Medicos m
                INNER JOIN Consultas c ON m.codm = c.codm
                WHERE c.data_consulta = ? ORDER BY m.nome
                """;
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, data);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                res.add(new String[] { rs.getString("codm"), rs.getString("nome") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    // Relatório f: médicos com consulta com determinado paciente
    public List<String[]> medicosPorPaciente(int codp) {
        String sql = """
                SELECT DISTINCT m.codm, m.nome FROM Medicos m
                INNER JOIN Consultas c ON m.codm = c.codm
                WHERE c.codp = ? ORDER BY m.nome
                """;
        List<String[]> res = new ArrayList<>();
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, codp);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                res.add(new String[] { rs.getString("codm"), rs.getString("nome") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    // Relatório g: idades dos médicos e total com a mesma idade
    public List<String[]> idadesETotalMedicos() {
        String sql = "SELECT idade, COUNT(*) as total FROM Medicos GROUP BY idade ORDER BY idade";
        List<String[]> res = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                res.add(new String[] { rs.getString("idade"), rs.getString("total") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public int contarPorAmbulatorio(int nroa) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("SELECT COUNT(*) FROM Medicos WHERE nroa=?")) {
            ps.setInt(1, nroa);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void desvincularAmbulatorio(int nroa) {
        try (PreparedStatement ps = Database.getConnection()
                .prepareStatement("UPDATE Medicos SET nroa=NULL WHERE nroa=?")) {
            ps.setInt(1, nroa);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void removerComTransacao(int codm) {
        String sql1 = "DELETE FROM Consultas WHERE codm=?";
        String sql2 = "DELETE FROM Medicos WHERE codm=?";
        try (PreparedStatement p1 = Database.getConnection().prepareStatement(sql1);
                PreparedStatement p2 = Database.getConnection().prepareStatement(sql2)) {
            p1.setInt(1, codm);
            p1.executeUpdate();
            p2.setInt(1, codm);
            p2.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    private void set(PreparedStatement ps, Medico m, boolean update) throws SQLException {
        ps.setInt(1, m.getCodm());
        ps.setString(2, m.getNome());
        ps.setInt(3, m.getIdade());
        ps.setString(4, m.getEspecialidade());
        if (m.getRg() != null)
            ps.setLong(5, m.getRg());
        else
            ps.setNull(5, Types.NUMERIC);
        ps.setString(6, m.getCidade());
        if (m.getNroa() != null)
            ps.setInt(7, m.getNroa());
        else
            ps.setNull(7, Types.INTEGER);
    }

    private Medico map(ResultSet rs) throws SQLException {
        Object nroaObj = rs.getObject("nroa");
        Object rgObj = rs.getObject("RG");
        return new Medico(
                rs.getInt("codm"),
                rs.getString("nome"),
                rs.getInt("idade"),
                rs.getString("especialidade"),
                rgObj != null ? ((Number) rgObj).longValue() : null,
                rs.getString("cidade"),
                nroaObj != null ? ((Number) nroaObj).intValue() : null);
    }

    public int obterProximoCodigo() {
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(codm), 0) + 1 FROM Medicos")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
