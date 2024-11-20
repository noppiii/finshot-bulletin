package com.example.finshot.bulletin.service.global;

import java.util.concurrent.TimeUnit;

public interface InMemoryCacheService {

    void setData(String key, Object value, Long time, TimeUnit timeUnit);

    Object getData(String key);

    void deleteData(String key);
}