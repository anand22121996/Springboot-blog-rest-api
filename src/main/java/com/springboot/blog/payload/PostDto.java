package com.springboot.blog.payload;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostDto {

	private long id;
	
	//title should not be null or empty
	//title should have at least 2 characters
	@NotEmpty
	@Size(min=2, message = "Post title should have at least 2 characters")
	private String title;
	
	//description should not be null or empty
	//description should have at least 10 characters
	@NotEmpty
	@Size(min=10, message = "Post description should be at least 10 characters")
	private String description;
	
	//content should not be null or empty
	@NotEmpty
	private String content;
	
	public long getId() {
		return id;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public String getContent() {
		return content;
	}
	
	private Set<CommentDto> comments;
	
}
