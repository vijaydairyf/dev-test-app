package com.devapp.devmain.network;

import com.devapp.devmain.dao.Dao;
import com.devapp.devmain.entity.Entity;

import java.io.IOException;
import java.util.List;

/**
 * Created by x on 28/1/18.
 */

public interface EntityManager {
    public List<Entity> getEntityFromJson(String jsonString, boolean isCloudPush) throws IOException, InvalidUpdateException;

    public String getDpnUrl();

    public void resetDpnUrl();

    public String getType();

    public Dao getDao();

    public void saveAll(List<? extends Entity> entityList, boolean isCloudPush) throws Exception;
}
