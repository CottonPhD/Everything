package com.ptc.ptcnet.everything;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;


@ThingworxPropertyDefinitions(properties = {
        @ThingworxPropertyDefinition(name="Accelerometer", description = "Geschwindigkeit", baseType = "NUMBER", category = "", aspects = {"isReadOnlyTrue"}),
        @ThingworxPropertyDefinition(name="MagneticField", description = "Kompass", baseType = "NUMBER", category = "", aspects = {"isReadOnlyTrue"}),
        @ThingworxPropertyDefinition(name="Light", description = "Licht in Lux", baseType = "NUMBER", category = "", aspects = {"isReadOnlyTrue"}),
        @ThingworxPropertyDefinition(name="Gyroscope", description = "Gyroscope", baseType = "NUMBER", category = "", aspects = {"isReadOnlyTrue"})
})
public class AndroidThing extends VirtualThing {

    public AndroidThing(String name, String description, ConnectedThingClient client) throws Exception {
        super(name, description, client);

        // Copy all the default values from the aspects of the properties defined above to this
        // instance. This gives it an initial state but does not push it to the server.
        initializeFromAnnotations();

    }


}
