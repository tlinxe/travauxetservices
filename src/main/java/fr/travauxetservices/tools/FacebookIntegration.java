package fr.travauxetservices.tools;

import facebook4j.*;
import facebook4j.conf.Configuration;
import facebook4j.conf.ConfigurationBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacebookIntegration {
    public static void main(String[] args) throws FacebookException {
        // Create conf builder and set authorization and access keys
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setDebugEnabled(true);
        configurationBuilder.setOAuthAppId("768341719887132");
        configurationBuilder.setOAuthAppSecret("xxxxx");
        configurationBuilder.setOAuthAccessToken("xxxx");
        configurationBuilder.setOAuthPermissions("email, publish_stream, id, name, first_name, last_name, read_stream , generic");
        configurationBuilder.setUseSSL(true);
        configurationBuilder.setJSONStoreEnabled(true);
        // Create configuration and get Facebook instance
        Configuration configuration = configurationBuilder.build();
        FacebookFactory ff = new FacebookFactory(configuration);
        Facebook Facebook = ff.getInstance();
        try {
            // Set search string and get results
            String searchPost = "MACDonaldsIndia";
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_hh_mm");
            String fileName = "D:\\FacebookConfigFolder\\File\\" + searchPost + "_" + simpleDateFormat.format(date) + ".txt";
            String results = getFacebookPostes(Facebook, searchPost);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(results);
                bw.close();
                System.out.println("Completed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is used to get Facebook posts based on the search string set
    public static String getFacebookPostes(Facebook Facebook, String searchPost) throws FacebookException {
        String searchResult = "Item : " + searchPost + "\n";
        StringBuffer searchMessage = new StringBuffer();
        ResponseList<Post> results = Facebook.getPosts(searchPost);
        for (Post post : results) {
            System.out.println(post.getMessage());
            searchMessage.append(post.getMessage() + "\n");
            for (int j = 0; j < post.getComments().size(); j++) {
                searchMessage.append(post.getComments().get(j).getFrom().getName() + ", ");
                searchMessage.append(post.getComments().get(j).getMessage() + ", ");
                searchMessage.append(post.getComments().get(j).getCreatedTime() + ", ");
                searchMessage.append(post.getComments().get(j).getLikeCount() + "\n");
            }
        }
        String feedString = getFacebookFeed(Facebook, searchPost);
        searchResult = searchResult + searchMessage.toString();
        searchResult = searchResult + feedString;
        return searchResult;
    }

    // This method is used to get Facebook feeds based on the search string set
    public static String getFacebookFeed(Facebook Facebook, String searchPost) throws FacebookException {
        String searchResult = "";
        StringBuffer searchMessage = new StringBuffer();
        ResponseList<Post> results = Facebook.getFeed(searchPost);
        for (Post post : results) {
            System.out.println(post.getMessage());
            searchMessage.append(post.getFrom().getName() + ", ");
            searchMessage.append(post.getMessage() + ", ");
            searchMessage.append(post.getCreatedTime() + "\n");
        }
        searchResult = searchResult + searchMessage.toString();
        return searchResult;
    }
}