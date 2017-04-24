package cz.zcu.kiv.spade.dao;

import cz.zcu.kiv.spade.domain.Person;

public interface PersonDAO extends GenericDAO<Person> {

    Person save(Person person);

}
