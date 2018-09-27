package org.apache.ctakes.utils.struct;

import org.apache.uima.jcas.cas.TOP;

import java.util.HashMap;
import java.util.Map;

public class MapFactory {
  private static Map<String, Map<?,?>> mapIndex = new HashMap<>();

  public static <K extends TOP,V extends TOP> V get(String mapId, K key){
    Map<?,?> map = mapIndex.get(mapId);
    if ( map != null ) {
      return (V)map.get( key );
    }
    return null;
  }
  
  public static <K extends TOP,V extends TOP> void put(String mapId, K key, V value){
    Map<K,V> map = (Map<K,V>) mapIndex.get(mapId);
    map.put(key, value);
  }

  public static <K extends TOP,V extends TOP> Map<K, V> createInstance(String mapId){
    Map<K,V> map = new HashMap<>();
    mapIndex.put(mapId, map);
    return map;
  }

}
