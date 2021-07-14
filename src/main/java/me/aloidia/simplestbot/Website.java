package me.aloidia.simplestbot;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;


public class Website {
    private static final LinkedHashMap<String, String> tables;
    private static Website website;

    static {
        tables = new LinkedHashMap<>();
        tables.put("level-table", "Level");
        tables.put("monster-table", "Monster fights");
        tables.put("arena-table", "Trophies");
        tables.put("old-arena-table", "Trophies");
        tables.put("referrals-table", "Referrals");
    }

    private final LinkedList<Leaderboard> leaderboards;

    public Website(LinkedList<Leaderboard> items) {
        leaderboards = items;
    }

    public static LinkedHashMap<String, String> getTables() {
        return tables;
    }

    public static void updateWebsite() {
        try {
            Document doc = loadDoc();
            if (doc == null) return;

            LinkedList<Leaderboard> leaderboards = new LinkedList<>();
            for (Map.Entry<String, String> entry : tables.entrySet()) {
                String tableName = entry.getValue();
                String tableID = entry.getKey();

                Leaderboard leaderboard = new Leaderboard(tableName);
                Element table = doc.getElementById(tableID);
                Element thead = table.getElementsByTag("thead").first();
                Element tbody = table.getElementsByTag("tbody").first();

                Elements ths = thead.select("th");
                Elements trs = tbody.select("tr");
                for (int i = 0; i < ths.size(); i++) {
                    String title = ths.get(i).text();
                    List<String> list = new ArrayList<>();
                    for (Element tr : trs) {
                        Elements tds = tr.select("td");
                        list.add(tds.get(i).text());
                    }
                    leaderboard.addColumn(title, list);
                }
                leaderboards.add(leaderboard);
            }
            website = new Website(leaderboards);
        } catch (Exception ignored) {
            System.out.println(Utils.formatDateTime() + " (Console) : Failed to update data.");
        }
    }

    private static Document loadDoc() {
        try {
            OkHttpClient okHttp = new OkHttpClient();
            Request request = new Request.Builder().url("https://simplestrpg.com").build();
            ResponseBody responseBody = okHttp.newCall(request).execute().body();

            return Jsoup.parse(responseBody.string());
        } catch (IOException e) {
            return null;
        }
    }

    public static Website getWebsite() {
        return website;
    }

    public LinkedList<Leaderboard> getLeaderboards() {
        return leaderboards;
    }
}
