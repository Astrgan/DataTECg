package ru.dgk.DataTECg.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TMEntity {
    @Id
    long TIME1970;
    Double VAL;

    public long getTIME1970() {
        return TIME1970;
    }

    public Double getVAL() {
        return VAL;
    }
}
