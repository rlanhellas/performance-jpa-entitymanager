package br.com.lanhellas.performancejpaentitymanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerRestController {

    private final CustomerRepository repository;
    private final EntityManager entityManager;

    private final int ROWS = 1000000;

    public CustomerRestController(CustomerRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @GetMapping("/save-with-entitymanager")
    @Transactional
    public long saveWithEntityManager() {

        for (int i = 0; i < ROWS; i++) {
            entityManager.persist(createCustomer());
        }

        return repository.count();
    }

    @GetMapping("/save-with-jpa")
    public long saveWithJpa() {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            customerList.add(createCustomer());
        }

        repository.saveAll(customerList);
        return repository.count();
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setAge(10);
        customer.setName("RONALDO");
        return customer;
    }

}
