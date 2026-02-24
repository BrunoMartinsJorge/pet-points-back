package br.com.api.petpoints.shared.repository;

import br.com.api.petpoints.shared.models.LogsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogsRepository extends JpaRepository<LogsModel, Long> {
}
