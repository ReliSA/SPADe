package cz.zcu.kiv.spade.indao.jpa;

import cz.zcu.kiv.spade.domain.Person;
import cz.zcu.kiv.spade.indao.PersonDAO;

import javax.persistence.EntityManager;

public class PersonDAO_JPA extends GenericDAO_JPA<Person> implements PersonDAO {

    public PersonDAO_JPA(EntityManager em) {
        super(em, Person.class);
    }

    public Person save(Person person) {
        entityManager.getTransaction().begin();

        Person ret;
        if (person.getId() == 0) {
            entityManager.persist(person);
            ret = person;
        } else {
            ret = entityManager.merge(person);
        }

        entityManager.getTransaction().commit();

        return ret;
    }
}
