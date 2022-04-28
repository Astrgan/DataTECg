package ru.dgk.DataTECg.entity;

import java.util.ArrayList;

public class TMData {
    public String chartId;
    public String id;
    public String name;
    public String HM;
    public ArrayList<Double> val = new ArrayList<>();
    public ArrayList <String> time = new ArrayList<>();

    public TMData(String name, String id, String cartId, String HM, ArrayList<Double> val, ArrayList<String> time) {
        this.id = id;
        this.chartId = cartId;
        this.val = val;
        this.time = time;
        this.name = name;
        this.HM = HM;
    }

    public TMData() {

    }
}
