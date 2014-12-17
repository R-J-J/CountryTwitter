package com.utwente.salp2.rafal.geonames;

import java.util.Map;
import java.util.Set;

/**
 * Created by rafal on 16.12.14.
 */
public interface GeoNamesSearcher
{
   public Map<String, Map<String, Integer>> search(final Set<String> toSearch)
           throws Exception;
}
