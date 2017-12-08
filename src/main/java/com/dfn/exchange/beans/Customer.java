package com.dfn.exchange.beans;

import java.util.List;

/**
 * Created by darshanas on 12/4/2017.
 */
public class Customer {

    private String nic;
    private String firstName;
    private String middleName;
    private String lastName;
    private String account;
    private List<Holding> holdings;

    public Customer(String nic, String firstName, String middleName, String lastName, String account) {
        this.nic = nic;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.account = account;
    }

    public Customer(String nic, String firstName, String lastName, String account) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.account = account;
    }

    public Customer(){

    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<Holding> getHoldings() {
        return holdings;
    }

    public void setHoldings(List<Holding> holdings) {
        this.holdings = holdings;
    }
}
