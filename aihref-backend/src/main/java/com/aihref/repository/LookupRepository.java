package com.aihref.repository;

import com.aihref.model.LookupDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LookupRepository extends ReactiveMongoRepository<LookupDocument, String> {
    
    Mono<LookupDocument> findByUrl(String url);
    
    Mono<Boolean> existsByUrl(String url);
}
