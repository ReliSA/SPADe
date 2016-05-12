package cz.zcu.kiv.ppicha.spade.indao;

import cz.zcu.kiv.ppicha.spade.domain.Person;

public interface PersonDAO extends GenericDAO<Person> {

    Person save(Person person);

}
