package com.springboot.blog.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	
	private PostRepository postRepository;
	
	public PostServiceImpl(PostRepository postRepository) {
		this.postRepository = postRepository;
	}
	
	@Override
	public PostDto createPost(PostDto postDto) {
		// Convert Dto to Entity	
		Post post = mapToEntity(postDto);
		
		Post newPost = postRepository.save(post);
		
		//convert Entity to Dto
		PostDto postResponse = mapToDTO(newPost);
		
		return postResponse;
	}

	@Override
	public List<PostDto> getAllPosts() {
		
		List<Post> posts = postRepository.findAll();
		return posts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
		
	}
	
	//Convert Entity to Dto
	private PostDto mapToDTO(Post post) {
		PostDto postDto = new PostDto();
		postDto.setId(post.getId());
		postDto.setTitle(post.getTitle());
		postDto.setDescription(post.getDescription());
		postDto.setContent(post.getContent());
		
		return postDto;
	}

	//Convert Dto to Entity
	private Post mapToEntity(PostDto postDto) {
		Post post = new Post();
		post.setTitle(postDto.getTitle());
		post.setDescription(postDto.getDescription());
		post.setContent(postDto.getContent());
		
		return post;
	}
}
