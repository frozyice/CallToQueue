package com.frozyice.queuemanager;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Settings implements Serializable{

    private String TextAddedToQueue;
    //private int AdaptiveEstimatedQueueTime;

    /*
    "Not accepting new people."
    "Added to queue! There are "+ peopleBefore + " people before You. Your estimated time: "+ (dateFormat.format(time.getTime())))
    "Already in queue! Keep Calm!"
    "Your up! It is your turn now!"

     if (text.contains("[peopleBefore]"));
        {
            //textArray = text.split("\\[",0);
            //text = Arrays.toString(textArray);
            //textArray = text.split("\\]");
            //text = Arrays.toString(textArray);
            StringTokenizer stringTokenizer = new StringTokenizer(text);
            text = "";

            //String textOut ="";
            while (stringTokenizer.hasMoreTokens())
            {
                String token = stringTokenizer.nextToken();
                if (token.equals("[peopleBefore]"))
                    text = text+"5 ";
                else
                    text = text+token+" ";

                //System.out.print(stringTokenizer.nextToken());
            }


        }

        //System.out.println(text);
        Log.wtf("PrintOut", text); //debug
     */


    public Settings(){
        TextAddedToQueue = "Added to queue! There are [people] people before You. Your estimated time: [time]";
    }

     public String getTextAddedToQueue() {
        return TextAddedToQueue;
    }

    public String getTextAddedToQueue(String time) {


        String text = TextAddedToQueue;

        if (text.contains("[time]"));
        {
            //textArray = text.split("\\[",0);
            //text = Arrays.toString(textArray);
            //textArray = text.split("\\]");
            //text = Arrays.toString(textArray);
            StringTokenizer stringTokenizer = new StringTokenizer(text);
            text = "";

            //String textOut ="";
            while (stringTokenizer.hasMoreTokens())
            {
                String token = stringTokenizer.nextToken();
                if (token.equals("[time]"))
                    text = text+" "+time+" ";
                else
                    text = text+token+" ";

                //System.out.print(stringTokenizer.nextToken());
            }


        }

        System.out.println(text);
        return text;
    }

    public void setTextAddedToQueue(String textAddedToQueue) {
        TextAddedToQueue = textAddedToQueue;
    }
}
