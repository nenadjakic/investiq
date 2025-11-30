package com.github.nenadjakic.investiq.data.repository

import com.github.nenadjakic.investiq.data.entity.Action
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActionRepository: JpaRepository<Action, UUID>