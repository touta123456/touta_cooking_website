package com.toutacooking.springboot.service;

import com.toutacooking.springboot.dto.CommentDTO;
import com.toutacooking.springboot.dto.RecipeDTO;
import com.toutacooking.springboot.dto.UserDTO;
import com.toutacooking.springboot.entity.Comment;
import com.toutacooking.springboot.repository.JpaCommentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private JpaCommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveComment_withAuthorAndRecipe() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(1L);
        commentDTO.setContent("Super recette !");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(10L);
        commentDTO.setUser(userDTO);

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(100L);
        commentDTO.setRecipe(recipeDTO);

        Comment commentEntity = new Comment();
        commentEntity.setId(1L);
        commentEntity.setContent("Super recette !");

        when(commentRepository.save(any(Comment.class))).thenReturn(commentEntity);

        CommentDTO savedComment = commentService.save(commentDTO);

        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getContent()).isEqualTo("Super recette !");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testSaveComment_withoutAuthor() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(2L);
        commentDTO.setContent("Sans auteur");

        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setId(200L);
        commentDTO.setRecipe(recipeDTO);

        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        CommentDTO result = commentService.save(commentDTO);

        assertThat(result.getContent()).isEqualTo("Sans auteur");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testSaveComment_withoutRecipe() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(3L);
        commentDTO.setContent("Sans recette");

        UserDTO userDTO = new UserDTO();
        userDTO.setId(30L);
        commentDTO.setUser(userDTO);

        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        CommentDTO result = commentService.save(commentDTO);

        assertThat(result.getContent()).isEqualTo("Sans recette");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testSaveComment_withoutAuthorAndRecipe() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(4L);
        commentDTO.setContent("Pas de lien");

        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        CommentDTO result = commentService.save(commentDTO);

        assertThat(result.getContent()).isEqualTo("Pas de lien");
        verify(commentRepository).save(any(Comment.class));
    }
}
