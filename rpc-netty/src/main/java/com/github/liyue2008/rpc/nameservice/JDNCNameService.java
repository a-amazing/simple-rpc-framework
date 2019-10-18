package com.github.liyue2008.rpc.nameservice;

import com.github.liyue2008.rpc.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author:wangyi
 * @Date:2019/10/17
 */
public class JDNCNameService implements NameService {

    private static final Logger logger = LoggerFactory.getLogger(JDNCNameService.class);

    private static final Collection<String> schemes = Collections.singleton("jdbc");

    private static final String databaseUrl = "jdbc:mysql://localhost:3306/rpcdemo?user=root&password=root&serverTimezone=Asia/Shanghai";
    private static final String querySql = "select uri from rpc_nameservice where service_name = ?";
    private static final String createSql = "create table rpc_nameservice (id int primary key auto_increment,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tservice_name varchar(128) not null unique\tCOMMENT '服务全路径类名',\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\turi varchar(128) not null comment '服务端地址'\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t);";
    private static final String insertSql = "insert into rpc_nameservice(service_name,uri) values(?,?);";
    private static final String updateSql = "update rpc_nameservice set uri = ? where service_name = ?;";
    private static String driverClassName = "";

    @Override
    public Collection<String> supportedSchemes() {
        return schemes;
    }

    @Override
    public void connect(URI nameServiceUri) {
        if (schemes.contains(nameServiceUri.getScheme())) {
            String specificPart = nameServiceUri.getSchemeSpecificPart();
            if (specificPart.contains("mysql")) {
                driverClassName = "com.mysql.cj.jdbc.Driver";
            }//其余数据库驱动的逻辑写在这里!
            Connection conn = null;
            try {
                Class.forName(driverClassName);
                conn = DriverManager.getConnection(databaseUrl);
            } catch (ClassNotFoundException e) {
                logger.error(driverClassName + "驱动类不存在!");
            } catch (SQLException e) {
                logger.error("获取数据库连接失败!");
            }finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            throw new RuntimeException("Unsupported scheme!");
        }
    }

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {
        //查询对应的信息,如果存在并等于当前uri,不做操作
        //如果存在并不等于,update,拼接当前uri
        //如果不存在,或者等于"",insert
        Object result = executeSql(insertSql, serviceName, uri.getScheme() + uri.getSchemeSpecificPart());
        if (result != null && result instanceof Integer) {
            if (((Integer) result).intValue() == 1) {
                logger.info("注册成功!");
                return;
            }
        }
        logger.error("注册失败!");
    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        Object result = executeSql(querySql, serviceName);
        if (result != null && result instanceof String) {
            try {
                return new URI((String) result);
            } catch (URISyntaxException e) {
                logger.error("uri格式有误,请确认!");
            }
        }
        return null;
    }

    private Object executeSql(String sql, String... params) {
        try (Connection conn = DriverManager.getConnection(databaseUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (sql == querySql) {
                pstmt.setString(1, params[0]);
                ResultSet resultSet = pstmt.executeQuery();
                String uri = resultSet.getString("uri");
                return uri;
            } else {
                pstmt.setString(1, params[0]);
                pstmt.setString(2, params[1]);
                Integer count = pstmt.executeUpdate();
                return count;
            }
        } catch (SQLException e) {
            logger.error("sql异常!");
            return null;
        }
    }
}
