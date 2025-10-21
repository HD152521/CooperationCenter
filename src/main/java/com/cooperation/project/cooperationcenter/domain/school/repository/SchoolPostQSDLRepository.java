package com.cooperation.project.cooperationcenter.domain.school.repository;

import com.cooperation.project.cooperationcenter.domain.school.model.SchoolPost;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SchoolPostQSDLRepository{
    SchoolPost findBeforePost(SchoolPost currentPost);
    SchoolPost findAfterPost(SchoolPost currentPost);
}
