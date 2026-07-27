package br.com.api.petpoints.shared.features.payment.repository;

import br.com.api.petpoints.shared.features.payment.model.CobrancaPix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CobrancaPixRepository extends JpaRepository<CobrancaPix, String> {
    Optional<CobrancaPix> findByGatewayId(String gatewayId);
}
