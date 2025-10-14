package com.cooperation.project.cooperationcenter.domain.member.repository;

import com.cooperation.project.cooperationcenter.domain.member.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog,Long> {


}
