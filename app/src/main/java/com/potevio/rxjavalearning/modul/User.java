package com.potevio.rxjavalearning.modul;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CHK on 19-7-10.
 */
public class User {

    String name;

    List<String> childNameList;

    public User() {
        childNameList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addChildName(String childName) {
        childNameList.add(childName);
    }

    public List<String> getChildNameList() {
        return childNameList;
    }
}
