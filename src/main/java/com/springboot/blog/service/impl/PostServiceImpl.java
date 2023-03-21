package com.springboot.blog.service.impl;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;

@Service
public class PostServiceImpl implements PostService{
	
	private PostRepository postRepository;
	
	private ModelMapper mapper;
	
	public PostServiceImpl(PostRepository postRepository,ModelMapper mapper) {
		this.postRepository = postRepository;
		this.mapper = mapper;
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
	public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
		
		Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
				:Sort.by(sortBy).descending();
		
		//create pageable instance
		PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
		
		Page<Post> posts = postRepository.findAll(pageable);
		
		//get content from page object
		List<Post> listOfPosts = posts.getContent();
		
		List<PostDto> content = listOfPosts.stream().map(post -> mapToDTO(post)).collect(Collectors.toList());
		
		PostResponse postResponse = new PostResponse();
		postResponse.setContent(content);
		postResponse.setPageNo(posts.getNumber());
		postResponse.setPageSize(posts.getSize());
		postResponse.setTotalElements(posts.getTotalElements());
		postResponse.setTotalPages(posts.getTotalPages());
		postResponse.setLast(posts.isLast());
		
		return postResponse;
	}
	
	//Convert Entity to Dto
	private PostDto mapToDTO(Post post) {
		
		//using ModelMapper class
		PostDto postDto = mapper.map(post, PostDto.class);
		
	    //--manually convert Entity to Dto
//		PostDto postDto = new PostDto();
//		postDto.setId(post.getId());
//		postDto.setTitle(post.getTitle());
//		postDto.setDescription(post.getDescription());
//		postDto.setContent(post.getContent());
		
		return postDto;
	}

	//Convert Dto to Entity
	private Post mapToEntity(PostDto postDto) {
		//Using ModelMapper class
		Post post = mapper.map(postDto, Post.class);
		//Manually convert Dto to Entity
//		Post post = new Post();
//		post.setTitle(postDto.getTitle());
//		post.setDescription(postDto.getDescription());
//		post.setContent(postDto.getContent());
		
		return post;
	}

	@Override
	public PostDto getPostById(long id) {
		
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		return mapToDTO(post);
	}

	@Override
	public PostDto updatePost(PostDto postDto, long id) {
		//get post by Id from Database
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		
		post.setTitle(postDto.getTitle());
		post.setDescription(postDto.getDescription());
		post.setContent(postDto.getContent());
		
		Post updatedPost = postRepository.save(post);
		
		return mapToDTO(updatedPost);
	}

	@Override
	public void deletePostByID(long id) {
		//delete post by ID
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
		
		postRepository.delete(post);
		
	}
}
