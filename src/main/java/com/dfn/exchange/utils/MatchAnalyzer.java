package com.dfn.exchange.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Created by darshanas on 12/8/2017.
 */
public class MatchAnalyzer {

    public static void main(String[] args) {
        String fileName = "./matchTimes.txt";
        try (Stream<String> stream = Files.lines(Paths.get(fileName))){
            stream.forEach(s -> {
                String [] times = s.split(",");
                System.out.println("TOTAL MATCHES " + times.length);
                long totalTime = 0;
                for(String timeStr : times){

                    if(timeStr != null && !timeStr.equals("")){
                        long time = Long.parseLong(timeStr);
                        totalTime = totalTime + time;
                    }

                }
                System.out.println("Average matching time ms. " + totalTime/times.length);
            });
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}
