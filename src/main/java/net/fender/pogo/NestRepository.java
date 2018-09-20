package net.fender.pogo;

import org.springframework.data.repository.CrudRepository;

public interface NestRepository extends CrudRepository<Nest, String> {
    // magic
}
