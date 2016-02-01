package com.ptc.ptcnet.everything;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;

/**
 * Created by pdahmen on 01.02.2016.
 */
public class AndroidThing extends VirtualThing {

    public AndroidThing(String name, String description, ConnectedThingClient client) throws Exception {
        super(name, description, client);

        // Copy all the default values from the aspects of the properties defined above to this
        // instance. This gives it an initial state but does not push it to the server.
        initializeFromAnnotations();

    }

}
