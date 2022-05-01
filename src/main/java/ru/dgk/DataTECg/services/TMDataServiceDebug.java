package ru.dgk.DataTECg.services;

import org.springframework.stereotype.Service;
import ru.dgk.DataTECg.entity.IDs;
import ru.dgk.DataTECg.entity.TMData;

import java.util.*;

@Service
public class TMDataServiceDebug {

    public Map<String, ?> getDatasets(String date, Map<String, String> params){
        Map<String, Object> attributes = new HashMap();
        ArrayList<TMData> tmDataset = new ArrayList();
        ArrayList <String> time = new ArrayList();
        int count = 24;
        for (int i = 0; i < params.size()-1; i++) {
            ArrayList<Double> val = new ArrayList<>();
            for (int j = 0; j < count; j++) {
                val.add(( ((Math.random() * (80 - 10)) + 10)));
            }
            tmDataset.add(new TMData(IDs.HM1CB.name,IDs.HM1CB.id+i,IDs.HM1CB.id+"_chart"+i,IDs.HM1CB.hm,val,time));
        }

        for (int i = 0; i < count; i++) time.add(i+":00");

        attributes.put("tmDataset", tmDataset);
        attributes.put("time", time);
        System.out.println(tmDataset);
        System.out.println(time);
        return attributes;
    }

    public ArrayList<EnumSet<IDs>> getHM(){
        ArrayList<EnumSet<IDs>> list = new ArrayList<>();

        list.add(EnumSet.range(IDs.HM1MAKEUP,IDs.HM1GB ));
        list.add(EnumSet.range(IDs.HM3MAKEUP,IDs.HM3GB ));
        list.add(EnumSet.range(IDs.HM7MAKEUP,IDs.HM7GB ));
        list.add(EnumSet.range(IDs.HM10MAKEUP,IDs.HM10GB ));
        return list;
    }
}
