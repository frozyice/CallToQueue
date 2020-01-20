package com.frozyice.callToQueue;

import org.joda.time.LocalTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private List<Card> CardList;

    private int NumberOfPeopleCalledIn;
    private int AdaptiveEstimatedQueueTime;
    private LocalTime QueueStartTime;
    private LocalTime RecallTime;

    private boolean IsAcceptingNewPersons;
    private boolean IsEndingCalls;
    private int UserEstimatedQueueTime;


    public Queue() {
        CardList = new ArrayList<>();
        NumberOfPeopleCalledIn = 0;
        IsAcceptingNewPersons=false;
        UserEstimatedQueueTime=5;
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
        return UserEstimatedQueueTime;
    }

    public void setUserEstimatedQueueTime(int userEstimatedQueueTime) {
        this.UserEstimatedQueueTime = userEstimatedQueueTime;
    }

    public boolean isEndingCalls() {
        return IsEndingCalls;
    }

    public void setEndingCalls(boolean endingCalls) {
        IsEndingCalls = endingCalls;
    }




    public List<Card> getCardList() {
        return CardList;
    }

    public void addToCardList(String phoneNumber) {
        Card card = new Card(phoneNumber);
        CardList.add(card);
    }

    public boolean cardListContains(String phoneNumber) {
        for (Card card : CardList)
        {
            if (card.getPhoneNumber().equals(phoneNumber))
                return true;
        }
        return false;
    }

    public void removeFromPhoneNumbersList() {
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
        return CardList.size()-2;
    }

    public int peopleTotal() {
        return CardList.size()-1;
    }

    public String calculateEstimateTime(int people) {

        int seconds;
        if (NumberOfPeopleCalledIn < 5) {
            seconds = (UserEstimatedQueueTime*60) * people;
        } else {
            seconds = AdaptiveEstimatedQueueTime * people;
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
