package com.example.Flower.repository;

import com.example.Flower.entity.CMPicture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CMPictureRepository extends CrudRepository<CMPicture, Long> {
    // 사진 데이터를 관리하는 리포지토리
}
