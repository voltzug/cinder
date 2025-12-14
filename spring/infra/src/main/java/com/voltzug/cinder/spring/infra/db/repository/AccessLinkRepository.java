package com.voltzug.cinder.spring.infra.db.repository;

import com.voltzug.cinder.spring.infra.db.entity.AccessLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLinkRepository
  extends JpaRepository<AccessLinkEntity, String> {}
