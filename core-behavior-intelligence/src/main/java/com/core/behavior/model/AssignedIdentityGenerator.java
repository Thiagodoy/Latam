package com.core.behavior.model;

import java.io.Serializable;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;

/**
 *
 * @author Thiago H. Godoy <thiagodoy@hotmail.com>
 */
public class AssignedIdentityGenerator
    extends IdentityGenerator {
 
    
    public Serializable generate(SessionImplementor session,
        Object obj) {
        if(obj instanceof Identifiable) {
            Identifiable identifiable = (Identifiable) obj;
            Serializable id = identifiable.getId();
            if(id != null) {
                return id;
            }
        }
        return super.generate(session, obj);
    }
}
