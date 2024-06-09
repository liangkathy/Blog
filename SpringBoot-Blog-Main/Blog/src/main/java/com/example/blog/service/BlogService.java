package com.example.blog.service;

import com.example.blog.dto.BlogDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Comment;
import com.example.blog.model.Tag;
import com.example.blog.model.User;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ICommentRepository;
import com.example.blog.repository.ITagRepository;
import com.example.blog.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlogService {
    @Autowired
    IBlogRepository blogRepository;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ICommentRepository commentRepository;

    @Autowired
    ITagRepository tagRepository;

    @Autowired
    CommentService commentService;

    private Blog mapToBlog(BlogDTO blogDTO, User existingUser) throws Exception {
        Blog blog = new Blog(); //create new blog
        blog.setTitle(blogDTO.getTitle());
        blog.setContent(blogDTO.getContent());
        if (blogDTO.getLikes() == null) { //set likes to 0 if given null
            blog.setLikes(0);
        } else {
            blog.setLikes(blogDTO.getLikes());
        }
        blog.setUser(existingUser);

        if (blogDTO.getTagIds() != null) { //if list of tag ids is provided, convert to list of tag objects
            List<Integer> tagIds = blogDTO.getTagIds();
            List<Tag> tags = new ArrayList<>(); //empty list to hold tags
            for (Integer tagId : tagIds) {
                Tag tagToAdd = tagRepository.findById(tagId).orElseThrow(() -> new Exception("Tag with id " + tagId + " not found"));
                tags.add(tagToAdd);
            }
            blog.setTags(tags); //set tags of blog to list created above
        }
        return blog;
    }

    //get all blogs
    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    //get blog by id
    public Blog getBlogById(Integer id) throws Exception {
        return blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));
    }


    //create blog
    public Blog createBlog (BlogDTO blogDTO) throws Exception {
        if (blogDTO != null) {
            Integer userId = blogDTO.getUserId();
            User existingUser = userRepository.findById(userId).orElseThrow(() -> new Exception("User with id " + userId + " not found"));

            Blog blog = mapToBlog(blogDTO, existingUser);
            existingUser.getBlogs().add(blog);
            userRepository.save(existingUser);
            return blogRepository.save(blog);
        } else {
            throw new NullPointerException("Blog cannot be null");
        }
    }


    //update blog
    public Blog updateBlog(Integer id, Blog blog) throws Exception {
        Blog existingBlog = blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));
        if (blog != null) {
            existingBlog.setTitle(blog.getTitle());
            existingBlog.setContent(blog.getContent());
            if (blog.getLikes() == null) { //set likes to 0 if given null
                existingBlog.setLikes(0);
            } else {
                existingBlog.setLikes(blog.getLikes());
            }
            //last modified date updates automatically, creation date does not change
            return blogRepository.save(existingBlog);
        } else {
            throw new NullPointerException("Blog cannot be null");
        }
    }

    //delete blog
    public void deleteBlog(Integer id) throws Exception {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));
        blogRepository.delete(blog);
    }


    //additional functionalities
    //add new comment to blog
    public Blog addCommentToBlog(Integer id, Comment comment) throws Exception {
        Blog existingBlog = blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));

        if(comment !=null) {
            String username = comment.getCommenterUsername();
            User existingUser = userRepository.findByUsername(username); //confirm username exists

            //set likes to 0 if given null
            if(comment.getLikes() == null) {
                comment.setLikes(0);
            }

            if(existingUser != null) {
                comment.setBlog(existingBlog);
                existingBlog.getComments().add(comment);

                commentRepository.save(comment); //save comment before communicating with notification to pass comment ID
                //sending notification to microservice
                commentService.createNotification(comment, existingBlog); //pass parameters to method that communicates with notification microservice


                return blogRepository.save(existingBlog);
            } else {
                throw new Exception("Commenter username does not exist as user");
            }
        } else {
            throw new NullPointerException("Comment cannot be null");
        }

    }

    //get blogs by keyword
    //using JPQL query from repository
    public List<Blog> getBlogsByKeyword(String keyword) throws Exception {
        //filter to find blog text containing keyword
        if (!keyword.isBlank()) { //keyword not blank (including whitespace) //note:request param cannot be null unless specified to be
            List<Blog> matchingBlogs = blogRepository.findBlogByKeywordInTitleOrContent(keyword);

            if (!matchingBlogs.isEmpty()) {
                return matchingBlogs;
            } else {
                return null; //no need for an exception (empty list is acceptable response to search)
            }
        } else {
            throw new IllegalArgumentException("Keyword cannot be a blank or empty string");
        }

    }

    //add new tag to blog
    public Blog addTagToBlog(Integer id, Tag tag) throws Exception {
        Blog existingBlog = blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));
        if (tag != null) {
            String tagName = tag.getName().toLowerCase(); //get tag name
            Tag matchingTag = tagRepository.findByName(tagName); //see if tag exists

            if (matchingTag != null) { //if a matching tag name is found, throw exception
                throw new DuplicateKeyException("Tag with name " + tagName + " already exists"); //stops here if match found
            }
        } else {
            throw new NullPointerException("Tag cannot be null");
        }

        existingBlog.getTags().add(tag); //add existing tag to existing blog

        return blogRepository.save(existingBlog); //save to blog will merge/persist to tag
    }

    //delete tag from blog
    public Blog removeTagFromBlog(Integer id, Integer tagId) throws Exception {
        Blog existingBlog = blogRepository.findById(id).orElseThrow(() -> new Exception("Blog with id " + id + " not found"));
        Tag existingTag = tagRepository.findById(tagId).orElseThrow(() -> new Exception("Tag with id " + tagId + " not found"));

        existingBlog.getTags().remove(existingTag);

        return blogRepository.save(existingBlog);
    }

}
