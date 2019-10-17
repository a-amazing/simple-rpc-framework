package com.github.liyue2008.rpc.nameservice;

import com.github.liyue2008.rpc.NameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author:wangyi
 * @Date:2019/10/17
 */
public class JDNCNameService implements NameService {

    private static final Logger logger = LoggerFactory.getLogger(LocalFileNameService.class);

    static {
        String clazzName = "";
        try {
            Class<?> clazz = Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            logger.error(clazzName + "不存在!");
        }
    }

    @Override
    public Collection<String> supportedSchemes() {
        ArrayList<String> list = new ArrayList<>();
        list.add("jdbc");
        return list;
    }

    @Override
    public void connect(URI nameServiceUri) {

    }

    @Override
    public void registerService(String serviceName, URI uri) throws IOException {

    }

    @Override
    public URI lookupService(String serviceName) throws IOException {
        return null;
    }
}
