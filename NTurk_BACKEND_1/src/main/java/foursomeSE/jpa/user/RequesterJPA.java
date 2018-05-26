package foursomeSE.jpa.user;

import foursomeSE.entity.user.Requester;

import javax.transaction.Transactional;

@Transactional
public interface RequesterJPA extends UserJPA<Requester> {
}
