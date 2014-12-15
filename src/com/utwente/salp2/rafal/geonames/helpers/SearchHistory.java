package com.utwente.salp2.rafal.geonames.helpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafal on 13.12.14.
 *
 * Class that holds last k values. They are searched according to the keys.
 */
public class SearchHistory<K, V>
{
   private int historySize;
   private Map<K, V> historyMap;
   private Deque<K> historyElements;

   public SearchHistory(int historySize)
   {
      this.historySize = historySize;
      historyMap = new HashMap<>();
      historyElements = new ArrayDeque<>(historySize);
   }

   public V search(K key)
   {
      if (historyElements.contains(key))
      {
         historyElements.remove(key);
         historyElements.addFirst(key);
         return historyMap.get(key);
      }
      else
         return null;
   }

   public void addToHistory(K key, V value)
   {
      if (historyElements.contains(key))
         return;

      if (historyElements.size() == historySize)
      {
         K lastKey = historyElements.removeLast();
         historyMap.remove(lastKey);
      }

      historyElements.addFirst(key);
      historyMap.put(key, value);
   }

   public boolean isInHistory(K key)
   {
      return historyElements.contains(key);
   }
}