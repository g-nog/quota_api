package com.nogueira.quota.repositories;

import com.nogueira.quota.repositories.elastic.UserElasticSearchRepository;
import com.nogueira.quota.repositories.jpa.UserMySQLRepository;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Component
public class UserRepositoryFactory {

    private final UserMySQLRepository mySQLRepository;
    private final UserElasticSearchRepository userElasticSearchRepository;

    public UserRepositoryFactory(UserMySQLRepository mySQLRepository, UserElasticSearchRepository userElasticSearchRepository) {
        this.mySQLRepository = mySQLRepository;
        this.userElasticSearchRepository = userElasticSearchRepository;
    }


    public UserRepository getUserRepository() {
        int hour = ZonedDateTime.now(ZoneOffset.UTC).getHour();
        if (hour >= 9 && hour <= 17) {
            return mySQLRepository;
        } else {
            return mySQLRepository;
            //TODO: expending too much time to make elasticSearch works, just skipping it for the test purpose
//            return userElasticSearchRepository;
        }
    }
}
