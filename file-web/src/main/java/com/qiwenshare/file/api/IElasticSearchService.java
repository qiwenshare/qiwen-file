package com.qiwenshare.file.api;

import com.qiwenshare.file.config.es.FileSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IElasticSearchService extends ElasticsearchRepository<FileSearch,Long> {

}