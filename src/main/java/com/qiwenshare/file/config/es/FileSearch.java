package com.qiwenshare.file.config.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "filesearch", shards = 1, replicas = 0)
public class FileSearch {
    @Id
    private Long userFileId;
    @Field(type = FieldType.Long)
    private Long fileId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String fileName;
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Keyword)
    private String timeStampName;
    @Field(type = FieldType.Keyword)
    private String fileUrl;
    @Field(type = FieldType.Long)
    private Long fileSize;
    @Field(type = FieldType.Integer)
    @Deprecated
    private Integer isOSS;
    @Field(type = FieldType.Integer)
    private Integer storageType;
    @Field(type = FieldType.Integer)
    private Integer pointCount;
    @Field(type = FieldType.Keyword)
    private String identifier;
    @Field(type = FieldType.Long)
    private Long userId;
    @Field(type = FieldType.Keyword)
    private String filePath;
    @Field(type = FieldType.Keyword)
    private String extendName;
    @Field(type = FieldType.Integer)
    private Integer isDir;
    @Field(type = FieldType.Keyword)
    private String uploadTime;
    @Field(type = FieldType.Integer)
    private Integer deleteFlag;
    @Field(type = FieldType.Keyword)
    private String deleteTime;
    @Field(type = FieldType.Keyword)
    private String deleteBatchNum;
}
