package org.pechblenda.festusinvitationrest.repository

import org.pechblenda.festusinvitationrest.entity.Team

import org.springframework.data.jpa.repository.JpaRepository

import java.util.UUID

interface ITeamRepository: JpaRepository<Team, UUID>
