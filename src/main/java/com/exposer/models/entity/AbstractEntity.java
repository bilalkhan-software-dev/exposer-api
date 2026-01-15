package com.exposer.models.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;


@Getter
@Setter
public abstract class AbstractEntity {

    @Id
    @Indexed
    private String id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    @Indexed
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

}
