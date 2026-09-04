package de.thb.fbi.msr.maus.einkaufsliste.remote;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;
import de.thb.fbi.msr.maus.einkaufsliste.model.TodoCRUDAccessor;
import de.thb.fbi.msr.maus.einkaufsliste.model.UserCheck;
import org.apache.log4j.Logger;

@ApplicationPath(value="/rest/*")
public class RemoteDataAccessRESTService extends javax.ws.rs.core.Application {

	protected static Logger logger = Logger.getLogger(RemoteDataAccessRESTService.class);
	
	public RemoteDataAccessRESTService() {
		logger.info("<Constructor>()");
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set = new HashSet<>();
		set.add(TodoCRUDAccessor.class);
		set.add(UserCheck.class);
		return set;
	}
}
