package com.exposer.models.entity;


import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "saved_posts")

public class SavedPost extends AbstractEntity {

    @Indexed
    private String userId;

    @DBRef(lazy = true)
    private Post post;

    private String notes;

}
