package com.example.instagramRandom.domains;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;

public class ClientQuery {
    private static String shortcode;
    private static HashSet<Object> usernames;

    public ClientQuery() {
    }

    public void setUsernames(HashSet<Object> usernames) {
        ClientQuery.usernames = usernames;
    }

    public void setShortcode(String shortcode) {
        ClientQuery.shortcode = shortcode;
    }

    public String getShortcode() {
        return shortcode;
    }

    public HashSet<Object> getUsernames() {
        return usernames;
    }

    public String linkBuilderByShortCode(){
        return "https://www.instagram.com/graphql/query/?query_hash=f0986789a5c5d17c2400faebf16efd0d&variables=" +
                "{\"shortcode\":\"" + shortcode + "\",\"first\":\"" + 50 + "\"}";
    }

    public  String linkBuilderByAfter(String after){
        String linkWithoutLastSymbol = linkBuilderByShortCode().substring(0,linkBuilderByShortCode().length()-1);
        return linkWithoutLastSymbol + ",\"after\":\"" + after + "\"}";
    }

    public String doRequestToInst(String query) {
        StringBuilder sb = new StringBuilder();
        HttpURLConnection httpClient = null;
        try {
            httpClient = (HttpURLConnection) new URL(query).openConnection();
            httpClient.setRequestMethod("GET");
            httpClient.setUseCaches(false);
            httpClient.setRequestProperty("Cookie","sessionid=6038257367%3ADdAdlqU442fqPQ%3A25");

            if(HttpURLConnection.HTTP_OK == httpClient.getResponseCode()){
                BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()));
                String line;
                while ((line = in.readLine()) != null){
                    sb.append(line);
                    sb.append("\n");
                }

            } else {
                System.out.println("Failed: " + httpClient.getResponseCode() + ", " + httpClient.getResponseMessage());
            }

        } catch (Throwable cause){
            cause.printStackTrace();
        } finally {
            if (httpClient!=null){
                httpClient.disconnect();
            }
        }

        return sb.toString();
    }


    public void getUsernamesFromComments(String json){
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONObject("data").getJSONObject("shortcode_media")
                .getJSONObject("edge_media_to_comment").getJSONArray("edges");

        for (int i = 0; i < array.length(); i++){
            JSONObject item = array.getJSONObject(i);
            usernames.add(item.getJSONObject("node").getJSONObject("owner").get("username"));
        }

        Object hasNextPage = object.getJSONObject("data").getJSONObject("shortcode_media")
                .getJSONObject("edge_media_to_comment").getJSONObject("page_info").get("has_next_page");

        Object wellDone = true;
        if (hasNextPage == wellDone) {
            Object endCursor = object.getJSONObject("data").getJSONObject("shortcode_media")
                    .getJSONObject("edge_media_to_comment").getJSONObject("page_info").get("end_cursor");
            getUsernamesFromComments(doRequestToInst(linkBuilderByAfter(endCursor.toString())));
        }

    }


    public String getLuckyFollower(){
        int size = usernames.size();
        int item = new Random().nextInt(size);
        int counter = 0;
        String line = null;
        for(Object o : usernames){
            if(counter == item){ line = o.toString();}
            counter++;
        }
        return line;
    }

}
