package com.example.blog.service;

import com.example.blog.dto.TagDTO;
import com.example.blog.model.Blog;
import com.example.blog.model.Tag;
import com.example.blog.repository.IBlogRepository;
import com.example.blog.repository.ITagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    @Autowired
    ITagRepository tagRepository;

    @Autowired
    IBlogRepository blogRepository;

    public List<Tag> mapToTags(List<TagDTO> tagsDTOs) throws Exception {
        List<Tag> tags = new ArrayList<>();
        for (TagDTO tagDTO : tagsDTOs) { //loop through tag DTOs
            Tag tag = new Tag(); //create a new tag for each round
            tag.setName(tagDTO.getName().toLowerCase());
            tag.setDescription(tagDTO.getDescription());

            if(tagDTO.getBlogIds() != null) { //if there are any blog Ids
                List<Integer> blogIds = tagDTO.getBlogIds(); //get list of blog ids
                List<Blog> blogs = new ArrayList<>(); //create empty list of blogs
                for (Integer blogId : blogIds) { //loop through blog id list
                    Blog blogToAdd = blogRepository.findById(blogId).orElseThrow(() -> new Exception("Blog with id " + blogId + " not found")); //find matching blog
                    blogs.add(blogToAdd); //add matching blog to blog list
                }
                tag.setBlogs(blogs); //set blogs to list created above
            }
            tags.add(tag); //add tag object
        }
        return tags;
    }

    //get all tags
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    //get tag by id
    public Tag getTagById(Integer id) throws Exception {
        return tagRepository.findById(id).orElseThrow(() -> new Exception("Tag with id " + id + " not found"));

    }

    //add tags (takes list and can take list of blogs)
    public List<Tag> addTags (List<TagDTO> tagDTOs) throws Exception {
        if (tagDTOs != null) {
            for (TagDTO tagDTO : tagDTOs) {
                String tagName = tagDTO.getName().toLowerCase(); //get tag name
                Tag matchingTag = tagRepository.findByName(tagName); //see if tag exists

                if (matchingTag != null) { //if a matching tag name is found, throw exception
                    throw new DuplicateKeyException("Tag with name " + tagName + " already exists"); //will stop here if match found
                }
            }
            //if no match found, method will continue here
            List<Tag> tags = mapToTags(tagDTOs);
            for (Tag tag : tags) { //for each tag, get all blogs
                if (tag.getBlogs() != null) { //if there are blogs
                    List<Blog> blogs = tag.getBlogs();
                    for (Blog blog : blogs) { //for each blog, set the tags
                        blog.getTags().add(tag);
                        //blogRepository.save(blog); //manual save to repo > not needed bc merge/persist from tag to blog
                    }
                }
            }
            return tagRepository.saveAll(tags); //saveAll replaces need to use for loop
        } else {
            throw new NullPointerException("Tag cannot be null");
        }
    }


    //update tag
    public Tag updateTag(Integer id, Tag tag) throws Exception {
        Tag existingTag = tagRepository.findById(id).orElseThrow(() -> new Exception("Tag with id " + id + " not found"));
        if (tag != null) {
            String tagName = tag.getName().toLowerCase(); //get tag name
            Tag matchingTag = tagRepository.findByName(tagName); //see if tag exists

            if (matchingTag != null) { //if a matching tag name is found, throw exception
                throw new DuplicateKeyException("Tag with name " + tagName + " already exists"); //stops here if match found
            }
            //if no match found, continues method
            existingTag.setName(tag.getName().toLowerCase());
            existingTag.setDescription(tag.getDescription());
            //intentionally not adding set for blog id since you wouldn't edit a tag from one blog to another (would delete tag or add)
            return tagRepository.save(existingTag);
        } else {
            throw new NullPointerException("Tag cannot be null");
        }
    }


    //delete tag
    public void deleteTag(Integer id) throws Exception {
        Tag existingTag = tagRepository.findById(id).orElseThrow(() -> new Exception("Tag with id " + id + " not found"));
        List<Blog> matchingBlogs = existingTag.getBlogs();
        for (Blog blog : matchingBlogs) {
            blog.getTags().remove(existingTag); //ensure deleted tag does not exist on blog
        }
        tagRepository.delete(existingTag);
    }

    //additional functionalities
    //get tags by name
    public Tag getTagByName(String name) throws Exception {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be a blank or empty string");
        }
        return tagRepository.findByName(name.toLowerCase()); //acceptable to return null response if no result found, no exception needed
    }


    //get blogs by tag id
    public List<Blog> getBlogsByTag(Integer id) throws Exception {
        Tag existingTag = tagRepository.findById(id).orElseThrow(() -> new Exception("Tag with id " + id + " not found"));

        return existingTag.getBlogs();
    }

}
