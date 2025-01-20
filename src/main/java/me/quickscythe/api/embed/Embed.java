package me.quickscythe.api.embed;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Embed {

    private final JSONObject data = new JSONObject();

    public Embed() {
        data.put("type", "rich");
        timestamp(new Date());
    }

    public Embed title(String title) {
        data.put("title", title);
        return this;
    }

    public Embed description(String description) {
        data.put("description", description);
        return this;
    }

    public Embed url(String url) {
        data.put("url", url);
        return this;
    }

    public Embed color(int color) {
        data.put("color", color);
        return this;
    }

    public Embed timestamp(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String iso = df.format(date);
        data.put("timestamp", iso);
        return this;
    }

    public Embed footer(String text, String icon_url) {
        JSONObject footer = new JSONObject();
        footer.put("text", text);
        footer.put("icon_url", icon_url);
        data.put("footer", footer);
        return this;
    }

    public Embed image(String url) {
        JSONObject image = new JSONObject();
        image.put("url", url);
        data.put("image", image);
        return this;
    }

    public Embed thumbnail(String url) {
        JSONObject thumbnail = new JSONObject();
        thumbnail.put("url", url);
        data.put("thumbnail", thumbnail);
        return this;
    }

    public Embed author(String name, String url, String icon_url) {
        JSONObject author = new JSONObject();
        author.put("name", name);
        author.put("url", url);
        author.put("icon_url", icon_url);
        data.put("author", author);
        return this;
    }

    public Embed addField(String name, String value, boolean inline) {
        JSONObject field = new JSONObject();
        field.put("name", name);
        field.put("value", value);
        field.put("inline", inline);
        if(!data.has("fields")) data.put("fields", new JSONArray());
        data.getJSONArray("fields").put(field);
        return this;
    }

    public JSONObject json() {
        return data;
    }
}
