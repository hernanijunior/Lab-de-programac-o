package clinica.dao;

import clinica.database.Database;
import clinica.model.Ambulatorio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmbulatorioDAO {

    public List<Ambulatorio> listarTodos() {
        List<Ambulatorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ambulatorios ORDER BY nroa";
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    public Ambulatorio buscarPorCodigo(int nroa) {
        String sql = "SELECT * FROM Ambulatorios WHERE nroa = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nroa);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void inserir(Ambulatorio a) {
        String sql = "INSERT INTO Ambulatorios (nroa, andar, capacidade) VALUES (?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, a.getNroa());
            ps.setInt(2, a.getAndar());
            if (a.getCapacidade() != null)
                ps.setInt(3, a.getCapacidade());
            else
                ps.setNull(3, Types.SMALLINT);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void atualizar(Ambulatorio a) {
        String sql = "UPDATE Ambulatorios SET andar=?, capacidade=? WHERE nroa=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, a.getAndar());
            if (a.getCapacidade() != null)
                ps.setInt(2, a.getCapacidade());
            else
                ps.setNull(2, Types.SMALLINT);
            ps.setInt(3, a.getNroa());
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void remover(int nroa) {
        String sql = "DELETE FROM Ambulatorios WHERE nroa=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nroa);
            ps.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    public void removerComTransacao(int nroa) {
        String sql1 = "UPDATE Medicos SET nroa=NULL WHERE nroa=?";
        String sql2 = "DELETE FROM Ambulatorios WHERE nroa=?";
        try (PreparedStatement p1 = Database.getConnection().prepareStatement(sql1);
                PreparedStatement p2 = Database.getConnection().prepareStatement(sql2)) {
            p1.setInt(1, nroa);
            p1.executeUpdate();
            p2.setInt(1, nroa);
            p2.executeUpdate();
            Database.commit();
        } catch (SQLException e) {
            Database.rollback();
            throw new RuntimeException(e);
        }
    }

    // Relatório b: ambulatório com maior capacidade onde nenhum médico atende
    public List<String[]> relatorioMaiorCapSemMedico() {
        String sql = """
                SELECT nroa, capacidade FROM Ambulatorios
                WHERE nroa NOT IN (SELECT DISTINCT nroa FROM Medicos WHERE nroa IS NOT NULL)
                AND capacidade = (
                    SELECT MAX(capacidade) FROM Ambulatorios
                    WHERE nroa NOT IN (SELECT DISTINCT nroa FROM Medicos WHERE nroa IS NOT NULL)
                )
                """;
        List<String[]> res = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                res.add(new String[] { rs.getString("nroa"), rs.getString("capacidade") });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    // Relatório h: andares com ambulatórios e média de capacidade por andar
    public List<String[]> relatorioAndaresMédiaCapacidade() {
        String sql = "SELECT andar, AVG(capacidade) as media FROM Ambulatorios GROUP BY andar ORDER BY andar";
        List<String[]> res = new ArrayList<>();
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                res.add(new String[] { rs.getString("andar"), String.format("%.2f", rs.getDouble("media")) });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private Ambulatorio map(ResultSet rs) throws SQLException {
        return new Ambulatorio(
                rs.getInt("nroa"),
                rs.getInt("andar"),
                (Integer) rs.getObject("capacidade"));
    }

    public int obterProximoCodigo() {
        try (Statement st = Database.getConnection().createStatement();
                ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(nroa), 0) + 1 FROM Ambulatorios")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }
}
