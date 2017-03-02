package coinsapi.api;

import coinsapi.Main;
import java.sql.ResultSet;
import org.bukkit.entity.Player;

public class CoinsAPI
{
    public static int getCoins(Player p)
    {
        int c = 0;
        try
        {
            ResultSet rs = Main.Query("SELECT `coins` FROM `players_coins` WHERE `player_uuid`='" + p.getUniqueId() + "'");
            while (rs.next()) {
                c = rs.getInt(1);
            }
        }
        catch (Exception err)
        {
            System.err.println(err);
            err.printStackTrace();
        }
        return c;
    }

    public static void addCoins(Player p, int coins)
    {
        int c = getCoins(p);
        c += coins;
        Main.Update("UPDATE `players_coins` SET `coins`='" + c + "' WHERE `player_uuid`='" + p.getUniqueId() + "'");
    }

    public static void removeCoins(Player p, int coins)
    {
        int c = getCoins(p);
        c -= coins;
        Main.Update("UPDATE `players_coins` SET `coins`='" + c + "' WHERE `player_uuid`='" + p.getUniqueId() + "'");
    }

    public static void setCoins(Player p, int coins)
    {
        int c = getCoins(p);
        int a = 0;
        c = a + coins;
        Main.Update("UPDATE `players_coins` SET `coins`='" + c + "' WHERE `player_uuid`='" + p.getUniqueId() + "'");
    }

}