package org.cts.registry;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class parses the world file available in the resources package.
 * For a given code, it returns a map of parameters required by
 * {@link org.cts.CRSHelper} to build a {@link org.cts.crs.CoordinateReferenceSystem}.
 *
 * @author Jules Party
 */
public class WorldRegistry extends AbstractProjRegistry {

    /**
     * The regex used to parse world registry.
     */
    static final Pattern WORLD_REGEX = Pattern.compile("\\s+");

    @Override
    public String getRegistryName() {
        return "world";
    }

    @Override
    public Map<String, String> getParameters(String code) throws RegistryException {
        try {
            Map<String, String> crsParameters = projParser.readParameters(code, WORLD_REGEX);
            return crsParameters;
        } catch (IOException ex) {
            throw new RegistryException("Cannot load the world registry", ex);
        }
    }

    @Override
    public Set<String> getSupportedCodes() throws RegistryException {
        try {
            return projParser.getSupportedCodes(WORLD_REGEX);
        } catch (IOException ex) {
            throw new RegistryException("Cannot load the world registry", ex);
        }
    }
}