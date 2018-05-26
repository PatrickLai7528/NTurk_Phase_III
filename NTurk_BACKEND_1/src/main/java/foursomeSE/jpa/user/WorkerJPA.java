package foursomeSE.jpa.user;

import foursomeSE.entity.user.Worker;

import javax.transaction.Transactional;

@Transactional
public interface WorkerJPA extends UserJPA<Worker> {

}
