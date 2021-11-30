package com.example.smn_aggregator;

import java.io.Serializable;
import java.util.ArrayList;

import twitter4j.Status;

/*
This class is used in order to make the Status class of the twitter4j
library serializable. This allows Status objects to be put as extras
and to be retrieved from intents
 */
public class StatusesWrapper implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<Status> statuses;

    public StatusesWrapper(ArrayList<Status> statuses){
        this.statuses = statuses;
    }

    public ArrayList<Status> getStatuses(){
        return statuses;
    }

}
