package com.don.ServiceLocator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public final class ServiceLocator {

    private static final Map<String, Object> services = new ConcurrentHashMap<String, Object>();

    private static ServiceLocator instance;

    private static Context context;

    static {
        try {
            Context initContext = new InitialContext();
            if (ServerDetector.isJBoss()) {
                context = (Context) initContext.lookup("java:");
            } else if (ServerDetector.isTomcat()) {
                context = (Context) initContext.lookup("java:/comp/env");
            } else {
                context = initContext;
                // or add more 'else if' blocks according to servers to be supported
            }
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static ServiceLocator getInstance() {
        return instance;
    }

    public DataSource getDataSource(String name) throws Exception {
        if (name == null || name.length() <= 0)
            throw new IllegalArgumentException("name");

        if (services.containsKey(name))
            return (DataSource) services.get(name);

        DataSource ds = (DataSource) context.lookup(name);

        services.put(name, ds);
        return ds;
    }
}