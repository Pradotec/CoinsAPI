package coinsapi;

        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;
        import org.bukkit.Bukkit;
        import org.bukkit.configuration.file.FileConfiguration;
        import org.bukkit.entity.Player;
        import org.bukkit.event.EventHandler;
        import org.bukkit.event.Listener;
        import org.bukkit.event.player.PlayerJoinEvent;

        import org.bukkit.plugin.java.JavaPlugin;
/**
 * Created by Pradotec on 02/03/2017.
 */
public class Main
        extends JavaPlugin
        implements Listener
{
    private FileConfiguration config = getConfig();
    private static Connection conn;
    private static String host;
    private static int port;
    private static String user;
    private static String pass;
    private static String database;
    private String prefix = "§7[§e§lCoinsAPI§7] ";

    public void onEnable()
    {
        this.config.addDefault("FirstJoinCoins", Integer.valueOf(0));
        this.config.addDefault("host", "locahost");
        this.config.addDefault("port", Integer.valueOf(3306));
        this.config.addDefault("user", "root");
        this.config.addDefault("pass", "password");
        this.config.addDefault("database", "coinsapi");
        this.config.options().copyDefaults(true);
        saveConfig();
        host = this.config.getString("host");
        port = this.config.getInt("port");
        user = this.config.getString("user");
        pass = this.config.getString("pass");
        database = this.config.getString("database");
        conn = openConnection();
        createTable();
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    public static Connection openConnection()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e1)
        {
            System.err.println(e1);
            e1.printStackTrace();
        }
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, pass);
            System.out.println("[CoinsAPI] Database connect");
            return conn;
        }
        catch (SQLException e)
        {
            System.out.println("[CoinsAPI] Database no connect");
            System.err.println(e);
            e.printStackTrace();
        }
        return null;
    }

    public static void createTable()
    {
        try
        {
            Statement state = conn.createStatement();
            state.executeUpdate("CREATE TABLE IF NOT EXISTS `players_coins` ('player_name' VARCHAR(20) ,`player_uuid` VARCHAR(16), `coins` INT)");
        }
        catch (SQLException e)
        {
            System.out.println("The table could not be created");
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public static void Update(String qry)
    {
        try
        {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(qry);

            stmt.close();
        }
        catch (Exception ex)
        {
            openConnection();
            System.err.println(ex);
        }
    }

    public static Connection getConnection()
    {
        return conn;
    }

    public static ResultSet Query(String qry)
    {
        ResultSet rs = null;
        try
        {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(qry);
        }
        catch (Exception ex)
        {
            openConnection();
            System.err.println(ex);
        }
        return rs;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        Player p = e.getPlayer();
        boolean exist = false;
        try
        {
            ResultSet rs = Query("SELECT `coins` FROM `players_coins` WHERE `player_uuid`='" + p.getUniqueId() + "'");
            while (rs.next()) {
                exist = Boolean.valueOf(true).booleanValue();
            }
        }
        catch (Exception err)
        {
            System.err.println(err);
            err.printStackTrace();
        }
        if (!exist) {
            Update("INSERT INTO `players_coins` ('player_name' ,`player_uuid`, `coins`) VALUES ('" + p.getName() +"', '" + p.getUniqueId() + "', '" + this.config.getInt("startCoins") + "')");
        }
        if(exist) {
            Main.Update("UPDATE `players_coins` SET `player_name`='" + p.getName() + "' WHERE `player_uuid`='" + p.getUniqueId() + "'");
        }
    }
}
