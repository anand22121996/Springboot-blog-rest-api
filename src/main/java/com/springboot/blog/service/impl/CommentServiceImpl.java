package com.springboot.blog.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService{

	private CommentRepository commentRepository;
	private PostRepository postRepository;
	private ModelMapper mapper;
	
	public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper mapper) {
		this.commentRepository = commentRepository;
		this.postRepository = postRepository;
		this.mapper = mapper;
	}
	@Override
	public CommentDto createComment(long postId, CommentDto commentDto) {

		Comment comment = mapToEntity(commentDto);
		
		//retrieve post entity by Id
		Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		
		//set post to comment entity 
		comment.setPost(post);
		
		//comment entity to DB
		Comment newComment = commentRepository.save(comment);
		
		return mapToDto(newComment);
	}

	//Convert Entity to Dto
	private CommentDto mapToDto(Comment comment) {
		//Using ModelMapper
		CommentDto commentDto = mapper.map(comment, CommentDto.class);
		//Manually
//		CommentDto commentDto = new CommentDto();
//		commentDto.setId(comment.getId());
//		commentDto.setName(comment.getName());
//		commentDto.setEmail(comment.getEmail());
//		commentDto.setBody(comment.getBody());
		
		return commentDto;
	
	}
	
	//Convert Dto to Entity
	private Comment mapToEntity(CommentDto commentDto) {
		//Using ModelMapper 
		Comment comment = mapper.map(commentDto, Comment.class);
		//Manually
//		Comment comment = new Comment();
//		comment.setId(commentDto.getId());
//		comment.setName(commentDto.getName());
//		comment.setEmail(commentDto.getEmail());
//		comment.setBody(commentDto.getBody());
		
		return comment;
	}
	@Override
	public List<CommentDto> getCommentsByPostId(long postId) {
		// Retrieve Comments by postId
		List<Comment> comments = commentRepository.findByPostId(postId);
		
		//convert list of comment entity
		return comments.stream().map(comment -> mapToDto(comment)).collect(Collectors.toList());
	}
	
	@Override
	public CommentDto getCommentById(Long postId, Long commentId) {
		// retrieve post entity by Id
		Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		
		//retrieve comment by Id
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "comment does not belong to post");
		}
		
		return mapToDto(comment);
	}
	@Override
	public CommentDto updateComment(Long postId, Long commentId, CommentDto commentRequest) {
		// retrieve post by ID
		Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "comment does not belong to post");
		}
		
		comment.setName(commentRequest.getName());
		comment.setEmail(commentRequest.getEmail());
		comment.setBody(commentRequest.getBody());
		
		Comment updatedComment = commentRepository.save(comment);
		
		return mapToDto(updatedComment);
	}
	
	
	@Override
	public void deleteComment(Long postId, Long commentId) {
		// 
		Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
		
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));
		
		if(!comment.getPost().getId().equals(post.getId())) {
			throw new BlogAPIException(HttpStatus.BAD_REQUEST, "comment does not belong to post");
		}
		
		commentRepository.delete(comment);
		
	}
	
}

