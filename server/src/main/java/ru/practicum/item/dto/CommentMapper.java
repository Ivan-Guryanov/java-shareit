package ru.practicum.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.item.Comment;
import ru.practicum.user.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment mapToComment(CommentDto comment, User user) {
        return Comment.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(comment.getItem())
                .author(user)
                .created(comment.getCreated())
                .build();
    }
}
