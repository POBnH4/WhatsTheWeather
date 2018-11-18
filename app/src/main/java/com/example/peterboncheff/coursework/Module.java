package com.example.peterboncheff.coursework;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

@Entity
public class Module {
    @NonNull
    @PrimaryKey(autoGenerate = true)

    private int uid;

    private String reference;

    @ColumnInfo(name = "SCQF_Credits")
    private int scqfCredits;

    private Date createdOn;

    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getScqfCredits() {
        return scqfCredits;
    }

    public void setScqfCredits(int scqfCredits) {
        this.scqfCredits = scqfCredits;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "Module{" +
                "uid=" + uid +
                ", reference='" + reference + '\'' +
                ", SCQF credits=" + scqfCredits +
                ", createdOn=" + createdOn +
                '}';
    }


}
