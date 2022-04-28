package ru.dgk.DataTECg.services;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import ru.dgk.DataTECg.entity.IDs;
import ru.dgk.DataTECg.entity.TMData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Service
public class TMDataService {
    String SQL = "SELECT TIME1970, FROM_DT1970(TIME1970) as datetime, VAL FROM RSDU2ELARH.EL010_6303821 WHERE time1970 > TO_DT1970(TO_DATE ('2022-03-23 0:00:00', 'YYYY-MM-DD HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE ('2022-03-24 00:00:00', 'YYYY-MM-DD HH24:MI:SS'))";
    final HikariDataSource dataSource;

    public TMDataService(HikariDataSource  dataSource) {
        this.dataSource = dataSource;
    }
/*
    public Map<String, Object> getData(){
        String oldHour = "00";
        ArrayList <Double> val = new ArrayList();
        ArrayList <String> time = new ArrayList();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(SQL)) {
            ResultSet rs = st.executeQuery();

            while (rs.next()){

                val.add(rs.getDouble("VAL"));
                String dateTime = rs.getString("datetime");
                int b = dateTime.indexOf(':');
                int a = b-2;
                String hour = dateTime.substring(a, b);


                if(hour.equals(oldHour)){
                    time.add("");
                }else{
                    time.add(hour);
                    oldHour = hour;
                }

//                System.out.println("hour: "+ hour + " VAL: "+ rs.getString("VAL"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> attributes = new HashMap();
        attributes.put("val", val);
        attributes.put("time", time);
        return attributes;
    }
*/

    public ArrayList<EnumSet<IDs>> getHM(){
        ArrayList<EnumSet<IDs>> list = new ArrayList<>();

        list.add(EnumSet.range(IDs.HM1MAKEUP,IDs.HM1GB ));
        list.add(EnumSet.range(IDs.HM3MAKEUP,IDs.HM3GB ));
        list.add(EnumSet.range(IDs.HM7MAKEUP,IDs.HM7GB ));
        list.add(EnumSet.range(IDs.HM10MAKEUP,IDs.HM10GB ));
        return list;
    }

    public Map<String, ?> getDatasets(Map<String,String> params) {

        Map<String, Object> attributes = new HashMap();
        StringBuffer sb = new StringBuffer();
        var paramArray = params.keySet().toArray();
        var firstID = IDs.valueOf((String) paramArray[0]).id;

        sb.append("SELECT  FROM_DT1970(EL010_");
        sb.append(firstID);
        sb.append(".time1970) as t");


        for (var param : paramArray) {
            sb.append(", ");
            sb.append("EL010_");
            sb.append(IDs.valueOf((String) param).id);
            sb.append(".VAL");
            sb.append(" as ");
            sb.append(IDs.valueOf((String) param));
        }


        sb.append(" FROM RSDU2ELARH.EL010_");
        sb.append(firstID);
        sb.append(" EL010_");
        sb.append(firstID);

        for (int i = 1; i < paramArray.length; i++) {
            String id = IDs.valueOf((String) paramArray[i]).id;

            sb.append(" FULL OUTER JOIN RSDU2ELARH.EL010_");
            sb.append(id);
            sb.append(" EL010_");
            sb.append(id);

            sb.append(" ON EL010_");
            sb.append(firstID);
            sb.append(".time1970 = EL010_");
            sb.append(id);
            sb.append(".time1970");
        }

        sb.append(" WHERE EL010_");
        sb.append(firstID);
        sb.append(".time1970 > TO_DT1970(TO_DATE ('2022-03-23 0:00:00', 'YYYY-MM-DD HH24:MI:SS'))");
        sb.append("AND EL010_");
        sb.append(firstID);
        sb.append(".time1970 < TO_DT1970(TO_DATE ('2022-03-23 23:00:00', 'YYYY-MM-DD HH24:MI:SS'))");
        sb.append("ORDER BY t");

        System.out.println(sb.toString());



        String oldHour = "00";
        ArrayList <TMData> tmDataset = null;
        ArrayList <String> time = new ArrayList();

        try(Connection conn = dataSource.getConnection();
            PreparedStatement st = conn.prepareStatement(sb.toString())) {
            ResultSet rs = st.executeQuery();
            var rsMeta = rs.getMetaData();

            tmDataset = new ArrayList(rsMeta.getColumnCount());
            for (int i = 2; i < rsMeta.getColumnCount()+1; i++) {
                var tmData = new TMData();
                tmData.id = rsMeta.getColumnLabel(i);
                tmData.chartId = "chart_"+tmData.id;
                var param = IDs.valueOf(tmData.id);
                tmData.name = param.name;
                tmData.HM = param.hm;
                tmDataset.add(tmData);
            }

            while (rs.next()){
                for (int i = 0; i < tmDataset.size(); i++) {
                    TMData tmData = tmDataset.get(i);
                    tmData.val.add(rs.getDouble(tmData.id));
                }

                String dateTime = rs.getString("T");
                int b = dateTime.indexOf(':');
                int a = b-2;
                String hour = dateTime.substring(a, b);


                if(hour.equals(oldHour)){
                    time.add("");
                }else{
                    time.add(hour);
                    oldHour = hour;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        attributes.put("tmDataset", tmDataset);
        attributes.put("time", time);

        return attributes;
    }
}
