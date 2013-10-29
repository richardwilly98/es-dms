package com.github.richardwilly98.esdms;

/*
 * #%L
 * es-dms-core
 * %%
 * Copyright (C) 2013 es-dms
 * %%
 * Copyright 2012-2013 Richard Louapre
 * 
 * This file is part of ES-DMS.
 * 
 * The current version of ES-DMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * ES-DMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import static com.google.common.collect.Sets.newHashSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrackerHashMap<K, V> extends HashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 7245821598577539544L;

    private final Set<K> removedKeys = newHashSet();
    
    @Override
    public V put(K key, V value) {
        removedKeys.remove(key);
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        removedKeys.removeAll(m.keySet());
        super.putAll(m);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        removedKeys.add((K)key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        removedKeys.addAll(super.keySet());
        super.clear();
    }

    public Set<K> getMissingKey() {
        return removedKeys;
    }

}
