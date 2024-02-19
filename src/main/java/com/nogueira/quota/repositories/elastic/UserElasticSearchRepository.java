package com.nogueira.quota.repositories.elastic;


import com.nogueira.quota.models.User;
import com.nogueira.quota.repositories.UserRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserElasticSearchRepository extends ElasticsearchRepository<User, Long>, UserRepository {
}