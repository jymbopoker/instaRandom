package com.example.instagramRandom.repos;

import com.example.instagramRandom.domains.InstaPhoto;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InstaPhotoRepo extends CrudRepository<InstaPhoto, Long> {
    List<InstaPhoto> findByShortcode(String shortcode);
}
