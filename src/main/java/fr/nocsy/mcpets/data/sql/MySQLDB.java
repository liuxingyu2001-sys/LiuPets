package fr.nocsy.mcpets.data.sql;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.logging.Level;

public class MySQLDB {

    private Connection sqlCon;
    private String user;
    private String pass;
    private String ip;
    private String port;
    private String db;

    /** Timestamp of the last successful connection validation. */
    private long lastValidationTime = 0;
    /** Minimum interval (ms) between connection validations. */
    private static final long VALIDATION_INTERVAL_MS = 5000;

    public MySQLDB(String user, String pass, String ip, String port, String db) {
        this.user = user;
        this.pass = pass;
        this.ip = ip;
        this.port = port;
        this.db = db;
    }

    public boolean init() {
        if (this.user == null || this.pass == null || this.ip == null || this.port == null || this.db == null) {
            MCPets.getInstance().getLogger().severe("缺少 SQL 参数，请检查 config.yml 中的 MySQL.User/Password/Host/Port/Database。");
            return false;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = urlBuilder();
            this.sqlCon = DriverManager.getConnection(url, this.user, this.pass);
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().severe("无法连接 SQL 数据库，请检查数据库配置。");
            return false;
        }
        return true;
    }

    public void close() {
        if (this.sqlCon == null) return;
        try {
            if (!this.sqlCon.isClosed())
                this.sqlCon.close();
        }
        catch (Exception e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQL connection", e);
        }
    }

    public String urlBuilder() {
        return "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.db;
    }

    private void ensureConnection() throws SQLException {
        long now = System.currentTimeMillis();
        if (now - lastValidationTime < VALIDATION_INTERVAL_MS && this.sqlCon != null) {
            return;
        }
        if (this.sqlCon == null || !this.sqlCon.isValid(1)) {
            if (this.sqlCon != null) this.sqlCon.close();
            this.init();
        }
        lastValidationTime = now;
    }

    private boolean isUsable() {
        return GlobalConfig.getInstance().isDatabaseSupport() && this.sqlCon != null;
    }

    public ResultSet query(String s) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to validate SQL connection", e1);
        }
        if (!isUsable())
            return null;
        ResultSet set = null;
        try {
            Statement stat = this.sqlCon.createStatement();
            if (s.toLowerCase().startsWith("select")) {
                set = stat.executeQuery(s);
                closeStat(stat);
            } else {
                stat.executeUpdate(s);
                stat.close();
            }

        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL query failed: " + s, e);
        }
        return set;
    }

    public ResultSet preparedQuery(String sql, Object... params) {
        if (!GlobalConfig.getInstance().isDatabaseSupport())
            return null;
        try {
            ensureConnection();
        } catch (SQLException e1) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to validate SQL connection", e1);
        }
        if (!isUsable())
            return null;
        ResultSet set = null;
        try {
            PreparedStatement pstmt = this.sqlCon.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            if (sql.trim().toLowerCase().startsWith("select")) {
                set = pstmt.executeQuery();
                closeStat(pstmt);
            } else {
                pstmt.executeUpdate();
                pstmt.close();
            }
        } catch (SQLException e) {
            MCPets.getInstance().getLogger().log(Level.SEVERE, "SQL prepared query failed: " + sql, e);
        }
        return set;
    }

    private void closeStat(final Statement stat) {
        // SELECT result sets are consumed by the caller after this method returns.
        // Give generous headroom (5s) before closing the statement on the main thread,
        // otherwise a large player-data load can hit "statement closed" mid-iteration.
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    stat.close();
                } catch (SQLException e) {
                    MCPets.getInstance().getLogger().log(Level.SEVERE, "Failed to close SQL statement", e);
                }
            }
        }.runTaskLater(MCPets.getInstance(), 100L);

    }
}
