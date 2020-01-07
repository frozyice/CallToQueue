package com.frozyice.queuemanager;

class Card {
    private String phoneNumber;


    public Card(){}

    public Card(String number){
        this.phoneNumber=number;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
