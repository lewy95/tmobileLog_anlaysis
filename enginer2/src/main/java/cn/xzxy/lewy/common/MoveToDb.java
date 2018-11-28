package cn.xzxy.lewy.common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import rpc.domain.HttpAppHost;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class MoveToDb {

    private static ComboPooledDataSource source = new ComboPooledDataSource();

    public static void toDb(Map<String, HttpAppHost> reduceMap) {

        Connection conn = null;

        try {
            conn = source.getConnection();
            conn.setAutoCommit(false);
            //需要在数据库中建好表
            String sql = "insert into f_http_app_host values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            int i = 0;
            for (Map.Entry<String, HttpAppHost> entry : reduceMap.entrySet()) {
                HttpAppHost hah = entry.getValue();
                String reportTime = hah.getReportTime().toString();
                String year = reportTime.substring(0, 4);
                String month = reportTime.substring(4, 6);
                String day = reportTime.substring(6, 8);
                reportTime = year + "-" + month + "-" + day;

                ps.setDate(1, java.sql.Date.valueOf(reportTime));
                ps.setInt(2, hah.getAppType());
                ps.setString(3, hah.getAppSubType().toString());
                ps.setString(4, hah.getUserIp().toString());
                ps.setInt(5, hah.getUserPort());
                ps.setString(6, hah.getAppServerIp().toString());
                ps.setString(7, hah.getAppServerPort().toString());
                ps.setString(8, hah.getHost().toString());
                ps.setString(9, hah.getCellid().toString());
                ps.setLong(10, hah.getAttemps());
                ps.setLong(11, hah.getAccepts());
                ps.setLong(12, hah.getTrafficUL());
                ps.setLong(13, hah.getTracfficDL());
                ps.setLong(14, hah.getRetranUL());
                ps.setLong(15, hah.getRetranDL());
                ps.setLong(16, 1l);
                ps.setLong(17, hah.getTransDelay());

                ps.addBatch();
                i++;
                if (i % 1000 == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                }

            }
            //防止有残余的信息没有处理
            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {

                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    conn = null;
                    e.printStackTrace();
                } finally {
                    conn = null;
                }
            }
        }
        System.out.println("数据已经全部写到数据库里");

    }
}
