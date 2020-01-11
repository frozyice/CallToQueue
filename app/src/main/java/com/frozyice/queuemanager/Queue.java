package com.frozyice.queuemanager;

import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private String CurrentPhoneNumber;
    private List<Card> CardList;

    private int NumberOfPeopleCalledIn;
    private int AdaptiveEstimatedQueueTime;
    private LocalTime QueueStartTime;
    private LocalTime RecallTime;

    private boolean IsAcceptingNewPersons;
    private boolean IsEndingCalls;
    private int userEstimatedQueueTime;


    public Queue() {
        CardList = new ArrayList<>();
        NumberOfPeopleCalledIn = 0;
        IsAcceptingNewPersons=false;
        userEstimatedQueueTime=300;
    }

    public void setRecallTime() {
        RecallTime = new LocalTime();
    }

    public boolean isAcceptingNewPersons() {
        return IsAcceptingNewPersons;
    }

    public void setAcceptingNewPersons(boolean acceptingNewPersons) {
        IsAcceptingNewPersons = acceptingNewPersons;
    }

    public int getUserEstimatedQueueTime() {
        return userEstimatedQueueTime;
    }

    public void setUserEstimatedQueueTime(int userEstimatedQueueTime) {
        this.userEstimatedQueueTime = userEstimatedQueueTime*60;
    }

    public boolean isEndingCalls() {
        return IsEndingCalls;
    }

    public void setEndingCalls(boolean endingCalls) {
        IsEndingCalls = endingCalls;
    }

    public String getCurrentPhoneNumber() {
        return CurrentPhoneNumber;
    }

    public void setCurrentPhoneNumber(String currentPhoneNumber) {
        CurrentPhoneNumber = currentPhoneNumber;
    }

    public List<Card> getCardList() {
        //return PhoneNumbersList;
        return CardList;
    }

    public void addToCardList(String phoneNumber) {
        Card card = new Card(phoneNumber);
        //card.setPhoneNumber(phoneNumber);
        CardList.add(card);
        //PhoneNumbersList.add(phoneNumber);
    }

    public boolean cardListContains(String phoneNumber) {
        for (Card card : CardList)
        {
            if (card.getPhoneNumber().equals(phoneNumber))
            {
                return true;
            }
        }
        return false;
    }

    public void removeFromPhoneNumbersList() {
        CurrentPhoneNumber=CardList.get(0).getPhoneNumber();
        CardList.remove(0);
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

    public int getNumberOfPeopleCalledIn() {
        return NumberOfPeopleCalledIn;
    }

    public int peopleBefore() {
        return CardList.size() - 1;
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
        if (NumberOfPeopleCalledIn == 1) {
            QueueStartTime = now;
        }

        else {
            int seconds = (Seconds.secondsBetween(QueueStartTime, now)).getSeconds();
            if (seconds <= 0)
                AdaptiveEstimatedQueueTime = seconds / (NumberOfPeopleCalledIn - 1);
        }
    }
}
