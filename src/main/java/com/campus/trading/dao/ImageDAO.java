package com.campus.trading.dao;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageDAO {
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;

    /**
     * 存储图片，返回图片ObjectId
     */
    public String storeImage(MultipartFile file, String contentType) throws IOException {
        ObjectId id = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), contentType);
        return id.toHexString();
    }

    /**
     * 通过图片ID获取GridFSFile
     */
    public GridFSFile getImageFileById(String id) {
        return gridFsTemplate.findOne(queryById(id));
    }

    /**
     * 获取图片输入流
     */
    public InputStream getImageStreamById(String id) throws IOException {
        GridFSFile file = getImageFileById(id);
        if (file == null) return null;
        return gridFsOperations.getResource(file).getInputStream();
    }

    /**
     * 删除图片
     */
    public void deleteImageById(String id) {
        gridFsTemplate.delete(queryById(id));
    }

    private org.springframework.data.mongodb.core.query.Query queryById(String id) {
        return new org.springframework.data.mongodb.core.query.Query(
                org.springframework.data.mongodb.core.query.Criteria.where("_id").is(new ObjectId(id))
        );
    }
} 