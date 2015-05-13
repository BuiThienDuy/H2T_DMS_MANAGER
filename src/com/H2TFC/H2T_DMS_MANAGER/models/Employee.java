package com.H2TFC.H2T_DMS_MANAGER.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.Date;

/*
 * Copyright (C) 2015 H2TFC Team, LLC
 * thanhduongpham4293@gmail.com
 * nhatnang93@gmail.com
 * buithienduy93@gmail.com
 * All rights reserved
 */
@ParseClassName("Employee")
public class Employee extends ParseUser {
    // 1. name
    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    // 2. phone number
    public String getPhoneNumber() {
        return getString("phone_number");
    }

    public void setPhoneNumber(String phoneNumber) {
        put("phone_number", phoneNumber);
    }

    // 3. address
    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    // 4. gender
    public String getGender() {
        return getString("gender");
    }

    public void setGender(String gender) {
        put("gender", gender);
    }

    // 5. id card
    public String getIdCard() {
        return getString("identify_card_number");
    }

    public void setIdCard(String cmnd) {
        put("identify_card_number", cmnd);
    }

    // 6. photo
    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }

    // 7. manager
    public String getManagerId() {
        return getString("manager_id");
    }

    public void setManagerId(String managerId) {
        put("manager_id", managerId);
    }

    // 8. role
    public String getRoleName() {
        return getString("role_name");
    }

    public void setRoleName(String roleName) {
        put("role_name", roleName);
    }

    // 9. ngay sinh
    public Date getDateOfBirth() {
        return getDate("date_of_birth");
    }

    public void setDateOfBirth(Date dob) {
        put("date_of_birth", dob);
    }

    // 10. khoa
    public boolean getLock() {
        return getBoolean("locked");
    }

    public void setLock(boolean locked) {
        put("locked", locked);
    }

    //  11. Chi tieu
    public double getRevenueGoal() {
        return getDouble("revenue_goal");
    }

    public void setRevenueGoal(double revenueGoal) {
        put("revenue_goal",revenueGoal);
    }
}
