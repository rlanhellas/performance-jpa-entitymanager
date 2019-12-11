package br.com.lanhellas.performancejpaentitymanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerRestController {

    private final CustomerRepository repository;
    private final EntityManager entityManager;

    private List<Customer> customerListDetached = new ArrayList<>();

    private final int ROWS = 100000;

    public CustomerRestController(CustomerRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @GetMapping("/save-with-entitymanager")
    @Transactional
    public long saveWithEntityManager() {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            Customer customer = createCustomer();
            customerList.add(customer);
            entityManager.persist(customer);
        }

        entityManager.flush();
        loadDetached(customerList);
        return repository.count();
    }

    @GetMapping("/save-with-jpa")
    public long saveWithJpa() {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < ROWS; i++) {
            Customer customer = createCustomer();
            customerList.add(customer);
            repository.save(customer);
        }

        repository.flush();
        loadDetached(customerList);
        return repository.count();
    }

    @GetMapping("/delete-with-jpa")
    public long deleteWithJpa() {

        for (Customer customer : customerListDetached) {
            repository.delete(customer);
        }

        return repository.count();
    }

    @GetMapping("/delete-with-entitymanager")
    @Transactional
    public long deleteWithEntityManager() {

        for (Customer customer : customerListDetached) {
            Query q = entityManager.createQuery("DELETE FROM Customer c WHERE c.id = :id");
            q.setParameter("id", customer.getId());
            q.executeUpdate();
        }

        return repository.count();
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setAge(10);
        customer.setName("RONALDO");
        return customer;
    }

    private void loadDetached(List<Customer> customerList) {
        customerListDetached.clear();
        customerListDetached.addAll(customerList);
        entityManager.clear();
    }

}
