package com.frozyice.queuemanager;

import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private String CurrentPhoneNumber;
    private List<String> PhoneNumbersList;

    private int NumberOfPeopleCalledIn;
    private int AdaptiveEstimatedQueueTime;
    private LocalTime QueueStartTime;
    private LocalTime RecallTime;


    public void setRecallTime() {
        RecallTime = new LocalTime();
    }

    public Queue() {
        PhoneNumbersList = new ArrayList<>();
        NumberOfPeopleCalledIn = 0;
    }

    public String getCurrentPhoneNumber() {
        return CurrentPhoneNumber;
    }

    public void setCurrentPhoneNumber(String currentPhoneNumber) {
        CurrentPhoneNumber = currentPhoneNumber;
    }

    public List<String> getPhoneNumbersList() {
        return PhoneNumbersList;
    }

    public void addToPhoneNumbersList(String phoneNumber) {
        PhoneNumbersList.add(phoneNumber);
    }

    public void removeFromPhoneNumbersList() {
        PhoneNumbersList.remove(0);
    }

    public void setNumberOfPeopleCalledIn() {
        NumberOfPeopleCalledIn++;

        if (RecallTime!=null) {
            LocalTime now = new LocalTime();
            int seconds = (Seconds.secondsBetween(RecallTime, now)).getSeconds();
            if (seconds < 60) {
                if (AdaptiveEstimatedQueueTime > 60) {
                    NumberOfPeopleCalledIn--;
                    RecallTime=null;
                }
            }
        }

    }

    public int peopleBefore() {
        return PhoneNumbersList.size() - 1;
    }


    public String calculateEstimateTime(int userEstimatedQueueTime) {
        int seconds;
        if (NumberOfPeopleCalledIn < 5) {
            seconds = userEstimatedQueueTime * peopleBefore();
            System.out.println("seconds = userEstimatedQueueTime * peopleBefore()"); //debug
            System.out.println(seconds+" = "+userEstimatedQueueTime+" * "+peopleBefore()); //debug
        } else {
            seconds = AdaptiveEstimatedQueueTime * peopleBefore();
            System.out.println("seconds = AdaptiveEstimatedQueueTime * peopleBefore()"); //debug
            System.out.println(seconds+" = "+AdaptiveEstimatedQueueTime+" * "+peopleBefore()); //debug
        }
        LocalTime now = new LocalTime();
        DateTimeFormatter timeFormat = DateTimeFormat.forPattern("HH:mm");

        return timeFormat.print(now.plusSeconds(seconds));
    }

    public void setAdaptiveEstimatedQueueTime() {

        LocalTime now = new LocalTime();

        //start Queue
        if (NumberOfPeopleCalledIn == 1)
            QueueStartTime = now;

        int seconds = (Seconds.secondsBetween(QueueStartTime, now)).getSeconds();
        if (seconds <= 0)
            AdaptiveEstimatedQueueTime = seconds / (NumberOfPeopleCalledIn-1);
    }
}
