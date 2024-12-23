package ru.antoncharov.socks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.antoncharov.socks.domain.Socks;

import java.util.Optional;

public interface SocksRepository extends JpaRepository<Socks, Long>, JpaSpecificationExecutor<Socks> {
    Optional<Socks> findFirstByColorAndCottonPercentage(String color, int cottonPercentage);
}
