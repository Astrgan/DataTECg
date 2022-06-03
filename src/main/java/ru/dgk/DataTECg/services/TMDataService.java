package ru.dgk.DataTECg.services;


import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Service;
import ru.dgk.DataTECg.entity.IDs;
import ru.dgk.DataTECg.entity.TMData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class TMDataService {
    String SQL = "SELECT TIME1970, FROM_DT1970(TIME1970) as datetime, VAL FROM RSDU2ELARH.EL010_6303821 WHERE time1970 > TO_DT1970(TO_DATE ('2022-03-23 0:00:00', 'YYYY-MM-DD HH24:MI:SS')) AND time1970 < TO_DT1970(TO_DATE ('2022-03-24 00:00:00', 'YYYY-MM-DD HH24:MI:SS'))";
    final HikariDataSource dataSource;

    public TMDataService(HikariDataSource  dataSource) {
        this.dataSource = dataSource;
    }

    public ArrayList<EnumSet<IDs>> getHM(){
        ArrayList<EnumSet<IDs>> list = new ArrayList<>();

        list.add(EnumSet.range(IDs.GASC,IDs.GASP ));

        list.add(EnumSet.range(IDs.HM1MAKEUP,IDs.HM1GB ));
        list.add(EnumSet.range(IDs.HM3MAKEUP,IDs.HM3GB ));
        list.add(EnumSet.range(IDs.HM7MAKEUP,IDs.HM7GB ));
        list.add(EnumSet.range(IDs.HM8MAKEUP,IDs.HM8GB ));
        list.add(EnumSet.range(IDs.HM9MAKEUP,IDs.HM9GB ));
        list.add(EnumSet.range(IDs.HM10MAKEUP,IDs.HM10GB ));

        list.add(EnumSet.range(IDs.PARP,IDs.PARG ));
        list.add(EnumSet.range(IDs.GVSP,IDs.GVSG ));
        list.add(EnumSet.range(IDs.SNPC,IDs.SNPARC ));
        return list;
    }

    public Map<String, ?> getDatasets(String dateTimeString, String timeEndString, Map<String, String> params) {
        var dateArray = dateTimeString.split(",");
        var date = dateArray[0];
        String timeStart = dateArray[1] + ":00";
        String timeEnd = timeEndString + ":00";
        Map<String, Object> attributes = new HashMap();
        StringBuffer sb = new StringBuffer();
        var paramArray = params.keySet().toArray();
        var firstID = IDs.valueOf((String) paramArray[2]).id;

        sb.append("SELECT  FROM_DT1970(EL010_");
        sb.append(firstID);
        sb.append(".time1970) as t");


        for (int i = 2; i < paramArray.length; i++) {
            sb.append(", ");
            sb.append("EL010_");
            sb.append(IDs.valueOf((String) paramArray[i]).id);
            sb.append(".VAL");
            sb.append(" as ");
            sb.append(IDs.valueOf((String) paramArray[i]));
        }

        sb.append(" FROM RSDU2ELARH.EL010_");
        sb.append(firstID);
        sb.append(" EL010_");
        sb.append(firstID);

        for (int i = 3; i < paramArray.length; i++) {
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
        sb.append(".time1970 >= TO_DT1970(TO_DATE ('");
        sb.append(date);
        sb.append(" ");
        sb.append(timeStart);
        sb.append("', 'DD.MM.YYYY HH24:MI:SS'))");
        sb.append("AND EL010_");
        sb.append(firstID);
        sb.append(".time1970 <= TO_DT1970(TO_DATE ('");
        sb.append(date);
        sb.append(" ");
        sb.append(timeEnd);
        sb.append("', 'DD.MM.YYYY HH24:MI:SS'))");
        sb.append("ORDER BY t");

        System.out.println(sb.toString());

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
                String hour = dateTime.substring(a, b+3);
                time.add(hour);
            }
            for (var tmData : tmDataset) {
                tmData.min = Collections.min(tmData.val);
                tmData.max = Collections.max(tmData.val);
                var average = tmData.val
                        .stream()
                        .mapToDouble(a -> a)
                        .average();
                tmData.average = average.isPresent() ? average.getAsDouble() : 0;
            }

            LocalDateTime dateTimeEnd = LocalDateTime.parse(date+" "+timeEnd,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

            LocalDateTime dateTimeLast = LocalDateTime.parse(date+" "+time.get(time.size()-1),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            dateTimeEnd = dateTimeEnd.minusMinutes(3);

            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");

            while (dateTimeLast.isBefore(dateTimeEnd)){
                dateTimeLast = dateTimeLast.plusMinutes(3);
                time.add(dateTimeLast.format(formatterTime));
                for (var tmData : tmDataset) {
                    tmData.val.add(0.0);
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
