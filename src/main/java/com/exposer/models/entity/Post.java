package com.exposer.models.entity;


import lombok.*;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "posts")
@CompoundIndex(name = "post_author_active_idx", def = "{'author.$id': 1, 'isActive': 1}")
public class Post extends AbstractEntity {

    @Indexed
    private String title;
    private String content;
    private String image;

    @DBRef(lazy = true)
    private User author;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    @Builder.Default
    private boolean hasComments = false;

    @Builder.Default
    private PostStats postStats = new PostStats();

}
