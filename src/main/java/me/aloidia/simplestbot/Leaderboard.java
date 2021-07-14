package me.aloidia.simplestbot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Leaderboard {
    private final String title;
    private final LinkedHashMap<String, List<String>> columns = new LinkedHashMap<>();

    public Leaderboard(String title) {
        this.title = title;
    }

    public void addColumn(String header, List<String> contents) {
        columns.put(header, contents);
    }

    public String getTitle() {
        return title;
    }



    public String find(String searchString, String columnTitleFrom, String columnTitleTo) {
        try {
            List<String> columnFromRows = columns.get(columnTitleFrom);
            columnFromRows = columnFromRows.stream().map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList());

            int indexOf = columnFromRows.indexOf(searchString.toLowerCase(Locale.ROOT));
            if (indexOf == -1) return null;

            return columns.get(columnTitleTo).get(indexOf);
        } catch (Exception e) {
            return null;
        }
    }
}
